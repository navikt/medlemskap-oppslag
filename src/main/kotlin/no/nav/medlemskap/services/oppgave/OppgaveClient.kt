package no.nav.medlemskap.services.oppgave

import io.github.resilience4j.retry.Retry
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import mu.KotlinLogging
import no.nav.medlemskap.common.cioHttpClient
import no.nav.medlemskap.services.runWithRetryAndMetrics
import no.nav.medlemskap.services.sts.StsRestClient

private const val TEMA_MEDLEMSKAP = "MED"
private const val TEMA_UNNTAK_FRA_MEDLEMSKAP = "UFM"
private const val TEMA_TRYGDEAVGIFT = "TRY"

class OppgaveClient(
        private val baseUrl: String,
        private val stsClient: StsRestClient,
        private val retry: Retry? = null
) {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    suspend fun hentOppgaver(ident: String, callId: String): FinnOppgaverResponse {
        val token = stsClient.oidcToken()
        return runWithRetryAndMetrics("Oppgave", "OppgaverV1", retry) {
            cioHttpClient.get<FinnOppgaverResponse> {
                url("$baseUrl/api/v1/oppgaver")
                header(HttpHeaders.Authorization, "Bearer $token")
                header("X-Correlation-Id", callId)
                parameter("aktoerId", ident)
                parameter("tema", TEMA_MEDLEMSKAP)
                parameter("tema", TEMA_UNNTAK_FRA_MEDLEMSKAP)
                parameter("tema", TEMA_TRYGDEAVGIFT)
            }
        }
    }

    suspend fun healthCheck(): HttpResponse {
        val token = try {
            stsClient.oidcToken()
        } catch (t: Throwable) {
            logger.warn("Feilet under henting av OIDC token i helsesjekken")
            throw t
        }
        return cioHttpClient.get {
            url("$baseUrl/internal/alive")
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

}

class OppgaveService(private val oppgaveClient: OppgaveClient) {

    suspend fun hentOppgaver(ident: String, callId: String) =
            mapOppgaveResultat(oppgaveClient.hentOppgaver(ident, callId).oppgaver)

}
