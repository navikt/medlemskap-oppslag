package no.nav.medlemskap.common


import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.Timer
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.micrometer.prometheus.PrometheusRenameFilter
import java.util.concurrent.atomic.AtomicInteger

const val COUNTER_REGEL_CALLS_TOTAL = "regel_calls_total"

fun Counter.inc() = this.increment()

class HoldTimer(private val service: String, private val operation: String) {
    private lateinit var sample: Timer.Sample

    fun startTimer(): HoldTimer {
        sample = Timer.start()
        return this
    }

    fun observeDuration() {
        sample.stop(
                Timer.builder("client_calls_latency")
                        .tags("service", service, "operation", operation)
                        .description("latency for calls to other services")
                        .publishPercentiles(0.5, 0.90)
                        .publishPercentileHistogram()
                        .register(Metrics.globalRegistry))
    }
}

fun configurePrometheusMeterRegistry(): PrometheusMeterRegistry {
    val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    Metrics.globalRegistry.add(prometheusRegistry)
    Metrics.globalRegistry.config().meterFilter(PrometheusRenameFilter())
    prometheusRegistry.config().meterFilter(PrometheusRenameFilter())
    return prometheusRegistry
}


fun regelCounter(regel: String, status: String): Counter = Counter
        .builder(COUNTER_REGEL_CALLS_TOTAL)
        .tags("regel", regel, "status", status)
        .description("counter for ja, nei, uavklart for regel calls")
        .register(Metrics.globalRegistry)

val API_COUNTER: Counter = Counter
        .builder("api_hit_counter")
        .description("Registers a counter for each hit to the api")
        .register(Metrics.globalRegistry)


fun clientTimer(service: String?, operation: String?): HoldTimer =
        HoldTimer(service ?: "UKJENT", operation ?: "UKJENT")

fun clientCounter(service: String?, operation: String?, status: String): Counter = Counter
        .builder("client_calls_total")
        .tags("service", service ?: "UKJENT", "operation", operation ?: "UKJENT", "status", status)
        .description("counter for failed or successful calls to other services")
        .register(Metrics.globalRegistry)

fun clientsGauge(client: String): AtomicInteger =
        AtomicInteger().apply {
            Gauge.builder("health_check_clients_status") { this }
                    .description("Indikerer applikasjonens baksystemers helsestatus. 0 er OK, 1 indikerer feil.")
                    .tags("client", client)
                    .register(Metrics.globalRegistry)
        }


fun totalGauge(): AtomicInteger =
        AtomicInteger().apply {
            Gauge.builder("health_check_status") { this }
                    .description("Indikerer applikasjonens helsestatus. 0 er OK, 1 indikerer feil.")
                    .register(Metrics.globalRegistry)
        }