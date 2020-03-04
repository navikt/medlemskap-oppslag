package no.nav.medlemskap

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.micrometer.prometheus.PrometheusRenameFilter

data class ApplicationState(var running: Boolean = true, var initialized: Boolean = false)

val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

fun main() {
    val applicationState = ApplicationState()

    prometheusRegistry.config().meterFilter(PrometheusRenameFilter())

    val applicationServer = createHttpServer(applicationState = applicationState, prometheusRegistry = prometheusRegistry)

    Runtime.getRuntime().addShutdownHook(Thread {
        applicationState.initialized = false
        applicationServer.stop(5000, 5000)
    })

    applicationServer.start(wait = true)
}
