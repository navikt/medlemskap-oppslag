package no.nav.medlemskap.clients.unleash

import mu.KotlinLogging
import no.finn.unleash.DefaultUnleash
import no.finn.unleash.Unleash
import no.finn.unleash.UnleashContext
import no.finn.unleash.UnleashContextProvider
import no.finn.unleash.event.UnleashReady
import no.finn.unleash.event.UnleashSubscriber
import no.finn.unleash.repository.FeatureToggleResponse
import no.finn.unleash.repository.ToggleCollection
import no.finn.unleash.util.UnleashConfig
import no.nav.medlemskap.config.Configuration
import java.util.concurrent.TimeUnit

interface FeatureToggle {
    fun IsEnabled(toggle: Toggle): Boolean
}

class UnleashService(val config: Configuration) : FeatureToggle {
    private val unleash: Unleash
    private val logger = KotlinLogging.logger {}
    init {
        val contextProvider: UnleashContextProvider = lagUnleashContextProvider()
        val config: UnleashConfig = UnleashConfig.Builder()
            .appName(config.unleash.appName)
            .instanceId("medlemskap-oppslag")
            .unleashAPI("https://unleash.nais.io/api")
            .unleashContextProvider(contextProvider)
            .fetchTogglesInterval(TimeUnit.MINUTES.toMinutes(1))
            .sendMetricsInterval(TimeUnit.MINUTES.toMinutes(1))
            // .subscriber(UnleashSubscriber{})
            .build()

        unleash = DefaultUnleash(config)
    }
    fun lagUnleashContextProvider(): UnleashContextProvider {
        return UnleashContextProvider {
            UnleashContext.builder()
                // .userId("a user") // Må legges til en gang i fremtiden
                .environment(config.unleash.environment)
                .appName(config.unleash.appName)
                .build()
        }
    }

    fun lagSubscriber(): UnleashSubscriber {
        val subscriber = object : UnleashSubscriber {
            override fun onReady(ready: UnleashReady) {
                logger.info("Unleash is ready")
            }

            override fun togglesFetched(response: FeatureToggleResponse) {
                // Nothing needs to be logged here!
            }

            override fun togglesBackedUp(toggleCollection: ToggleCollection) {
                logger.info("Unleash toggles Backup stored.")
            }
        }
        return subscriber
    }

    override fun IsEnabled(toggle: Toggle): Boolean {
        // unleash is case sensitiv, så all toggles in unleash must! be in small letters!
        return unleash.isEnabled(toggle.name.toLowerCase())
    }
}

enum class Toggle {
    SHADOW_PROSESSING,
}
