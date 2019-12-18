package no.nav.medlemskap

import com.natpryce.konfig.*

private val defaultProperties = ConfigurationMap(
        mapOf(
                "client.id" to "medlemskap-oppslag",
                "AZURE_TENANT" to "",
                "AZURE_AUTHORITY_ENDPOINT" to "",
                "SECURITY_TOKEN_SERVICE_BASE_URL" to ""
        )
)

private val config = ConfigurationProperties.systemProperties() overriding
        EnvironmentVariables overriding
        defaultProperties

data class Configuration(
        val azureAd: AzureAd = AzureAd(),
        val sts: Sts = Sts()
) {
    data class Sts(
            val baseUrl: String = config[Key("SECURITY_TOKEN_SERVICE_BASE_URL", stringType)],
            val username: String = config[Key("SERVICE_USER_USERNAME", stringType)],
            val password: String = config[Key("SERVICE_USER_PASSWORD", stringType)]
    )

    data class AzureAd(
            val clientId: String = config[Key("client.id", stringType)],
            val tenant: String = config[Key("azuread.tenant", stringType)],
            val authorityEndpoint: String = config[Key("azuread.authority.endpoint", stringType)].removeSuffix("/"),
            val openIdConfiguration: AzureAdOpenIdConfiguration = getAadConfig(authorityEndpoint, tenant)
    )
}
