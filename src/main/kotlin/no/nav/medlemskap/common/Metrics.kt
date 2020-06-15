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
import kotlin.math.truncate


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
    influxMeterRegistry.config().meterFilter(MeterFilter.denyUnless {
        it.name.startsWith("api_hit_counter")
                || it.name.startsWith("stillingsprosent")
                || it.name.startsWith("dekningstyper")
                || it.name.contains("arbeidsforhold")

    })
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

fun stillingsprosentCounter(stillingsprosent: Double): Counter =
        if (stillingsprosent < 100.0) {
            Counter.builder("stillingsprosent_deltid")
                    .tags("stillingsprosent", getStillingsprosentIntervall(stillingsprosent))
                    .description("counter for fordeling av stillingsprosenter")
                    .register(Metrics.globalRegistry)
        } else {
            Counter.builder("stillingsprosent_heltid")
                    .description("counter for antall brukere med heltidsstilling")
                    .register(Metrics.globalRegistry)
        }


fun merEnn10ArbeidsforholdCounter(): Counter = Counter
        .builder("over_10_arbeidsforhold")
        .description("counter for brukere med flere enn 10 arbeidsforhold")
        .register(Metrics.globalRegistry)

fun usammenhengendeArbeidsforholdCounter(): Counter = Counter
        .builder("usammenhengende_arbeidsforhold")
        .description("counter for usammenhengende arbeidsforhold")
        .register(Metrics.globalRegistry)

fun harIkkeArbeidsforhold12MndTilbakeCounter(): Counter = Counter
        .builder("ingen_arbeidsforhold_fra_12_mnd_tilbake")
        .description("counter for brukere som ikke har arbeidsforhold som starter 12 mnd tilbake")
        .register(Metrics.globalRegistry)

fun dekningKoderCounter(dekning: String): Counter = Counter
        .builder("dekningstyper")
        .tags( "dekningstyper", dekning)
        .description("Ulike dekningskoder til brukere som har spurt tjenesten")
        .register(Metrics.globalRegistry)

private fun getStillingsprosentIntervall(stillingsprosent: Double): String {
    //Fjerner desimaler fremfor å runde av fordi regelsjekken godtar ikke f.eks. 24.9% stilling som høy nok til å regnes som 25% stilling.
    val stillingsprosentHeltall = truncate(stillingsprosent).toInt()
    when {
        0 == stillingsprosentHeltall -> return "0"
        (1..14).contains(stillingsprosentHeltall) -> return "1 - 14"
        (15..24).contains(stillingsprosentHeltall) -> return "15 - 24"
        (25..34).contains(stillingsprosentHeltall) -> return "25 - 34"
        (35..44).contains(stillingsprosentHeltall) -> return "35 - 44"
        (45..54).contains(stillingsprosentHeltall) -> return "45 - 54"
        (55..64).contains(stillingsprosentHeltall) -> return "55 - 64"
        (65..74).contains(stillingsprosentHeltall) -> return "65 - 74"
        (75..84).contains(stillingsprosentHeltall) -> return "75 - 84"
        (85..99).contains(stillingsprosentHeltall) -> return "85 - 99"
    }
    return "N/A"
}

fun apiCounter(): Counter = Counter
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