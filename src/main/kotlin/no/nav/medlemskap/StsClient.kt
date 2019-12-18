package no.nav.medlemskap

import com.github.kittinunf.fuel.core.Deserializable
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.response
import com.github.kittinunf.fuel.httpGet
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.*

class StsClient(val baseUrl: String, val username: String, val password: String) {
    private var cachedOidcToken: Token? = null
    private var cachedSamlToken: Token? = null

    fun oidcToken(): String {
        val shouldRenewToken = cachedOidcToken?.hasExpired() ?: true
        if (shouldRenewToken)  {
            val (_, _, result) = "$baseUrl/rest/v1/sts/token?grant_type=client_credentials&scope=openid".httpGet()
                    .authenticate(username, password)
                    .header(mapOf("Accept" to "application/json"))
                    .responseJson()

            cachedOidcToken = result.get()
        }

        return cachedOidcToken!!.token
    }

    fun samlToken(): String {
        val shouldRenewToken = cachedSamlToken?.hasExpired() ?: true
        if (shouldRenewToken)  {
            val (_, _, result) = "$baseUrl/rest/v1/sts/samltoken".httpGet()
                    .authenticate(username, password)
                    .header(mapOf("Accept" to "application/json"))
                    .responseJson()

            cachedSamlToken = result.get()
        }

        val urldecodedBase64 = cachedSamlToken!!.token
                .replace('-', '+')
                .replace('_', '/')
                .plus("=".repeat(cachedSamlToken!!.token.length % 4))

        return String(Base64.getDecoder().decode(urldecodedBase64))
    }

    private fun Request.responseJson() = response(TokenDeserializer())

    class TokenDeserializer : Deserializable<Token> {
        override fun deserialize(response: Response): Token {
            return GsonBuilder()
                    .create()
                    .fromJson(
                            response.dataStream.bufferedReader(Charsets.UTF_8).readText(),
                            Token::class.java
                    )
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
