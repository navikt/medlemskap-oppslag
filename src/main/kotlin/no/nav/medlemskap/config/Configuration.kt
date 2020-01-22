package no.nav.medlemskap.config

import com.natpryce.konfig.*

private val defaultProperties = ConfigurationMap(
        mapOf(
                "AZURE_TENANT" to "",
                "AZURE_AUTHORITY_ENDPOINT" to "",
                "SERVICE_USER_USERNAME" to "test",
                "MEDLEMSKAP_REGLER_URL" to "",
                "TPSWS_URL" to "",
                "MEDL2_BASE_URL" to "",
                "AAREG_BASE_URL" to "",
                "INNTEKT_BASE_URL" to "",
                "SECURITY_TOKEN_SERVICE_URL" to "",
                "SECURITY_TOKEN_SERVICE_REST_URL" to "",
                "SERVICE_USER_PASSWORD" to "",
                "NAIS_APP_NAME" to "",
                "JWT_AUDIENCE" to "",
                "SAF_BASE_URL" to "",
                "OPPGAVE_BASE_URL" to ""
        )
)

private val config = ConfigurationProperties.systemProperties() overriding
        EnvironmentVariables overriding
        defaultProperties

private fun String.configProperty(): String = config[Key(this, stringType)]

data class Configuration(
        val register: Register = Register(),
        val sts: Sts = Sts(),
        val azureAd: AzureAd = AzureAd(),
        val reglerUrl: String = "MEDLEMSKAP_REGLER_URL".configProperty()
) {
    data class Register(
            val tpsUrl: String = "TPSWS_URL".configProperty(),
            val medl2BaseUrl: String = "MEDL2_BASE_URL".configProperty(),
            val aaRegBaseUrl: String = "AAREG_BASE_URL".configProperty(),
            val inntektBaseUrl: String = "INNTEKT_BASE_URL".configProperty(),
            val safBaseUrl: String = "SAF_BASE_URL".configProperty(),
            val oppgaveBaseUrl: String = "OPPGAVE_BASE_URL".configProperty()
    )

    data class Sts(
            val endpointUrl: String = "SECURITY_TOKEN_SERVICE_URL".configProperty(),
            val restUrl: String = "SECURITY_TOKEN_SERVICE_REST_URL".configProperty(),
            val username: String = "SERVICE_USER_USERNAME".configProperty(),
            val password: String = "SERVICE_USER_PASSWORD".configProperty()
    )

    data class AzureAd(
            val clientId: String = "NAIS_APP_NAME".configProperty(),
            val jwtAudience: String = "JWT_AUDIENCE".configProperty(),
            val tenant: String = "AZURE_TENANT".configProperty(),
            val authorityEndpoint: String = "AZURE_AUTHORITY_ENDPOINT".configProperty().removeSuffix("/"),
            val openIdConfiguration: AzureAdOpenIdConfiguration = getAadConfig(authorityEndpoint, tenant)
    )
}
