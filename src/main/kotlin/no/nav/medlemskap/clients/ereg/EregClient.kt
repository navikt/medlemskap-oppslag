package no.nav.medlemskap.clients.ereg

import io.github.resilience4j.retry.Retry
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.medlemskap.clients.runWithRetryAndMetrics
import no.nav.medlemskap.config.Configuration

class EregClient(
    private val baseUrl: String,
    private val httpClient: HttpClient,
    private val configuration: Configuration,
    private val retry: Retry? = null
) {
    suspend fun hentEnhetstype(orgnummer: String, callId: String): String? {
        val organisasjon = runCatching {
            runWithRetryAndMetrics("Ereg", "noekkelinfo", retry) {
                httpClient.get<OrganisasjonNøkkelinfo> {
                    url("$baseUrl/v1/organisasjon/$orgnummer/noekkelinfo")
                    header(HttpHeaders.Accept, ContentType.Application.Json)
                    header("Nav-Call-Id", callId)
                    header("Nav-Consumer-Id", configuration.sts.username)
                }
            }
        }.fold(
            onSuccess = { it },
            onFailure = { error ->
                when (error) {
                    is ClientRequestException -> {
                        if (error.response.status.value == 404) {
                            OrganisasjonNøkkelinfo(null)
                        } else {
                            throw error
                        }
                    }
                    else -> throw error
                }
            }

        )
        return organisasjon.enhetstype
    }

    suspend fun hentOrganisasjon(orgnummer: String?, callId: String): Organisasjon {
        val organisasjonsInfo = kotlin.runCatching {
            runWithRetryAndMetrics("Ereg", "hentOrganisasjon", retry) {
                httpClient.get<Organisasjon> {
                    url("$baseUrl/v1/organisasjon/$orgnummer")
                    parameter("inkluderHierarki", true)
                    header(HttpHeaders.Accept, ContentType.Application.Json)
                    header("Nav-Call-Id", callId)
                    header("Nav-Consumer-Id", configuration.sts.username)
                }
            }
        }.fold(
            onSuccess = { it },
            onFailure = { error ->
                when (error) {
                    is ClientRequestException -> {
                        if (error.response.status.value == 404) {
                            Organisasjon(
                                null,
                                null,
                                null,
                                null,
                                null
                            )
                        } else {
                            throw error
                        }
                    }
                    else -> throw error
                }
            }

        )
        return organisasjonsInfo
    }
}
