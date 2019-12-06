package no.nav.medlemskap

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import no.nav.medlemskap.modell.Resultat
import no.nav.medlemskap.modell.Resultattype.*

fun createHttpServer(applicationState: ApplicationState): ApplicationEngine = embeddedServer(Netty, 7070) {
    install(CallLogging)

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
        }
    }

    routing {
        naisRoutes(readinessCheck = { applicationState.initialized }, livenessCheck = { applicationState.running })
        route("/") {
            get {
                API_COUNTER.labels("get").inc()
                call.respond(Resultat(KANSKJE, "Ingen regler implementert"))
            }
        }
    }

    applicationState.initialized = true
}
