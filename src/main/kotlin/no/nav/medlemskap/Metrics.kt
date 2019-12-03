package no.nav.medlemskap

import io.prometheus.client.Counter

const val METRICS_NS = "medlemskap-oppslag"

val API_COUNTER: Counter = Counter.Builder()
        .namespace(METRICS_NS)
        .name("api_hit_counter")
        .help("Registers a counter for each hit to the api")
        .register()
