package no.nav.medlemskap.routes

import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Route.setupSwaggerDocApi() {
    route("/api") {
        static {
            resources("api")
            defaultResource("index.html", "api")
        }
    }
}
