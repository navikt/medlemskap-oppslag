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
import mu.KotlinLogging
import no.nav.medlemskap.common.apiCounter
import no.nav.medlemskap.common.exceptions.KonsumentIkkeFunnet
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
        configuration: Configuration,
        createDatagrunnlag: suspend (fnr: String, callId: String, periode: InputPeriode, brukerinput: Brukerinput, services: Services, clientId: String?, ytelseFraRequest: Ytelse?) -> Datagrunnlag) {

    authenticate("azureAuth") {
        post("/") {
            apiCounter().increment()

            val callerPrincipal: JWTPrincipal = call.authentication.principal()!!
            val azp = callerPrincipal.payload.getClaim("azp").asString()
            secureLogger.info("EvalueringRoute: azp-claim i principal-token: {}", azp)

            val request = validerRequest(call.receive())
            val callId = call.callId ?: UUID.randomUUID().toString()

            val datagrunnlag = createDatagrunnlag.invoke(
                    request.fnr,
                    callId,
                    request.periode,
                    request.brukerinput,
                    services,
                    azp,
                    request.ytelse)
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

fun Routing.evalueringTestRoute(
        services: Services,
        configuration: Configuration,
        createDatagrunnlag: suspend (fnr: String, callId: String, periode: InputPeriode, brukerinput: Brukerinput, services: Services, clientId: String?, ytelseFraRequest: Ytelse?) -> Datagrunnlag) {
    logger.info("autentiserer IKKE kallet")
    post("/") {
        apiCounter().increment()
        val request = validerRequest(call.receive())
        val callId = call.callId ?: UUID.randomUUID().toString()

        val datagrunnlag = createDatagrunnlag.invoke(
                request.fnr,
                callId,
                request.periode,
                request.brukerinput,
                services,
                null,
                request.ytelse)
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


fun finnYtelse(ytelseFraRequest: Ytelse?, clientId: String?) =
        (ytelseFraRequest ?: Ytelse.fromClientId(clientId))
                ?: throw KonsumentIkkeFunnet("Fant ikke clientId i mapping til ytelse. Ta kontakt med medlemskap-teamet for tilgang til tjenesten.")


private fun evaluerData(datagrunnlag: Datagrunnlag): Resultat =
        Hovedregler(datagrunnlag).kjørHovedregler()

private fun Resultat.sisteRegel() =
        if (this.delresultat.isEmpty()) {
            this
        } else {
            this.delresultat.last()
        }

