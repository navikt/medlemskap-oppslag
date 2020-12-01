package no.nav.medlemskap.routes

import io.ktor.http.content.*
import io.ktor.routing.*

fun Route.setupSwaggerDocApi() {
    route("/api/v1/docs/") {
        static {
            resources("api")
            defaultResource("api/index.html")
        }
    }
}
