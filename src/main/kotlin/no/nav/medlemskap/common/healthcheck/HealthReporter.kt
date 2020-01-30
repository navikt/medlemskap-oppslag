package no.nav.medlemskap.common.healthcheck

import io.prometheus.client.Gauge
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.util.*
import kotlin.concurrent.fixedRateTimer

class HealthReporter(
        private val healthService: HealthService,
        frequency: Duration = Duration.ofSeconds(30)
) {

    private companion object {
        private const val HEALTHY = 0.0
        private const val UNHEALTHY = 1.0

        private val clientsGauge = Gauge
                .build("health_check_clients_status",
                        "Indikerer applikasjonens helse status. 0 er OK, 1 indikerer feil.")
                .labelNames("client")
                .register()

        private val totalGauge = Gauge
                .build("health_check_status",
                        "Indikerer applikasjonens helse status. 0 er OK, 1 indikerer feil.")
                .register()
    }

    private val timer: Timer

    init {
        timer = fixedRateTimer(
                name = "health_reporter",
                initialDelay = Duration.ofMinutes(1).toMillis(),
                period = frequency.toMillis()) {
            val results = runBlocking { healthService.check() }
            totalGauge.setTotalFromResults(results)
            clientsGauge.setClientsFromResults(results)
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            timer.cancel()
        })
    }

    private fun Gauge.setTotalFromResults(results: List<Result>) {
        if (results.any { it is UnHealthy }) {
            set(UNHEALTHY)
        } else {
            set(HEALTHY)
        }
    }

    private fun Gauge.setClientsFromResults(results: List<Result>) {
        results.forEach { this.labels(it.name).set(if (it is UnHealthy) UNHEALTHY else HEALTHY) }
    }
}