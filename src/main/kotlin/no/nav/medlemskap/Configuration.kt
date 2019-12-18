package no.nav.medlemskap

import com.natpryce.konfig.*

private val defaultProperties = ConfigurationMap(
        mapOf(
                "client.id" to "medlemskap-oppslag",
                "azuread.tenant" to "",
                "azuread.authority.endpoint" to ""
        )
)

private val config = ConfigurationProperties.systemProperties() overriding
        EnvironmentVariables overriding
        defaultProperties

data class Configuration(
        val azureAd: AzureAd = AzureAd()
) {
    data class AzureAd(
            val clientId: String = config[Key("client.id", stringType)],
            val tenant: String = config[Key("azuread.tenant", stringType)],
            val authorityEndpoint: String = config[Key("azuread.authority.endpoint", stringType)].removeSuffix("/"),
            val openIdConfiguration: AzureAdOpenIdConfiguration = getAadConfig(authorityEndpoint, tenant)
    )
}
