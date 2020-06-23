package no.nav.medlemskap.common

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.auth.Principal
import io.ktor.auth.jwt.JWTCredential
import io.ktor.auth.jwt.JWTPrincipal
import mu.KotlinLogging
import no.nav.medlemskap.config.AzureAdOpenIdConfiguration
import no.nav.medlemskap.config.Configuration
import java.net.URL
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger { }
private val secureLogger = KotlinLogging.logger("tjenestekall")

class JwtConfig(val configuration: Configuration, azureAdOpenIdConfiguration: AzureAdOpenIdConfiguration) {

    companion object {
        const val REALM = "medlemskap-oppslag"
    }

    val jwkProvider: JwkProvider = JwkProviderBuilder(URL(azureAdOpenIdConfiguration.jwksUri))
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()

    fun validate(credentials: JWTCredential): Principal? {
        return try {
            requireNotNull(credentials.payload.audience) { "Auth: Audience mangler i token" }
            require(credentials.payload.audience.contains(configuration.azureAd.jwtAudience)) { "Auth: Ugyldig audience i token" }
            secureLogger.info { "credentials payload fra JwtConfig: " + credentials.payload }
            var azp = credentials.payload.getClaim("azp")
            secureLogger.info("azp verdi fra credentials i JwtConfig: $azp")
            JWTPrincipal(credentials.payload)
        } catch (e: Exception) {
            logger.error(e) { "Failed to validate token" }
            null
        }
    }

}
