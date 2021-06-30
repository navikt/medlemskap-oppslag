package no.nav.medlemskap.services.kafka

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.time.Duration

class Heartbeat(val producer: KafkaProducer<String, String>) {
    fun run() {
        GlobalScope.launch { beat() }
    }

    private suspend fun beat() {
        while (true) {
            producer.send(createRecord("test-lovme-heartbeat", "heartbeat"))
            delay(Duration.ofMinutes(1))
        }
    }

    private fun createRecord(topic: String, value: String): ProducerRecord<String, String> {
        return ProducerRecord(topic, null, value)
    }
}
