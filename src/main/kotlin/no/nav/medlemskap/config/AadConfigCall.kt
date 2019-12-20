package no.nav.medlemskap.config

import com.google.gson.annotations.SerializedName
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.medlemskap.common.defaultHttpClient

data class AzureAdOpenIdConfiguration(
        @SerializedName("jwks_uri")
        val jwksUri: String,
        @SerializedName("issuer")
        val issuer: String,
        @SerializedName("token_endpoint")
        val tokenEndpoint: String,
        @SerializedName("authorization_endpoint")
        val authorizationEndpoint: String
)

private val logger = KotlinLogging.logger { }

fun getAadConfig(authorityEndpoint: String, tenant: String): AzureAdOpenIdConfiguration = runBlocking {
        defaultHttpClient.get<AzureAdOpenIdConfiguration>("$authorityEndpoint/$tenant/v2.0/.well-known/openid-configuration").also { logger.info { it } }
}
