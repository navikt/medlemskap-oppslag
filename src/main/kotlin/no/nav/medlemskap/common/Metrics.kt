package no.nav.medlemskap.common


import io.micrometer.core.instrument.*
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.micrometer.prometheus.PrometheusRenameFilter
import no.nav.medlemskap.common.influx.SensuInfluxConfig
import no.nav.medlemskap.common.influx.SensuInfluxMeterRegistry
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger


fun configurePrometheusMeterRegistry(): PrometheusMeterRegistry {
    val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    prometheusRegistry.config().meterFilter(PrometheusRenameFilter())
//    prometheusRegistry.config().meterFilter(MeterFilter.denyNameStartsWith(/*"regel_call"));
//    prometheusRegistry.config().meterFilter(MeterFilter.denyNameStartsWith("api_hit_counter"));*/
    Metrics.globalRegistry.add(prometheusRegistry)
    return prometheusRegistry
}

fun configureSensuInfluxMeterRegistry(): SensuInfluxMeterRegistry {
    val config: SensuInfluxConfig = object : SensuInfluxConfig {
        override fun step(): Duration? {
            return Duration.ofSeconds(10)
        }

        override fun sensuName(): String = "medlemskap-oppslag-events"

        override operator fun get(k: String?): String? {
            return null // accept the rest of the defaults
        }
    }

    val influxMeterRegistry = SensuInfluxMeterRegistry(config, Clock.SYSTEM)
    influxMeterRegistry.config().meterFilter(MeterFilter.denyUnless { it.name.startsWith("regel_call") || it.name.startsWith("api_hit_counter") })
    influxMeterRegistry.config().commonTags(defaultInfluxTags());
    Metrics.globalRegistry.add(influxMeterRegistry)
    return influxMeterRegistry
}

fun defaultInfluxTags() = listOf(
        Tag.of("application", getenv("NAIS_APP_NAME", "medlemskap-oppslag")),
        Tag.of("cluster", getenv("NAIS_CLUSTER_NAME", "dev-fss")),
        Tag.of("namespace", getenv("NAIS_NAMESPACE", "default"))
)

private fun getenv(env: String, defaultValue: String): String {
    return if (System.getenv(env) != null) System.getenv(env) else defaultValue
}

fun regelCounter(regel: String, status: String): Counter = Counter
        .builder("regel_calls_total")
        .tags("regel", regel, "status", status)
        .description("counter for ja, nei, uavklart for regel calls")
        .register(Metrics.globalRegistry)

val API_COUNTER: Counter = Counter
        .builder("api_hit_counter")
        .description("Registers a counter for each hit to the api")
        .register(Metrics.globalRegistry)


fun clientTimer(service: String?, operation: String?): Timer =
        Timer.builder("client_calls_latency")
                .tags("service", service ?: "UKJENT", "operation", operation ?: "UKJENT")
                .description("latency for calls to other services")
                .publishPercentileHistogram()
                .register(Metrics.globalRegistry)

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