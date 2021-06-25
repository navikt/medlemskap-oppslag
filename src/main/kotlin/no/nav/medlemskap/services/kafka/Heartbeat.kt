package no.nav.medlemskap.services.kafka

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import java.time.Duration

class Heartbeat() {
        fun run() {
            GlobalScope.launch { beat() }
        }

        private suspend fun beat() {
            while (true) {
                println("heartbeat")
                delay(Duration.ofMinutes(1))
            }
        }
}