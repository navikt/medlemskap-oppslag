package no.nav.medlemskap

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mu.KotlinLogging
import no.nav.medlemskap.common.API_COUNTER
import no.nav.medlemskap.common.JwtConfig
import no.nav.medlemskap.common.MDC_CALL_ID
import no.nav.medlemskap.common.exceptionHandler
import no.nav.medlemskap.modell.Request
import no.nav.medlemskap.modell.Resultat
import no.nav.medlemskap.modell.Resultattype.*
import no.nav.medlemskap.services.WsClients
import no.nav.medlemskap.services.sts.StsRestClient
import no.nav.medlemskap.services.sts.stsClient
import no.nav.medlemskap.services.tpsws.PersonService
import org.slf4j.event.Level
import java.util.*

private const val REALM = "medlemskap-oppslag"

private val logger = KotlinLogging.logger { }

private val callIdGenerator = ThreadLocal.withInitial {
    UUID.randomUUID().toString()
}

fun createHttpServer(
        applicationState: ApplicationState,
        useAuthentication: Boolean = true
): ApplicationEngine = embeddedServer(Netty, 7070) {

    val stsWsClient = stsClient(
            stsUrl = configuration.sts.endpointUrl,
            username = configuration.sts.username,
            password = configuration.sts.password
    )

    val stsRestClient = StsRestClient(
            baseUrl = configuration.sts.restUrl,
            username = configuration.sts.username,
            password = configuration.sts.password
    )

    val wsClients = WsClients(
            stsClientWs = stsWsClient,
            stsClientRest = stsRestClient,
            callIdGenerator = callIdGenerator::get
    )

    val personService = PersonService(wsClients.person(configuration.register.tpsUrl))

    install(StatusPages) {
        exceptionHandler()
    }

    install(CallLogging) {
        level = Level.DEBUG
        callIdMdc(MDC_CALL_ID)
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
        }
    }

    install(CallId) {
        header("Nav-Call-Id")
        generate { callIdGenerator.get() }
        reply { _, callId -> callIdGenerator.set(callId) }
    }

    if (useAuthentication) {
        install(Authentication) {
            jwt {
                skipWhen { !useAuthentication }
                val jwtConfig = JwtConfig()
                realm = REALM
                verifier(jwtConfig.jwkProvider, configuration.azureAd.openIdConfiguration.issuer)
                validate { credentials ->
                    jwtConfig.validate(credentials)
                }
            }
        }
    }

    routing {
        naisRoutes(readinessCheck = { applicationState.initialized }, livenessCheck = { applicationState.running })
        authenticate {
            route("/") {
                post {
                    API_COUNTER.inc()
                    val request = call.receive<Request>()
                    logger.info { "Recieved request for ${request.fnr}" }
                    val historikk = personService.personhistorikk(request.fnr)
                    call.respond(Resultat(UAVKLART, "Historikk: ${historikk.statsborgerskapListe}"))
                }
            }
        }
    }

    applicationState.initialized = true
}
