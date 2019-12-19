package no.nav.medlemskap

import com.github.kittinunf.result.Result;
import com.github.kittinunf.fuel.httpGet
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger { }

class StsClient(val baseUrl: String, val username: String, val password: String) {
    private var cachedOidcToken: Token? = null
    private var cachedSamlToken: Token? = null

    fun testToken() {
        var str = ""
        "$baseUrl/rest/v1/sts/token?grant_type=client_credentials&scope=openid".httpGet()
                .authenticate(username, password)
                .responseString { _, _, result ->
                    when (result) {
                        is Result.Failure -> {
                            val ex = result.getException()
                            logger.error(ex) { "STS error" }
                        }
                        is Result.Success -> {
                            val data = result.get()
                            logger.info { data.substring(0, 10) }
                            str = data
                        }
                    }
                }
        val gson = GsonBuilder().create()
        val token = gson.fromJson(str, Token::class.java)
        logger.info { token.expiresIn }
    }

    fun oidcToken(): String {
        if (cachedOidcToken.shouldBeRenewed())  {
            cachedOidcToken = runBlocking {
                defaultHttpClient.get<Token> {
                    url("$baseUrl/rest/v1/sts/token")
                    header(HttpHeaders.Authorization, "Basic ${credentials()}")
                    parameter("grant_type", "client_credentials")
                    parameter("scope", "openid")
                }
            }
        }

        return cachedOidcToken!!.token
    }

    fun samlToken(): String {
        if (cachedSamlToken.shouldBeRenewed())  {
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

    private fun credentials() = Base64.getEncoder().encode("${username}:${password}".toByteArray(Charsets.UTF_8))

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
