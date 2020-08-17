package no.nav.medlemskap.clients.sts

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.resilience4j.retry.Retry
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import no.nav.medlemskap.clients.runWithRetryAndMetrics
import java.time.LocalDateTime
import java.util.*

class StsRestClient(
        private val baseUrl: String,
        private val username: String,
        private val password: String,
        private val httpClient: HttpClient,
        private val retry: Retry? = null
) {
    private var cachedOidcToken: Token? = null
    private var cachedSamlToken: Token? = null

    suspend fun oidcToken(): String {
        if (cachedOidcToken.shouldBeRenewed()) {
            cachedOidcToken = runWithRetryAndMetrics("STS", "TokenV1", retry) {
                httpClient.get<Token> {
                    url("$baseUrl/rest/v1/sts/token")
                    header(HttpHeaders.Authorization, "Basic ${credentials()}")
                    parameter("grant_type", "client_credentials")
                    parameter("scope", "openid")
                }
            }
        }

        return cachedOidcToken!!.token
    }

    suspend fun healthCheck(): HttpResponse {
        return httpClient.options {
            url("$baseUrl/isReady")
            header("Nav-Consumer-Id", username)
        }
    }

    suspend fun samlToken(): String {
        if (cachedSamlToken.shouldBeRenewed()) {
            cachedSamlToken = runWithRetryAndMetrics("STS", "SamlTokenV1", retry) {
                httpClient.get<Token> {
                    url("$baseUrl/rest/v1/sts/samltoken")
                    header(HttpHeaders.Authorization, "Basic ${credentials()}")
                }
            }
        }

        val urldecodedBase64 = cachedSamlToken!!.token
                .replace('-', '+')
                .replace('_', '/')
                .plus("=".repeat(cachedSamlToken!!.token.length % 4))

        return String(Base64.getDecoder().decode(urldecodedBase64))
    }


    private fun credentials() = Base64.getEncoder().encodeToString("${username}:${password}".toByteArray(Charsets.UTF_8))

    private fun Token?.shouldBeRenewed(): Boolean = this?.hasExpired() ?: true

    data class Token(
            @JsonProperty(value = "access_token", required = true)
            val token: String,
            @JsonProperty(value = "token_type", required = true)
            val type: String,
            @JsonProperty(value = "expires_in", required = true)
            val expiresIn: Int) {

        private val expirationTime: LocalDateTime = LocalDateTime.now().plusSeconds(expiresIn - 20L)

        fun hasExpired(): Boolean = expirationTime.isBefore(LocalDateTime.now())
    }
}