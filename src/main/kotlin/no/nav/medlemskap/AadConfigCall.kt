package no.nav.medlemskap

import com.google.gson.annotations.SerializedName
import io.ktor.client.HttpClient
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking

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

fun getAadConfig(authorityEndpoint: String, tenant: String): AzureAdOpenIdConfiguration {
        val client = HttpClient {
                install(JsonFeature) {
                        serializer = GsonSerializer()
                }
        }

        val config = runBlocking {
                client.get<AzureAdOpenIdConfiguration>("$authorityEndpoint/$tenant/v2.0/.well-known/openid-configuration")
        }

        client.close()

        return config
}
