package no.nav.medlemskap

import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.jackson.JacksonConverter
import io.ktor.metrics.micrometer.MicrometerMetrics
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import mu.KotlinLogging
import no.nav.medlemskap.common.JwtConfig
import no.nav.medlemskap.common.JwtConfig.Companion.REALM
import no.nav.medlemskap.common.MDC_CALL_ID
import no.nav.medlemskap.common.exceptionHandler
import no.nav.medlemskap.common.healthcheck.healthRoute
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.config.AzureAdOpenIdConfiguration
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.config.getAadConfig
import no.nav.medlemskap.routes.evalueringRoute
import no.nav.medlemskap.routes.naisRoutes
import no.nav.medlemskap.routes.reglerRoute
import no.nav.medlemskap.services.Services
import org.slf4j.event.Level
import java.util.*

private val logger = KotlinLogging.logger { }

fun createHttpServer(
        applicationState: ApplicationState,
        useAuthentication: Boolean = true,
        configuration: Configuration = Configuration(),
        azureAdOpenIdConfiguration: AzureAdOpenIdConfiguration = getAadConfig(configuration.azureAd),
        services: Services = Services(configuration),
        prometheusRegistry: PrometheusMeterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
): ApplicationEngine = embeddedServer(Netty, 7070) {

    install(StatusPages) {
        exceptionHandler()
    }

    install(CallLogging) {
        level = Level.INFO
        callIdMdc(MDC_CALL_ID)
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(objectMapper))
    }

    install(CallId) {
        header(MDC_CALL_ID)
        generate { UUID.randomUUID().toString() }
        verify { callId: String -> callId.isNotEmpty() }
    }

    if (useAuthentication) {
        logger.info { "Installerer authentication" }
        install(Authentication) {
            jwt {
                val jwtConfig = JwtConfig(configuration, azureAdOpenIdConfiguration)
                realm = REALM
                verifier(jwtConfig.jwkProvider, azureAdOpenIdConfiguration.issuer)
                validate { credentials ->
                    jwtConfig.validate(credentials)
                }
            }
        }
    } else {
        logger.info { "Installerer IKKE authentication" }
    }

    install(MicrometerMetrics) {
        registry = prometheusRegistry
        meterBinders = listOf(
                ClassLoaderMetrics(),
                JvmMemoryMetrics(),
                JvmGcMetrics(),
                ProcessorMetrics(),
                JvmThreadMetrics(),
                FileDescriptorMetrics()
        )
    }

    routing {
        naisRoutes(readinessCheck = { applicationState.initialized }, livenessCheck = { applicationState.running }, collectorRegistry = prometheusRegistry.prometheusRegistry)
        evalueringRoute(services, useAuthentication, configuration)
        reglerRoute()
        healthRoute("/healthCheck", services.healthService)
    }

    applicationState.initialized = true
}
