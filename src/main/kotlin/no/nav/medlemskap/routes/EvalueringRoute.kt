package no.nav.medlemskap.routes

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import kotlinx.coroutines.runBlocking
import no.nav.medlemskap.common.API_COUNTER
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.configuration
import no.nav.medlemskap.domene.Personhistorikk
import no.nav.medlemskap.modell.Request
import no.nav.medlemskap.modell.Resultat
import no.nav.medlemskap.services.Services.personService
import no.nav.medlemskap.services.tpsws.mapPersonhistorikkResultat
import no.nav.nare.core.evaluations.Evaluering

fun Routing.evalueringRoute() {
    authenticate {
        post("/") {
            API_COUNTER.inc()
            val request = call.receive<Request>()
            val datagrunnlag = createDatagrunnlag(request.fnr)
            call.respond(Resultat(datagrunnlag, evaluerData(datagrunnlag)))
        }
    }
}

private fun createDatagrunnlag(fnr: String): Personhistorikk {
    val historikkFraTps = personService.personhistorikk(fnr)

    return mapPersonhistorikkResultat(historikkFraTps)
}

private fun evaluerData(personhistorikk: Personhistorikk): Evaluering = runBlocking {
    defaultHttpClient.post<Evaluering> {
        url(configuration.reglerUrl)
        body = personhistorikk
    }
}
