package no.nav.medlemskap.services.inntekt

import io.github.resilience4j.retry.Retry
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.ServerResponseException
import io.ktor.client.request.header
import io.ktor.client.request.options
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import mu.KotlinLogging
import no.nav.medlemskap.common.apacheHttpClient
import no.nav.medlemskap.common.cioHttpClient
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.services.runWithRetryAndMetrics
import no.nav.medlemskap.services.sts.StsRestClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InntektClient(
        private val baseUrl: String,
        private val stsClient: StsRestClient,
        private val configuration: Configuration,
        private val retry: Retry? = null
) {

    private val logger = KotlinLogging.logger { }

    suspend fun hentInntektListe(ident: String, callId: String, fraOgMed: LocalDate? = null, tilOgMed: LocalDate? = null): InntektskomponentResponse {
        val token = stsClient.oidcToken()
        return runCatching {
            runWithRetryAndMetrics("Inntekt", "HentinntektlisteV1", retry) {
                cioHttpClient.post<InntektskomponentResponse> {
                    url("$baseUrl/rs/api/v1/hentinntektliste")
                    header(HttpHeaders.Authorization, "Bearer $token")
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                    header("Nav-Consumer-Id", configuration.sts.username)
                    header("Nav-Call-Id", callId)
                    body = HentInntektListeRequest(
                            ident = Ident(ident, "NATURLIG_IDENT"),
                            ainntektsfilter = "MedlemskapA-inntekt",
                            maanedFom = fraOgMed?.tilAarOgMnd(),
                            maanedTom = tilOgMed?.tilAarOgMnd(),
                            formaal = "Medlemskap") //Må diskutere med Helle om vi skal bruke Medlemskap eller sykepenger som formål
                }
            }
        }.fold(
                onSuccess = { response -> response },
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
        return apacheHttpClient.options {
            url("$baseUrl")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header("Nav-Consumer-Id", configuration.sts.username)
        }
    }
}

class InntektService(private val inntektClient: InntektClient) {

    suspend fun hentInntektListe(ident: String, callId: String, fraOgMed: LocalDate?, tilOgMed: LocalDate?) =
            mapInntektResultat(inntektClient.hentInntektListe(ident, callId, fraOgMed, tilOgMed))

}







