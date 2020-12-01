package no.nav.medlemskap.routes

import io.ktor.http.content.*
import io.ktor.routing.*

fun Route.setupSwaggerDocApi() {
    route("/api/") {
        static {
            resources("api")
            defaultResource("index.html", "api")
        }
    }
}
