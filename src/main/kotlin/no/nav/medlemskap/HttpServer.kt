package no.nav.medlemskap

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.AuthenticationRouteSelector
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import no.nav.medlemskap.modell.Resultat
import no.nav.medlemskap.modell.Resultattype.*
import org.slf4j.event.Level
import java.util.*

private const val REALM = "medlemskap-oppslag"

fun createHttpServer(
        applicationState: ApplicationState,
        useAuthentication: Boolean = true
): ApplicationEngine = embeddedServer(Netty, 7070) {
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
        generate { UUID.randomUUID().toString() }
        verify { callId: String -> callId.isNotEmpty() }
        header(HttpHeaders.XCorrelationId)
    }

    if (useAuthentication) {
        install(Authentication) {
            jwt {
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
        route("/") {
            conditionalAuthenticate(useAuthentication) {
                get {
                    API_COUNTER.inc()
                    val token = stsClient.oidcToken()
                    call.respond(Resultat(KANSKJE, "${configuration.sts.username} ${token.substring(0, 5)}"))
                }
            }
        }
    }

    applicationState.initialized = true
}

private fun Route.conditionalAuthenticate(useAuthentication: Boolean, build: Route.() -> Unit): Route {
    if (useAuthentication) {
        return authenticate(build = build)
    }
    val route = createChild(AuthenticationRouteSelector(listOf<String?>(null)))
    route.build()
    return route
}
