package no.nav.medlemskap.clients.oppgave

import io.github.resilience4j.retry.Retry
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
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
                aktorIder.forEach { parameter("aktoerId", it) }
                parameter("tema", TEMA_MEDLEMSKAP)
                parameter("tema", TEMA_UNNTAK_FRA_MEDLEMSKAP)
                parameter("tema", TEMA_TRYGDEAVGIFT)
            }
        }
    }

    suspend fun healthCheck(): HttpResponse {
        val token = stsClient.oidcToken()
        return httpClient.get {
            url("$baseUrl/internal/alive")
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

}

class OppgaveService(private val oppgaveClient: OppgaveClient) {

    suspend fun hentOppgaver(aktorIder: List<String>, callId: String) =
            mapOppgaveResultat(oppgaveClient.hentOppgaver(aktorIder, callId).oppgaver)

}
