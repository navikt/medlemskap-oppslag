package no.nav.medlemskap.config

import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.medlemskap.common.defaultHttpClient

data class AzureAdOpenIdConfiguration(
        @JsonProperty("jwks_uri")
        val jwksUri: String,
        @JsonProperty("issuer")
        val issuer: String,
        @JsonProperty("token_endpoint")
        val tokenEndpoint: String,
        @JsonProperty("authorization_endpoint")
        val authorizationEndpoint: String
)

private val logger = KotlinLogging.logger { }

fun getAadConfig(authorityEndpoint: String, tenant: String): AzureAdOpenIdConfiguration = runBlocking {
        defaultHttpClient.get<AzureAdOpenIdConfiguration>("$authorityEndpoint/$tenant/v2.0/.well-known/openid-configuration").also { logger.info { it } }
}
