package no.nav.medlemskap

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.auth.Principal
import io.ktor.auth.jwt.JWTCredential
import io.ktor.auth.jwt.JWTPrincipal
import mu.KotlinLogging
import java.lang.Exception
import java.net.URL
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger { }

class JwtConfig {

    val jwkProvider: JwkProvider = JwkProviderBuilder(URL(configuration.azureAd.openIdConfiguration.jwksUri))
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()

    fun validate(credentials: JWTCredential): Principal? {
        return try {
            JWTPrincipal(credentials.payload)
        } catch (e: Exception) {
            logger.error(e) {"Failed to validate token"}
            null
        }
    }
}
