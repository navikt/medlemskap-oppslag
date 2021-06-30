package no.nav.medlemskap.routes

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.medlemskap.common.hentVersjoner
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.v1.ReglerService
import no.nav.medlemskap.services.kafka.Producer
import org.apache.kafka.clients.producer.ProducerRecord

fun Routing.reglerRoute() {
    route("/regler") {
        post {
            val datagrunnlag: Datagrunnlag = call.receive()
            call.respond(ReglerService.kjørRegler(datagrunnlag))
        }
    }
    route("/regler/v1") {
        post {
            val datagrunnlag: Datagrunnlag = call.receive()
            call.respond(ReglerService.kjørRegler(datagrunnlag))
        }
    }
    route("/regler/versions") {
        get {
            call.respond(hentVersjoner())
        }
    }

    route("/produceTestMessage") {
        get {
            val producer = Producer().createProducer(Configuration().kafkaConfig)
            val futureresult = producer.send(createRecord("test-lovme-heartbeat", "heartbeat"))
            val result = futureresult.get()
            call.respond("message posted with offset: " + result.offset())
        }
    }
}

private fun createRecord(topic: String, value: String): ProducerRecord<String, String> {
    return ProducerRecord(topic, null, value)
}
