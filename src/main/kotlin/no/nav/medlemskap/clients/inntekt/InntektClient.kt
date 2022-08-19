package no.nav.medlemskap.clients.inntekt

import io.github.resilience4j.retry.Retry
import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import mu.KotlinLogging
import no.nav.medlemskap.clients.runWithRetryAndMetrics
import no.nav.medlemskap.clients.sts.StsRestClient
import no.nav.medlemskap.config.Configuration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InntektClient(
    private val baseUrl: String,
    private val stsClient: StsRestClient,
    private val configuration: Configuration,
    private val httpClient: HttpClient,
    private val retry: Retry? = null
) {

    private val logger = KotlinLogging.logger { }

    suspend fun hentInntektListe(ident: String, callId: String, fraOgMed: LocalDate? = null, tilOgMed: LocalDate? = null): InntektskomponentResponse {
        val token = stsClient.oidcToken()
        return runCatching {
            runWithRetryAndMetrics<InntektskomponentResponse>("Inntekt", "HentinntektlisteV1", retry) {
                httpClient.post() {
                    url("$baseUrl/rs/api/v1/hentinntektliste")
                    header(HttpHeaders.Authorization, "Bearer $token")
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                    header("Nav-Consumer-Id", configuration.sts.username)
                    header("Nav-Call-Id", callId)
                    setBody(
                        HentInntektListeRequest(
                            ident = Ident(ident, "NATURLIG_IDENT"),
                            ainntektsfilter = "MedlemskapA-inntekt",
                            maanedFom = fraOgMed?.tilAarOgMnd(),
                            maanedTom = tilOgMed?.tilAarOgMnd(),
                            formaal = "Medlemskap"
                        )
                    ) // Må diskutere med Helle om vi skal bruke Medlemskap eller sykepenger som formål
                }.body()
            }
        }.fold(
            onSuccess = { it },
            onFailure = { error ->
                when (error) {
                    is ClientRequestException -> {
                        if (error.response.status.value == 400) {
                            InntektskomponentResponse(listOf(), Ident(ident, ""))
                        } else {
                            throw error
                        }
                    }
                    is ServerResponseException -> {
                        if (error.response.status.value == 500) {
                            InntektskomponentResponse(listOf(), Ident(ident, ""))
                        } else {
                            throw error
                        }
                    }
                    else -> throw error
                }
            }
        )
    }

    private fun LocalDate.tilAarOgMnd() = this.format(DateTimeFormatter.ofPattern("yyyy-MM"))

    suspend fun healthCheck(): HttpResponse {
        return httpClient.options {
            url("$baseUrl")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header("Nav-Consumer-Id", configuration.sts.username)
        }
    }
}
