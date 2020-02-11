package no.nav.medlemskap.services.saf

import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import io.ktor.client.request.header
import io.ktor.client.request.options
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import mu.KotlinLogging
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.common.exceptions.GraphqlError
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.modell.saf.DokumentoversiktBrukerResponse
import no.nav.medlemskap.modell.saf.hentSafQuery
import no.nav.medlemskap.services.sts.StsRestClient

private const val ANTALL_JOURNALPOSTER = 10

class SafClient(
        private val baseUrl: String,
        private val stsClient: StsRestClient,
        private val callIdGenerator: () -> String,
        private val configuration: Configuration,
        private val retry: Retry? = null
) {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    suspend fun hentJournaldata(fnr: String): DokumentoversiktBrukerResponse {
        retry?.let {
            return it.executeSuspendFunction {
                hentJournaldataRequest(fnr)
            }
        }
        return hentJournaldataRequest(fnr)
    }

    suspend fun hentJournaldataRequest(fnr: String): DokumentoversiktBrukerResponse {

        val dokumentoversiktBrukerResponse = defaultHttpClient.post<DokumentoversiktBrukerResponse>() {
            url("$baseUrl")
            header(HttpHeaders.Authorization, "Bearer ${stsClient.oidcToken()}")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Callid", callIdGenerator.invoke())
            header("Nav-Consumer-Id", configuration.sts.username)
            body = hentSafQuery(fnr, ANTALL_JOURNALPOSTER)
        }

        dokumentoversiktBrukerResponse.errors?.let { errors ->
            logger.warn { "Fikk følgende feil fra Saf: ${objectMapper.writeValueAsString(errors)}" }
            throw GraphqlError(errors.first(), "Saf")
        }

        return dokumentoversiktBrukerResponse
    }

    suspend fun healthCheck(): HttpResponse {
        return defaultHttpClient.options {
            url("$baseUrl")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Consumer-Id", configuration.sts.username)
        }
    }
}


