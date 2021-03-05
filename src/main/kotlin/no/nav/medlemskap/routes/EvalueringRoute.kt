package no.nav.medlemskap.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import net.logstash.logback.argument.StructuredArguments.kv
import net.logstash.logback.marker.Markers.append
import no.nav.medlemskap.clients.Services
import no.nav.medlemskap.common.RequestContextService
import no.nav.medlemskap.common.apiCounter
import no.nav.medlemskap.common.exceptions.KonsumentIkkeFunnet
import no.nav.medlemskap.common.exceptions.UgyldigRequestException
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.common.uavklartPåRegel
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Fødselsnummer.Companion.gyldigFnr
import no.nav.medlemskap.domene.Request
import no.nav.medlemskap.domene.Response
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.Ytelse.Companion.name
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.v1.Hovedregler
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger { }

private val secureLogger = KotlinLogging.logger("tjenestekall")

fun Routing.evalueringRoute(
    services: Services,
    configuration: Configuration,
    requestContextService: RequestContextService,
    createDatagrunnlag: suspend (request: Request, callId: String, services: Services, clientId: String?) -> Datagrunnlag
) {

    authenticate("azureAuth") {
        post("/") {
            apiCounter().increment()

            val callerPrincipal: JWTPrincipal = call.authentication.principal()!!
            val azp = callerPrincipal.payload.getClaim("azp").asString()
            secureLogger.info("EvalueringRoute: azp-claim i principal-token: {}", azp)

            val request = validerRequest(call.receive(), azp)
            val callId = call.callId ?: UUID.randomUUID().toString()

            val datagrunnlag = withContext(
                requestContextService.getCoroutineContext(
                    context = coroutineContext,
                    ytelse = finnYtelse(request.ytelse, azp)
                )
            ) {
                createDatagrunnlag.invoke(
                    request,
                    callId,
                    services,
                    azp
                )
            }
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
    requestContextService: RequestContextService,
    createDatagrunnlag: suspend (request: Request, callId: String, services: Services, clientId: String?) -> Datagrunnlag
) {
    logger.info("autentiserer IKKE kallet")
    post("/") {
        apiCounter().increment()
        val request = validerRequest(call.receive(), Ytelse.toMedlemskapClientId())
        val callId = call.callId ?: UUID.randomUUID().toString()

        val datagrunnlag = withContext(
            requestContextService.getCoroutineContext(
                context = coroutineContext,
                ytelse = finnYtelse(request.ytelse, Ytelse.toMedlemskapClientId())
            )
        ) {
            createDatagrunnlag.invoke(
                request,
                callId,
                services,
                Ytelse.toMedlemskapClientId()
            )
        }
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
        kv("orgnummer", response.datagrunnlag.gyldigeOrgnummer()),
        kv("fom", response.datagrunnlag.periode.fom.toString()),
        kv("tom", response.datagrunnlag.periode.tom.toString()),
        kv("førsteDagForYtelse", response.datagrunnlag.førsteDagForYtelse.toString()),
        kv("brukerInput", response.datagrunnlag.brukerinput.toString()),
        kv("startdatoForYtelse", response.datagrunnlag.startDatoForYtelse.toString()),
        kv("ytelse", response.datagrunnlag.ytelse),
        kv("svar", response.resultat.svar),
        kv("årsak", årsak),
        kv("årsaker", årsakerSomRegelIdStr),
        kv("aarsaksAnt", aarsaksAnt),
        kv("aarsaker", årsakerSomRegelIdStr.toString()),
        kv("statsborgerskap", response.datagrunnlag.gyldigeStatsborgerskap().toString()),
        kv("statsborgerskapAnt", response.datagrunnlag.gyldigeStatsborgerskap().size),
        kv("erTredjelandsborger", response.resultat.erTredjelandsborger()),
        kv("erEosBorger", response.resultat.erEøsBorger()),
        kv("erNorskBorger", response.resultat.erNorskBorger()),
        kv("AaRegUtenlandsoppholdLandkode", response.datagrunnlag.gyldigeAaRegUtenlandsopphold()),
        kv("AaRegUtenlandsoppsholdPeriodeFom", response.datagrunnlag.gyldigeAaRegUtenlandsoppholdPeriodeFom().toString()),
        kv("AaRegUtenlandsoppsholdPeriodeTom", response.datagrunnlag.gyldigeAaRegUtenlandsoppholdPeriodeTom().toString()),
        kv("skipsinfo", response.datagrunnlag.kombinasjonAvSkipsregisterFartsomradeOgSkipstype()),
        kv("response", objectMapper.writeValueAsString(response))
    )

    if (årsaker.isNotEmpty()) {
        uavklartPåRegel(årsaker.first(), response.datagrunnlag.ytelse.name()).increment()
        secureLogger.info(append("årsaker", årsaker), "Årsaker for bruker {}: {}", fnr, årsakerSomRegelIdStr)
    }
}

private fun validerRequest(request: Request, azp: String): Request {
    val ytelse = finnYtelse(request.ytelse, azp)

    if (request.periode.tom.isBefore(request.periode.fom)) {
        throw UgyldigRequestException("Periode tom kan ikke være før periode fom", ytelse)
    }

    if (!gyldigFnr(request.fnr)) {
        throw UgyldigRequestException("Ugyldig fødselsnummer", ytelse)
    }

    return request
}

fun finnYtelse(ytelseFraRequest: Ytelse?, clientId: String?) =
    (ytelseFraRequest ?: Ytelse.fromClientId(clientId))
        ?: throw KonsumentIkkeFunnet("Fant ikke clientId i mapping til ytelse. Ta kontakt med medlemskap-teamet for tilgang til tjenesten.")

fun evaluerData(datagrunnlag: Datagrunnlag): Resultat =
    Hovedregler(datagrunnlag).kjørHovedregler()
