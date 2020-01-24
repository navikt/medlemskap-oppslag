package no.nav.medlemskap.common

import io.prometheus.client.Counter
import io.prometheus.client.Histogram

private const val METRICS_NS = "medlemskap"

val API_COUNTER: Counter = Counter.Builder()
        .namespace(METRICS_NS)
        .name("api_hit_counter")
        .help("Registers a counter for each hit to the api")
        .register()

val restClientTimer: Histogram = Histogram.build()
        .name("restclient_calls_latency")
        .labelNames("service", "operation")
        .help("latency for REST calls")
        .register()

val restClientCounter: Counter = Counter.build()
        .name("restclient_calls_total")
        .labelNames("service", "operation", "status")
        .help("counter for failed or successful REST calls")
        .register()
