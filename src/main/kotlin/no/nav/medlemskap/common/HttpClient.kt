package no.nav.medlemskap.common

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import no.nav.medlemskap.localDateDeserializer
import no.nav.medlemskap.localDateSerializer
import org.apache.http.impl.conn.SystemDefaultRoutePlanner
import java.net.ProxySelector
import java.time.LocalDate

internal val defaultHttpClient = HttpClient(Apache) {
    install(JsonFeature) {
        serializer = GsonSerializer {
            disableHtmlEscaping()
            registerTypeAdapter(LocalDate::class.java, localDateDeserializer)
            registerTypeAdapter(LocalDate::class.java, localDateSerializer)
        }
    }
    engine {
        customizeClient { setRoutePlanner(SystemDefaultRoutePlanner(ProxySelector.getDefault())) }
    }
}
