package no.nav.medlemskap.clients.udi

import io.github.resilience4j.retry.Retry
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.medlemskap.clients.azuread.AzureAdClient
import no.nav.medlemskap.clients.runWithRetryAndMetrics
import no.nav.medlemskap.domene.Oppholdstillatelse

class UdiClient(
    private val baseUrl: String,
    private val azureAdClient: AzureAdClient,
    private val httpClient: HttpClient,
    private val udiProxyApiKey: String,
    private val retry: Retry? = null
) {

    suspend fun oppholdstillatelse(udiRequest: UdiRequest, callId: String): Oppholdstillatelse {
        val token = azureAdClient.hentTokenScopetMotUdiProxy()
        return runWithRetryAndMetrics("UDI-proxy", "Oppholdstillatelse", retry) {
            httpClient.post {
                url("$baseUrl/udi/person")
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                header("X-Correlation-Id", callId)
                header("x-nav-apiKey", udiProxyApiKey)
                body = udiRequest
            }
        }
    }
}
