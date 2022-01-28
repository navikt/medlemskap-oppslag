package no.nav.medlemskap.clients.medl

import io.github.resilience4j.retry.Retry
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import no.nav.medlemskap.clients.runWithRetryAndMetrics
import no.nav.medlemskap.clients.sts.StsRestClient
import no.nav.medlemskap.config.Configuration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedlClient(
    private val baseUrl: String,
    private val stsClient: StsRestClient,
    private val configuration: Configuration,
    private val httpClient: HttpClient,
    private val medlApiKey: String,
    private val retry: Retry? = null
) {

    suspend fun hentMedlemskapsunntak(ident: String, callId: String, fraOgMed: LocalDate? = null, tilOgMed: LocalDate? = null): List<MedlMedlemskapsunntak> {
        val token = stsClient.oidcToken()
        return runCatching {
            runWithRetryAndMetrics("Medl", "MedlemskapsunntakV1", retry) {
                httpClient.get<List<MedlMedlemskapsunntak>> {
                    url("$baseUrl/api/v1/medlemskapsunntak")
                    header(HttpHeaders.Authorization, "Bearer $token")
                    header(HttpHeaders.Accept, ContentType.Application.Json)
                    header("x-nav-apiKey", medlApiKey)
                    header("Nav-Call-Id", callId)
                    header("Nav-Personident", ident)
                    header("Nav-Consumer-Id", configuration.sts.username)
                    fraOgMed?.let { parameter("fraOgMed", fraOgMed.tilIsoFormat()) }
                    tilOgMed?.let { parameter("tilOgMed", tilOgMed.tilIsoFormat()) }
                }
            }
        }.fold(
            onSuccess = { liste -> liste },
            onFailure = { error ->
                when (error) {
                    is ClientRequestException -> {
                        if (error.response.status.value == 404) {
                            listOf()
                        } else {
                            throw error
                        }
                    }
                    else -> throw error
                }
            }
        )
    }

    private fun LocalDate.tilIsoFormat() = this.format(DateTimeFormatter.ISO_DATE)

    suspend fun healthCheck(): HttpResponse {
        return httpClient.get {
            url("$baseUrl/api/ping")
            header("Nav-Consumer-Id", configuration.sts.username)
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("x-nav-apiKey", medlApiKey)
        }
    }
}
