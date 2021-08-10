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
import java.util.*

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

    route("/medlemskapVurdert") {
        post {
            val datagrunnlag: Datagrunnlag = call.receive()
            val producer = Producer().createProducer((Configuration().kafkaConfig))
            val reglerResultat = ReglerService.kjørRegler(datagrunnlag)
            producer.send(createRecord("medlemskap-vurdert","", reglerResultat.tilJson()))
            call.respond("Request prosessert OK")
        }
    }
}

private fun createRecord(topic: String, key: String = UUID.randomUUID().toString(), value: String): ProducerRecord<String, String> {
    return ProducerRecord(topic, key, value)
}
