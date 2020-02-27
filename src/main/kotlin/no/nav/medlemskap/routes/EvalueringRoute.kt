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
import no.nav.medlemskap.common.API_COUNTER
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.v1.RegelsettForMedlemskap
import no.nav.medlemskap.services.Services
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

fun Routing.evalueringRoute(
        services: Services,
        useAuthentication: Boolean,
        configuration: Configuration) {
    fun receiveAndRespond() {
        post("/") {
            API_COUNTER.inc()
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

private fun gyldigFnr(fnr: String): Boolean {
    if (fnr.length != 11) {
        return false
    }

    val s = fnr.toCharArray().map { it.toInt() }
    val k1 = 11 - ((3 * s[0] + 7 * s[1] + 6 * s[2] + 1 * s[3] + 8 * s[4] + 9 * s[5] + 4 * s[6] + 5 * s[7] + 2 * s[8]) % 11)
    val k2 = 11 - ((5 * s[0] + 4 * s[1] + 3 * s[2] + 2 * s[3] + 7 * s[4] + 6 * s[5] + 5 * s[6] + 4 * s[7] + 3 * s[8] + 2 * k1) % 11)

    return k1 == s[9] && k2 == s[10]
}

private suspend fun createDatagrunnlag(
        fnr: String,
        callId: String,
        aktoer: String,
        periode: InputPeriode,
        brukerinput: Brukerinput,
        services: Services): Datagrunnlag = coroutineScope {

    val historikkFraTpsRequest = async { services.personService.personhistorikk(fnr) }
    val medlemskapsunntakRequest = async { services.medlService.hentMedlemskapsunntak(fnr, callId) }
    val arbeidsforholdRequest = async { services.aaRegService.hentArbeidsforhold(fnr, callId) }
    val inntektListeRequest = async { services.inntektService.hentInntektListe(fnr, callId, periode.fom, periode.tom) }
    val journalPosterRequest = async { services.safService.hentJournaldata(fnr, callId) }
    val gosysOppgaver = async { services.oppgaveService.hentOppgaver(aktoer, callId) }


    val historikkFraTps = historikkFraTpsRequest.await()
    val medlemskapsunntak = medlemskapsunntakRequest.await()
    val arbeidsforhold = arbeidsforholdRequest.await()
    val inntektListe = inntektListeRequest.await()
    val journalPoster = journalPosterRequest.await()
    val oppgaver = gosysOppgaver.await()

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
