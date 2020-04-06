package no.nav.medlemskap.routes

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.features.BadRequestException
import io.ktor.features.callId
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import mu.KotlinLogging
import no.nav.medlemskap.common.apiCounter
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.v1.RegelsettForMedlemskap
import no.nav.medlemskap.services.Services
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger { }


fun Routing.evalueringRoute(
        services: Services,
        useAuthentication: Boolean,
        configuration: Configuration) {

    fun receiveAndRespond() {
        post("/") {
            apiCounter().increment()
            val request = validerRequest(call.receive())
            val callId = call.callId ?: UUID.randomUUID().toString()
            val aktorId = services.pdlService.hentAktorId(request.fnr, callId)
            val datagrunnlag = createDatagrunnlag(
                    fnr = request.fnr,
                    callId = callId,
                    aktoer = aktorId,
                    periode = request.periode,
                    brukerinput = request.brukerinput,
                    services = services)
            val resultat = evaluerData(datagrunnlag)
            val response = Response(
                    tidspunkt = LocalDateTime.now(),
                    versjonRegler = "v1",
                    versjonTjeneste = configuration.commitSha,
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

private fun validerRequest(request: Request): Request {
    if (request.periode.tom.isBefore(request.periode.fom)) {
        throw BadRequestException("Periode tom kan ikke være før periode fom")
    }

    if (request.periode.fom.isBefore(LocalDate.of(2016, 1, 1))) {
        throw BadRequestException("Periode fom kan ikke være før 2016-01-01")
    }

    if (!gyldigFnr(request.fnr)) {
        throw BadRequestException("Ugyldig fødslesnummer")
    }

    return request
}

private suspend fun createDatagrunnlag(
        fnr: String,
        callId: String,
        aktoer: String,
        periode: InputPeriode,
        brukerinput: Brukerinput,
        services: Services): Datagrunnlag = coroutineScope {


    val pdlHistorikkRequest = async { services.pdlService.hentPersonHistorikk(fnr, callId) }
    val historikkFraTpsRequest = async { services.personService.personhistorikk(fnr, periode.fom) }
    val medlemskapsunntakRequest = async { services.medlService.hentMedlemskapsunntak(fnr, callId) }
    val arbeidsforholdRequest = async { services.aaRegService.hentArbeidsforhold(fnr, callId, periode.fom, periode.tom) }
    val inntektListeRequest = async { services.inntektService.hentInntektListe(fnr, callId, periode.fom, periode.tom) }
    val journalPosterRequest = async { services.safService.hentJournaldata(fnr, callId) }
    val gosysOppgaver = async { services.oppgaveService.hentOppgaver(aktoer, callId) }

    val pdlHistorikk = pdlHistorikkRequest.await()
    val historikkFraTps = historikkFraTpsRequest.await()
    val medlemskapsunntak = medlemskapsunntakRequest.await()
    val arbeidsforhold = arbeidsforholdRequest.await()
    val inntektListe = inntektListeRequest.await()
    val journalPoster = journalPosterRequest.await()
    val oppgaver = gosysOppgaver.await()


    logger.apply { pdlHistorikk }


    Datagrunnlag(
            periode = periode,
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
        RegelsettForMedlemskap().evaluer(Personfakta.initialiserFakta(datagrunnlag))
