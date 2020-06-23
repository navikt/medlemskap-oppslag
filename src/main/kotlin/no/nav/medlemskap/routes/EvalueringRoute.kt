package no.nav.medlemskap.routes

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTPrincipal
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
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.v1.Hovedregler
import no.nav.medlemskap.services.Services
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger { }

private val secureLogger = KotlinLogging.logger("tjenestekall")


fun Routing.evalueringRoute(
        services: Services,
        useAuthentication: Boolean,
        configuration: Configuration) {

    if (useAuthentication) {
        logger.info("autentiserer kallet")
        authenticate {
            post("/") {
                apiCounter().increment()
                val callerPrincipal: JWTPrincipal? = call.authentication.principal()
                val subject = callerPrincipal?.payload?.subject ?: "ukjent"
                secureLogger.info("Mottar principal {} med subject {}", callerPrincipal, subject)
                val azp = callerPrincipal?.payload?.getClaim("azp")?.asString() ?: "ukjent"
                secureLogger.info("EvalueringRoute: azp-claim i principal-token:", azp)
                val claims = callerPrincipal?.payload?.claims?.toString() ?: "ukjent"
                secureLogger.info("EvalueringRoute: alle claims:", claims)
                
                val request = validerRequest(call.receive())
                val callId = call.callId ?: UUID.randomUUID().toString()
                //konsumentCounter(subject).increment()

                val datagrunnlag = createDatagrunnlag(
                        fnr = request.fnr,
                        callId = callId,
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
                secureLogger.info("{} konklusjon gitt for bruker {} på regel {}", resultat.svar.name, request.fnr, resultat.sisteRegel())
                secureLogger.info("For bruker {} er responsen {}", request.fnr, response)

                call.respond(response)
            }
        }
    } else {
        logger.info("autentiserer IKKE kallet")
        post("/") {
            apiCounter().increment()
            val callerPrincipal: JWTPrincipal? = call.authentication.principal()
            val subject = callerPrincipal?.payload?.subject ?: "ukjent"
            secureLogger.info("Mottar principal {} med subject {}", callerPrincipal, subject)
            val azp = callerPrincipal?.payload?.getClaim("azp")?.asString() ?: "ukjent"
            secureLogger.info("EvalueringRoute: azp-claim i principal-token:", azp)
            val request = validerRequest(call.receive())
            val callId = call.callId ?: UUID.randomUUID().toString()
            //konsumentCounter(subject).increment()

            val datagrunnlag = createDatagrunnlag(
                    fnr = request.fnr,
                    callId = callId,
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
            secureLogger.info("{} konklusjon gitt for bruker {} på regel {}", resultat.svar.name, request.fnr, resultat.sisteRegel())
            secureLogger.info("For bruker {} er responsen {}", request.fnr, response)

            call.respond(response)
        }
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
        throw BadRequestException("Ugyldig fødselsnummer")
    }

    return request
}

private suspend fun createDatagrunnlag(
        fnr: String,
        callId: String,
        periode: InputPeriode,
        brukerinput: Brukerinput,
        services: Services): Datagrunnlag = coroutineScope {

    val aktorIder = services.pdlService.hentAlleAktorIder(fnr, callId)
    // val pdlHistorikkRequest = async { services.pdlService.hentPersonHistorikk(fnr, callId) }
    val historikkFraTpsRequest = async { services.personService.personhistorikk(fnr, periode.fom) }
    val medlemskapsunntakRequest = async { services.medlService.hentMedlemskapsunntak(fnr, callId) }
    val arbeidsforholdRequest = async { services.aaRegService.hentArbeidsforhold(fnr, callId, fraOgMedDatoForArbeidsforhold(periode), periode.tom) }
    val journalPosterRequest = async { services.safService.hentJournaldata(fnr, callId) }
    val gosysOppgaver = async { services.oppgaveService.hentOppgaver(aktorIder, callId) }

    //  val pdlHistorikk = pdlHistorikkRequest.await()
    val historikkFraTps = historikkFraTpsRequest.await()
    val medlemskap = medlemskapsunntakRequest.await()
    val arbeidsforhold = arbeidsforholdRequest.await()
    val journalPoster = journalPosterRequest.await()
    val oppgaver = gosysOppgaver.await()


    //  logger.info { pdlHistorikk }


    Datagrunnlag(
            periode = periode,
            brukerinput = brukerinput,
            personhistorikk = historikkFraTps,
            medlemskap = medlemskap,
            arbeidsforhold = arbeidsforhold,
            oppgaver = oppgaver,
            dokument = journalPoster
    )


}

private fun fraOgMedDatoForArbeidsforhold(periode: InputPeriode) = periode.fom.minusYears(1).minusDays(1)

private fun evaluerData(datagrunnlag: Datagrunnlag): Resultat =
        Hovedregler(datagrunnlag).kjørHovedregler()

private fun Resultat.sisteRegel() =
        if (this.delresultat.isEmpty()) {
            this
        } else {
            this.delresultat.last()
        }
