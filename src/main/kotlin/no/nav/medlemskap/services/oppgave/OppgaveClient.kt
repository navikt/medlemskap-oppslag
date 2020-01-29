package no.nav.medlemskap.services.oppgave

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.response.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import mu.KotlinLogging
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.modell.oppgave.FinnOppgaverResponse
import no.nav.medlemskap.services.sts.StsRestClient

private const val TEMA_MEDLEMSKAP = "MED"
private const val TEMA_UNNTAK_FRA_MEDLEMSKAP = "UFM"
private const val TEMA_TRYGDEAVGIFT = "TRY"

class OppgaveClient(
        private val baseUrl: String,
        private val stsClient: StsRestClient,
        private val callIdGenerator: () -> String
) {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    suspend fun hentOppgaver(ident: String): FinnOppgaverResponse {
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

    suspend fun healthCheck(): Boolean {
        return try {
            val response: HttpResponse = healthCheckQuery()
            logger.info("Healthcheck mot GSak returnerte status ${response.status.value} (${response.status.description})")
            response.status.isSuccess()
        } catch (t: Throwable) {
            logger.warn("Healthcheck mot GSak feilet", t)
            false
        }
    }

    suspend fun healthCheckQuery(): HttpResponse {
        val token = stsClient.oidcToken()
        return defaultHttpClient.get {
            url("$baseUrl/internal/alive")
            header(HttpHeaders.Authorization, "Bearer $token")
            header("X-Correlation-Id", callIdGenerator.invoke())
        }
    }

}
