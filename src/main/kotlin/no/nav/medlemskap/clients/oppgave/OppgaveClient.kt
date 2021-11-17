package no.nav.medlemskap.clients.oppgave

import io.github.resilience4j.retry.Retry
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import mu.KotlinLogging
import no.nav.medlemskap.clients.runWithRetryAndMetrics
import no.nav.medlemskap.clients.sts.StsRestClient

private const val TEMA_MEDLEMSKAP = "MED"
private const val TEMA_UNNTAK_FRA_MEDLEMSKAP = "UFM"
private const val TEMA_TRYGDEAVGIFT = "TRY"

class OppgaveClient(
    private val baseUrl: String,
    private val stsClient: StsRestClient,
    private val httpClient: HttpClient,
    private val oppgaveApiKey: String,
    private val retry: Retry? = null
) {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    suspend fun hentOppgaver(aktorIder: List<String>, callId: String): FinnOppgaverResponse {
        val token = stsClient.oidcToken()
        return runWithRetryAndMetrics("Oppgave", "OppgaverV1", retry) {
            httpClient.get<FinnOppgaverResponse> {
                url("$baseUrl/api/v1/oppgaver")
                header(HttpHeaders.Authorization, "Bearer $token")
                header("X-Correlation-Id", callId)
                header("x-nav-apiKey", oppgaveApiKey)
                aktorIder.forEach { parameter("aktoerId", it) }
                parameter("tema", TEMA_MEDLEMSKAP)
                parameter("tema", TEMA_UNNTAK_FRA_MEDLEMSKAP)
                parameter("tema", TEMA_TRYGDEAVGIFT)
            }
        }
    }

    // skrur av pga. 404 meldinger i kibana
    /*
    suspend fun healthCheck(): HttpResponse {
        val token = stsClient.oidcToken()
        return httpClient.get {
            url("$baseUrl/ping")
            header(HttpHeaders.Authorization, "Bearer $token")
            header("x-nav-apiKey", oppgaveApiKey)
        }
    }
     */
}
