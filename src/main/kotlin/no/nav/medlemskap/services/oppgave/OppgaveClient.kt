package no.nav.medlemskap.services.oppgave

import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import mu.KotlinLogging
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.modell.oppgave.FinnOppgaverResponse
import no.nav.medlemskap.services.sts.StsRestClient

private const val TEMA_MEDLEMSKAP = "MED"
private const val TEMA_UNNTAK_FRA_MEDLEMSKAP = "UFM"
private const val TEMA_TRYGDEAVGIFT = "TRY"

class OppgaveClient(
        private val baseUrl: String,
        private val stsClient: StsRestClient,
        private val callIdGenerator: () -> String,
        private val retry: Retry? = null
) {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    suspend fun hentOppgaver(ident: String): FinnOppgaverResponse {
        retry?.let {
            return it.executeSuspendFunction {
                hentOppgaverRequest(ident)
            }
        }
        return hentOppgaverRequest(ident)
    }

    suspend fun hentOppgaverRequest(ident: String): FinnOppgaverResponse {
        val token = stsClient.oidcToken()
        return defaultHttpClient.get {
            url("$baseUrl/api/v1/oppgaver")
            header(HttpHeaders.Authorization, "Bearer $token")
            header("X-Correlation-Id", callIdGenerator.invoke())
            parameter("aktoerId", ident)
            parameter("tema", TEMA_MEDLEMSKAP)
            parameter("tema", TEMA_UNNTAK_FRA_MEDLEMSKAP)
            parameter("tema", TEMA_TRYGDEAVGIFT)
        }
    }

    suspend fun healthCheck(): HttpResponse {
        val token = stsClient.oidcToken()
        return defaultHttpClient.get {
            url("$baseUrl/internal/alive")
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

}
