package no.nav.medlemskap.services.ereg

import io.github.resilience4j.retry.Retry
import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.services.runWithRetryAndMetrics

class EregClient (
        private val baseUrl: String,
        private val httpClient: HttpClient,
        private val configuration: Configuration,
        private val retry: Retry? = null
) {
    suspend fun hentEnhetstype(orgnummer:String, callId: String): String? {
        val organisasjon = runCatching {
            runWithRetryAndMetrics("Ereg", "noekkelinfo", retry) {
                httpClient.get<Organisasjon> {
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
                                Organisasjon(null)
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

    suspend fun hentAntallAnsatte(orgnummer:String?, callId: String): Map<Bruksperiode, Int>? {
        val organisasjonsInfo = runCatching {
            runWithRetryAndMetrics("Ereg", "hentAntallAnsatte", retry) {
                httpClient.get<OrganisasjonsInfo> {
                    url("$baseUrl/v1/organisasjon/$orgnummer")
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
                                OrganisasjonsInfo(null)
                            } else {
                                throw error
                            }
                        }
                        else -> throw error
                    }
                }

        )
        return organisasjonsInfo.organisasjonDetaljer?.ansatte?.associateBy({ ansatte -> ansatte.bruksPeriode }, { ansatte -> ansatte.antall })
    }
}