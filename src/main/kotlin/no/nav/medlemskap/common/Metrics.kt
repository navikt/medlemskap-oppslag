package no.nav.medlemskap.common

import io.prometheus.client.Counter
import io.prometheus.client.Histogram

private const val METRICS_NS = "medlemskap"

val API_COUNTER: Counter = Counter.Builder()
        .namespace(METRICS_NS)
        .name("api_hit_counter")
        .help("Registers a counter for each hit to the api")
        .register()

val clientTimer: Histogram = Histogram.build()
        .name("client_calls_latency")
        .labelNames("service", "operation")
        .help("latency for calls to other services")
        .register()

val clientCounter: Counter = Counter.build()
        .name("restclient_calls_total")
        .labelNames("service", "operation", "status")
        .help("counter for failed or successful calls to other services")
        .register()
