package no.nav.medlemskap

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.auth.Principal
import io.ktor.auth.jwt.JWTCredential
import io.ktor.auth.jwt.JWTPrincipal
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.URL
import java.util.concurrent.TimeUnit

private val log = LoggerFactory.getLogger(JwtConfig::class.java)

class JwtConfig {

    val jwkProvider: JwkProvider = JwkProviderBuilder(URL(configuration.azureAd.openIdConfiguration.jwksUri))
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()

    fun validate(credentials: JWTCredential): Principal? {
        return try {
            requireNotNull(credentials.payload.audience) {
                "Audience not present"
            }
            require(credentials.payload.audience.contains(configuration.azureAd.clientId)) {
                "Valid audience not found in claims"
            }
            JWTPrincipal(credentials.payload)
        } catch (e: Exception) {
            log.warn("Failed to validate token", e)
            null
        }
    }
}
