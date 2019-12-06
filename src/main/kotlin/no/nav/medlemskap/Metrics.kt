package no.nav.medlemskap

import io.prometheus.client.Counter

private const val METRICS_NS = "medlemskap"

val API_COUNTER: Counter = Counter.Builder()
        .namespace(METRICS_NS)
        .name("api_hit_counter")
        .help("Registers a counter for each hit to the api")
        .register()
