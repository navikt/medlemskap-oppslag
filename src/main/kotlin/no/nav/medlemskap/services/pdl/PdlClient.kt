package no.nav.medlemskap.services.pdl

import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.modell.pdl.HentIdenterQuery
import no.nav.medlemskap.modell.pdl.HentIdenterResponse
import no.nav.medlemskap.modell.pdl.IdentGruppe
import no.nav.medlemskap.services.sts.StsRestClient


class PdlClient(
        private val baseUrl: String,
        private val stsClient: StsRestClient,
        private val callIdGenerator: () -> String,
        private val configuration: Configuration) {

    suspend fun hentIdenter(fnr: String): HentIdenterResponse {

        return defaultHttpClient.post<HentIdenterResponse>() {
            url("$baseUrl")
            header(HttpHeaders.Authorization, "Bearer ${stsClient.oidcToken()}")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Call-Id", callIdGenerator.invoke())
            header("Nav-Consumer-Token", "Bearer ${stsClient.oidcToken()}")
            header("Nav-Consumer-Id", configuration.sts.username)
            body = HentIdenterQuery(fnr)
        }
    }
}

class PdlService(val pdlClient: PdlClient) {
    suspend fun hentAktorId(fnr: String) : String = pdlClient.hentIdenter(fnr).data.hentIdenter.identer.first { !it.historisk && it.type == IdentGruppe.AKTORID }.ident

}


