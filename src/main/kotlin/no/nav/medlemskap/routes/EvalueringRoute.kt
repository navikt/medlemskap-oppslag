package no.nav.medlemskap.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import mu.KotlinLogging
import net.logstash.logback.argument.StructuredArguments.kv
import net.logstash.logback.marker.Markers.append
import no.nav.medlemskap.clients.Services
import no.nav.medlemskap.common.apiCounter
import no.nav.medlemskap.common.exceptions.KonsumentIkkeFunnet
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.common.uavklartPåRegel
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Request
import no.nav.medlemskap.domene.Response
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.Ytelse.Companion.metricName
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.v1.Hovedregler
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger { }

private val secureLogger = KotlinLogging.logger("tjenestekall")

fun Routing.evalueringRoute(
    services: Services,
    configuration: Configuration,
    createDatagrunnlag: suspend (request: Request, callId: String, services: Services, clientId: String?) -> Datagrunnlag
) {

    authenticate("azureAuth") {
        post("/") {
            apiCounter().increment()

            val callerPrincipal: JWTPrincipal = call.authentication.principal()!!
            val azp = callerPrincipal.payload.getClaim("azp").asString()
            secureLogger.info("EvalueringRoute: azp-claim i principal-token: {}", azp)

            val request = validerRequest(call.receive())
            val callId = call.callId ?: UUID.randomUUID().toString()

            val datagrunnlag = createDatagrunnlag.invoke(
                request,
                callId,
                services,
                azp
            )
            val resultat = evaluerData(datagrunnlag)

            val response = lagResponse(
                versjonTjeneste = configuration.commitSha,
                datagrunnlag = datagrunnlag,
                resultat = resultat
            )

            loggResponse(request.fnr, response)

            call.respond(response)
        }
    }
}

fun Routing.evalueringTestRoute(
    services: Services,
    configuration: Configuration,
    createDatagrunnlag: suspend (request: Request, callId: String, services: Services, clientId: String?) -> Datagrunnlag
) {
    logger.info("autentiserer IKKE kallet")
    post("/") {
        apiCounter().increment()
        val request = validerRequest(call.receive())
        val callId = call.callId ?: UUID.randomUUID().toString()

        val datagrunnlag = createDatagrunnlag.invoke(
            request,
            callId,
            services,
            null
        )
        val resultat = evaluerData(datagrunnlag)

        val response = lagResponse(
            versjonTjeneste = configuration.commitSha,
            datagrunnlag = datagrunnlag,
            resultat = resultat
        )

        loggResponse(request.fnr, response)

        call.respond(response)
    }
}

private fun lagResponse(datagrunnlag: Datagrunnlag, resultat: Resultat, versjonTjeneste: String): Response {
    return Response(
        tidspunkt = LocalDateTime.now(),
        versjonRegler = "v1",
        versjonTjeneste = versjonTjeneste,
        datagrunnlag = datagrunnlag,
        resultat = resultat
    )
}

private fun loggResponse(fnr: String, response: Response) {
    val resultat = response.resultat
    val årsaker = resultat.årsaker
    val årsakerSomRegelIdStr = årsaker.map { it.regelId.toString() }
    val årsak = årsaker.map { it.regelId.toString() }.firstOrNull()
    val aarsaksAnt = årsaker.size

    secureLogger.info(
        "{} konklusjon gitt for bruker {}, ytelse {}", resultat.svar.name, fnr, response.datagrunnlag.ytelse,
        kv("fnr", fnr),
        kv("fom", response.datagrunnlag.periode.fom.toString()),
        kv("tom", response.datagrunnlag.periode.tom.toString()),
        kv("førsteDagForYtelse", response.datagrunnlag.førsteDagForYtelse.toString()),
        kv("brukerInput", response.datagrunnlag.brukerinput.toString()),
        kv("ytelse", response.datagrunnlag.ytelse),
        kv("svar", response.resultat.svar),
        kv("årsak", årsak),
        kv("årsaker", årsakerSomRegelIdStr),
        kv("aarsaksAnt", aarsaksAnt),
        kv("aarsaker", årsakerSomRegelIdStr.toString()),
        kv("response", objectMapper.writeValueAsString(response))
    )

    if (årsaker.isNotEmpty()) {
        uavklartPåRegel(årsaker.first(), response.datagrunnlag.ytelse.metricName()).increment()
        secureLogger.info(append("årsaker", årsaker), "Årsaker for bruker {}: {}", fnr, årsakerSomRegelIdStr)
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
