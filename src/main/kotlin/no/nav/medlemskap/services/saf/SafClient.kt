package no.nav.medlemskap.services.saf

import io.github.resilience4j.retry.Retry
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.options
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import mu.KotlinLogging
import no.nav.medlemskap.common.exceptions.GraphqlError
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.services.runWithRetryAndMetrics
import no.nav.medlemskap.services.sts.StsRestClient

class SafClient(
        private val baseUrl: String,
        private val stsClient: StsRestClient,
        private val configuration: Configuration,
        private val httpClient: HttpClient,
        private val retry: Retry? = null
) {

    companion object {
        private val logger = KotlinLogging.logger { }
        private const val ANTALL_JOURNALPOSTER = 10
    }

    suspend fun hentJournaldata(fnr: String, callId: String): DokumentoversiktBrukerResponse {
        return runWithRetryAndMetrics("SAF", "DokumentoversiktBruker", retry) {
            val dokumentoversiktBrukerResponse = httpClient.post<DokumentoversiktBrukerResponse>() {
                url("$baseUrl")
                header(HttpHeaders.Authorization, "Bearer ${stsClient.oidcToken()}")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Callid", callId)
                header("Nav-Consumer-Id", configuration.sts.username)
                body = hentSafQuery(fnr, ANTALL_JOURNALPOSTER)
            }

            dokumentoversiktBrukerResponse.errors?.let { errors ->
                logger.warn { "Fikk følgende feil fra Saf: ${objectMapper.writeValueAsString(errors)}" }
                throw GraphqlError(errors.first(), "Saf")
            }

            dokumentoversiktBrukerResponse
        }
    }

    suspend fun healthCheck(): HttpResponse {
        return httpClient.options {
            url("$baseUrl")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Consumer-Id", configuration.sts.username)
        }
    }
}

class SafService(private val safClient: SafClient) {
    suspend fun hentJournaldata(fnr: String, callId: String) =
            mapDokumentoversiktBrukerResponse(safClient.hentJournaldata(fnr, callId))
}


