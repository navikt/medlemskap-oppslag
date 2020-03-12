package no.nav.medlemskap.services.medl

import io.github.resilience4j.retry.Retry
import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.services.runWithRetryAndMetrics
import no.nav.medlemskap.services.sts.StsRestClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedlClient(
        private val baseUrl: String,
        private val stsClient: StsRestClient,
        private val configuration: Configuration,
        private val httpClient: HttpClient,
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
        }
    }

}

class MedlService(private val medlClient: MedlClient) {

    suspend fun hentMedlemskapsunntak(ident: String, callId: String, fraOgMed: LocalDate? = null, tilOgMed: LocalDate? = null) =
            mapMedlemskapResultat(medlClient.hentMedlemskapsunntak(ident, callId, fraOgMed, tilOgMed))
}
