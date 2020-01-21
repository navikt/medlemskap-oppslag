package no.nav.medlemskap.common

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import org.apache.http.impl.conn.SystemDefaultRoutePlanner
import java.net.ProxySelector

internal val defaultHttpClient = HttpClient(Apache) {
    install(JsonFeature) {
        serializer = JacksonSerializer { objectMapper }
    }
    engine {
        customizeClient { setRoutePlanner(SystemDefaultRoutePlanner(ProxySelector.getDefault())) }
    }
}
