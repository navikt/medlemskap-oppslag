package no.nav.medlemskap.services.kafka

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.time.delay
import java.time.Duration

class heartbeat() {

    suspend fun run() {
        coroutineScope {
            while (true) {
                delay(Duration.ofMinutes(1))
                println("heartbeat")
            }
        }
    }
}