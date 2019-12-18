package no.nav.medlemskap

import com.github.kittinunf.fuel.core.Deserializable
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.response
import com.github.kittinunf.fuel.httpGet
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName

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
        val (_,_,result) = "$authorityEndpoint/$tenant/v2.0/.well-known/openid-configuration".httpGet()
                .header(mapOf("Accept" to "application/json"))
                .responseJson()
        return result.get()
}

private class AadDeserializer : Deserializable<AzureAdOpenIdConfiguration> {
        override fun deserialize(response: Response): AzureAdOpenIdConfiguration {
                return GsonBuilder()
                        .create()
                        .fromJson(
                                response.dataStream.bufferedReader(Charsets.UTF_8).readText(),
                                AzureAdOpenIdConfiguration::class.java
                        )
        }
}

private fun Request.responseJson() = response(AadDeserializer())
