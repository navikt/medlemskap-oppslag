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
import no.nav.medlemskap.configuration
import no.nav.medlemskap.domene.Brukerinput
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Inntekt
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.modell.Request
import no.nav.medlemskap.modell.Resultat
import no.nav.medlemskap.modell.aareg.mapAaregResultat
import no.nav.medlemskap.modell.medl.mapMedlemskapResultat
import no.nav.medlemskap.modell.oppgave.mapOppgaveResultat
import no.nav.medlemskap.modell.saf.mapJournalResultat
import no.nav.medlemskap.services.Services.aaRegClient
import no.nav.medlemskap.services.Services.inntektClient
import no.nav.medlemskap.services.Services.medlClient
import no.nav.medlemskap.services.Services.oppgaveClient
import no.nav.medlemskap.services.Services.personService
import no.nav.medlemskap.services.Services.safClient
import no.nav.medlemskap.services.inntekt.mapInntektResultat
import no.nav.medlemskap.services.tpsws.mapPersonhistorikkResultat
import no.nav.nare.core.evaluations.Evaluering
import java.time.LocalDate

private val logger = KotlinLogging.logger { }

fun Routing.evalueringRoute() {
    authenticate {
        post("/") {
            API_COUNTER.inc()
            val request = call.receive<Request>()
            val datagrunnlag = createDatagrunnlag(request.fnr, request.soknadsperiodeStart, request.soknadsperiodeSlutt, request.soknadstidspunkt, request.brukerinput)
            call.respond(evaluerData(datagrunnlag))
        }
    }
}

private suspend fun createDatagrunnlag(
        fnr: String,
        soknadsperiodeStart: LocalDate,
        soknadsperiodeSlutt: LocalDate,
        soknadstidspunkt: LocalDate,
        brukerinput: Brukerinput): Datagrunnlag = coroutineScope {

    val historikkFraTpsRequest = async { personService.personhistorikk(fnr) }
    val medlemskapsunntakRequest = async { medlClient.hentMedlemskapsunntak(fnr) }
    val arbeidsforholdRequest = async { aaRegClient.hentArbeidsforhold(fnr) }
    val inntektListeRequest = async { inntektClient.hentInntektListe(fnr, soknadsperiodeStart, soknadsperiodeSlutt) }
    val journalPosterRequest = async { safClient.hentJournaldata(fnr) }
    val gosysOppgaver = async { oppgaveClient.hentOppgaver(fnr) }

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

    Datagrunnlag(
            soknadsperiode = Periode(fom = soknadsperiodeStart, tom = soknadsperiodeSlutt),
            soknadstidspunkt = soknadstidspunkt,
            brukerinput = brukerinput,
            personhistorikk = mapPersonhistorikkResultat(historikkFraTps),
            medlemskapsunntak = mapMedlemskapResultat(medlemskapsunntak),
            arbeidsforhold = mapAaregResultat(arbeidsforhold),
            inntekt = mapInntektResultat(inntektListe),
            oppgaver = mapOppgaveResultat(oppgaver.oppgaver),
            dokument = mapJournalResultat(journalPoster.data.dokumentoversiktBruker.journalposter)


            )


}

private fun evaluerData(datagrunnlag: Datagrunnlag): Resultat = runBlocking {
    defaultHttpClient.post<Resultat> {
        url(configuration.reglerUrl)
        contentType(ContentType.Application.Json)
        body = datagrunnlag
    }
}
