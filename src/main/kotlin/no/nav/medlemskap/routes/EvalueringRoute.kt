package no.nav.medlemskap.routes

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.medlemskap.common.API_COUNTER
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Regelavklaring
import no.nav.medlemskap.modell.Request
import no.nav.medlemskap.services.Services
import no.nav.medlemskap.services.tpsws.mapPersonhistorikkResultat
import no.nav.nare.core.evaluations.Evaluering
import java.time.LocalDate

private val logger = KotlinLogging.logger { }

fun Routing.evalueringRoute(services: Services, useAuthentication: Boolean) {
    fun receiveAndRespond() {
        post("/") {
            API_COUNTER.inc()
            val request = call.receive<Request>()
            val datagrunnlag = createDatagrunnlag(request.fnr, request.soknadsperiodeStart, request.soknadsperiodeSlutt, request.soknadstidspunkt, services)
            // call.respond(Resultat(datagrunnlag, evaluerData(datagrunnlag, configuration))) DISABLER TIL DENNE ER FIKSET FOR NY DATA
            call.respond(datagrunnlag)
        }
    }

    if (useAuthentication) {
        authenticate {
            receiveAndRespond()
        }
    } else {
        receiveAndRespond()
    }
}

private suspend fun createDatagrunnlag(
        fnr: String,
        soknadsperiodeStart: LocalDate,
        soknadsperiodeSlutt: LocalDate,
        soknadstidspunkt: LocalDate,
        services: Services): Regelavklaring = coroutineScope {

    val historikkFraTpsRequest = async { services.personService.personhistorikk(fnr) }
    val medlemskapsunntakRequest = async { services.medlClient.hentMedlemskapsunntak(fnr) }
    val arbeidsforholdRequest = async { services.aaRegClient.hentArbeidsforhold(fnr) }
    val inntektListeRequest = async { services.inntektClient.hentInntektListe(fnr, soknadsperiodeStart, soknadsperiodeSlutt) }
    val journalPosterRequest = async { services.safClient.hentJournaldata(fnr) }
    val gosysOppgaver = async { services.oppgaveClient.hentOppgaver(fnr) }

    val historikkFraTps = historikkFraTpsRequest.await()
    val medlemskapsunntak = medlemskapsunntakRequest.await()
    val arbeidsforhold = arbeidsforholdRequest.await()
    val inntektListe = inntektListeRequest.await()
    val journalPoster = journalPosterRequest.await()
    val oppgaver = gosysOppgaver.await()

    // En test for å se på data:
    logger.info { medlemskapsunntak }
    logger.info { arbeidsforhold }
    logger.info { inntektListe }
    logger.info { journalPoster }
    logger.info { oppgaver }

    Regelavklaring(
            soknadsperiode = Periode(fom = soknadsperiodeStart, tom = soknadsperiodeSlutt),
            soknadstidspunkt = soknadstidspunkt,
            personhistorikk = mapPersonhistorikkResultat(historikkFraTps))
}

private fun evaluerData(regelavklaring: Regelavklaring, configuration: Configuration): Evaluering = runBlocking {
    defaultHttpClient.post<Evaluering> {
        url(configuration.reglerUrl)
        contentType(ContentType.Application.Json)
        body = regelavklaring
    }
}
