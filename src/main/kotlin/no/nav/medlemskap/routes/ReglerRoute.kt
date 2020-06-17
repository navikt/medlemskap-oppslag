package no.nav.medlemskap.routes

import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import no.nav.medlemskap.common.hentVersjoner
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.v1.Hovedregler
import no.nav.medlemskap.regler.v1.ReglerService

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
}
