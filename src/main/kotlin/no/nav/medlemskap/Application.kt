package no.nav.medlemskap

import no.nav.medlemskap.config.Configuration
import java.util.concurrent.TimeUnit

data class ApplicationState(var running: Boolean = true, var initialized: Boolean = false)

val configuration = Configuration()

fun main() {
    val applicationState = ApplicationState()
    val applicationServer = createHttpServer(applicationState)

    Runtime.getRuntime().addShutdownHook(Thread {
        applicationState.initialized = false
        applicationServer.stop(5, 5, TimeUnit.SECONDS)
    })

    applicationServer.start(wait = true)
}
