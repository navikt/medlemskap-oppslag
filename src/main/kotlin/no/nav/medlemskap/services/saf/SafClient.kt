package no.nav.medlemskap.services.saf

import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.modell.saf.DokumentoversiktBrukerQuery
import no.nav.medlemskap.modell.saf.DokumentoversiktBrukerResponse
import no.nav.medlemskap.services.sts.StsRestClient

private const val ANTALL_JOURNALPOSTER = 10

class SafClient(
        val baseUrl: String,
        val stsClient: StsRestClient,
        val callIdGenerator: () -> String,
        private val config: Configuration) {

    suspend fun hentJournaldata(fnr: String): DokumentoversiktBrukerResponse {

        return defaultHttpClient.post<DokumentoversiktBrukerResponse>() {
            url("$baseUrl")
            header(HttpHeaders.Authorization, "Bearer ${stsClient.oidcToken()}")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Callid", callIdGenerator.invoke())
            header("Nav-Consumer-Id", config.sts.username)
            body = DokumentoversiktBrukerQuery(fnr, ANTALL_JOURNALPOSTER)
        }
    }
}


