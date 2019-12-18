package no.nav.medlemskap

import com.google.gson.annotations.SerializedName
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.util.*

class StsClient(val baseUrl: String, val username: String, val password: String) {
    private var cachedOidcToken: Token? = null
    private var cachedSamlToken: Token? = null

    fun oidcToken(): String {
        val shouldRenewToken = cachedOidcToken?.hasExpired() ?: true
        if (shouldRenewToken)  {
            cachedOidcToken = runBlocking {
                fetchToken("$baseUrl/rest/v1/sts/token?grant_type=client_credentials&scope=openid")
            }
        }

        return cachedOidcToken!!.token
    }

    fun samlToken(): String {
        val shouldRenewToken = cachedSamlToken?.hasExpired() ?: true
        if (shouldRenewToken)  {
            cachedSamlToken = runBlocking {
                fetchToken("$baseUrl/rest/v1/sts/samltoken")
            }
        }

        val urldecodedBase64 = cachedSamlToken!!.token
                .replace('-', '+')
                .replace('_', '/')
                .plus("=".repeat(cachedSamlToken!!.token.length % 4))

        return String(Base64.getDecoder().decode(urldecodedBase64))
    }

    private suspend fun fetchToken(url: String): Token {
        val credentials = Base64.getEncoder().encode("${username}:${password}".toByteArray(Charsets.UTF_8))

        return defaultHttpClient.get {
            url("$url")
            header(HttpHeaders.Authorization, "Basic $credentials")
        }
    }

    data class Token(
            @SerializedName("access_token")
            val token: String,
            @SerializedName("token_type")
            val type: String,
            @SerializedName("expires_in")
            val expiresIn: Int) {
        // Sett utløpstid litt før, for å være sikker
        private val expirationTime: LocalDateTime = LocalDateTime.now().plusSeconds(expiresIn - 10L)

        fun hasExpired(): Boolean = expirationTime.isBefore(LocalDateTime.now())
    }
}
