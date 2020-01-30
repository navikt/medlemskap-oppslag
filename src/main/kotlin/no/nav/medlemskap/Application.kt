package no.nav.medlemskap

import java.util.concurrent.TimeUnit

data class ApplicationState(var running: Boolean = true, var initialized: Boolean = false)

fun main() {
    val applicationState = ApplicationState()
    val applicationServer = createHttpServer(applicationState)

    Runtime.getRuntime().addShutdownHook(Thread {
        applicationState.initialized = false
        applicationServer.stop(5000, 5000)
    })

    applicationServer.start(wait = true)
}
