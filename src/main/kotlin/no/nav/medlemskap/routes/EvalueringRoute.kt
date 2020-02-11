package no.nav.medlemskap.routes

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import mu.KotlinLogging
import no.nav.medlemskap.common.API_COUNTER
import no.nav.medlemskap.domene.Brukerinput
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.modell.Request
import no.nav.medlemskap.modell.Response
import no.nav.medlemskap.regler.common.Fakta
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.v1.RegelsettForMedlemskap
import no.nav.medlemskap.services.Services
import java.time.LocalDate
import java.time.LocalDateTime

private val logger = KotlinLogging.logger { }

fun Routing.evalueringRoute(services: Services, useAuthentication: Boolean) {
    fun receiveAndRespond() {
        post("/") {
            API_COUNTER.inc()
            val request = call.receive<Request>()
            val aktorId = services.pdlService.hentAktorId(request.fnr)
            val datagrunnlag = createDatagrunnlag(
                    fnr = request.fnr,
                    aktoer = aktorId,
                    soknadsperiodeStart = request.soknadsperiodeStart,
                    soknadsperiodeSlutt = request.soknadsperiodeSlutt,
                    soknadstidspunkt = request.soknadstidspunkt,
                    brukerinput = request.brukerinput,
                    services = services)
            val resultat = evaluerData(datagrunnlag)
            val response = Response(
                    tidspunkt = LocalDateTime.now(),
                    versjonRegler = "v1",
                    versjonTjeneste = "TODO",
                    datagrunnlag = datagrunnlag,
                    resultat = resultat
            )

            call.respond(response)
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
        aktoer: String,
        soknadsperiodeStart: LocalDate,
        soknadsperiodeSlutt: LocalDate,
        soknadstidspunkt: LocalDate,
        brukerinput: Brukerinput,
        services: Services): Datagrunnlag = coroutineScope {

    val historikkFraTpsRequest = async { services.personService.personhistorikk(fnr) }
    val medlemskapsunntakRequest = async { services.medlService.hentMedlemskapsunntak(fnr) }
    val arbeidsforholdRequest = async { services.aaRegService.hentArbeidsforhold(fnr) }
    val inntektListeRequest = async { services.inntektService.hentInntektListe(fnr, soknadsperiodeStart, soknadsperiodeSlutt) }
    val journalPosterRequest = async { services.safService.hentJournaldata(fnr) }
    val gosysOppgaver = async { services.oppgaveService.hentOppgaver(aktoer) }


    val historikkFraTps = historikkFraTpsRequest.await()
    val medlemskapsunntak = medlemskapsunntakRequest.await()
    val arbeidsforhold = arbeidsforholdRequest.await()
    val inntektListe = inntektListeRequest.await()
    val journalPoster = journalPosterRequest.await()
    val oppgaver = gosysOppgaver.await()

    Datagrunnlag(
            soknadsperiode = Periode(fom = soknadsperiodeStart, tom = soknadsperiodeSlutt),
            soknadstidspunkt = soknadstidspunkt,
            brukerinput = brukerinput,
            personhistorikk = historikkFraTps,
            medlemskapsunntak = medlemskapsunntak,
            arbeidsforhold = arbeidsforhold,
            inntekt = inntektListe,
            oppgaver = oppgaver,
            dokument = journalPoster
    )
}

private fun evaluerData(datagrunnlag: Datagrunnlag): Resultat =
        RegelsettForMedlemskap(Fakta.initialiserFakta(datagrunnlag)).evaluer()
