package no.nav.medlemskap

import com.google.gson.annotations.SerializedName
import io.ktor.client.request.*
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger { }

class StsClient(val baseUrl: String, val username: String, val password: String) {
    private var cachedOidcToken: Token? = null
    private var cachedSamlToken: Token? = null

    suspend fun oidcToken(): String {
        if (cachedOidcToken.shouldBeRenewed()) {
            cachedOidcToken = defaultHttpClient.get<Token> {
                url("$baseUrl/rest/v1/sts/token")
                header(HttpHeaders.Authorization, "Basic ${credentials()}")
                parameter("grant_type", "client_credentials")
                parameter("scope", "openid")
            }
        }

        return cachedOidcToken!!.token
    }

    fun samlToken(): String {
        if (cachedSamlToken.shouldBeRenewed()) {
            cachedSamlToken = runBlocking {
                defaultHttpClient.get<Token> {
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
            @SerializedName("access_token")
            val token: String,
            @SerializedName("token_type")
            val type: String,
            @SerializedName("expires_in")
            val expiresIn: Int) {
        // Sett utløpstid litt før, for å være sikker
        @Transient
        private val expirationTime: LocalDateTime = LocalDateTime.now().plusSeconds(expiresIn - 10L)

        fun hasExpired(): Boolean = expirationTime.isBefore(LocalDateTime.now())
    }
}
