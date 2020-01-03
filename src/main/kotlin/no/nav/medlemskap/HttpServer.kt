package no.nav.medlemskap

import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import no.nav.medlemskap.common.JwtConfig
import no.nav.medlemskap.common.MDC_CALL_ID
import no.nav.medlemskap.common.callIdGenerator
import no.nav.medlemskap.common.exceptionHandler
import no.nav.medlemskap.routes.evalueringRoute
import no.nav.medlemskap.routes.naisRoutes
import org.slf4j.event.Level
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val REALM = "medlemskap-oppslag"

private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

val localDateSerializer: JsonSerializer<LocalDate> = JsonSerializer { src, _, _ ->
    if (src == null) null else JsonPrimitive(src.format(formatter))
}

val localDateDeserializer: JsonDeserializer<LocalDate> = JsonDeserializer<LocalDate> { json, _, _ ->
    if (json == null) null else LocalDate.parse(json.asString, formatter)
}

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
            registerTypeAdapter(LocalDate::class.java, localDateDeserializer)
            registerTypeAdapter(LocalDate::class.java, localDateSerializer)
            setPrettyPrinting()
            disableHtmlEscaping()
        }
    }

    install(CallId) {
        header(MDC_CALL_ID)
        generate { callIdGenerator.get() }
        reply { _, callId -> callIdGenerator.set(callId) }
    }

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

    routing {
        naisRoutes(readinessCheck = { applicationState.initialized }, livenessCheck = { applicationState.running })
        evalueringRoute()
    }

    applicationState.initialized = true
}
