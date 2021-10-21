package no.nav.medlemskap.clients.unleash

import no.nav.medlemskap.config.Configuration
import org.junit.Ignore
import org.junit.jupiter.api.Test

@Ignore
/**
 * Tests are just for verifying toggles and that they exists. should not be run as part of
 * builing process due to threadign issues in Junit
 * tests can be run locally by removinb @ignore and modify/add new tests
 * */
class UnleashServiceTest {
    @Ignore
    @Test
    fun `oppretting av UnleashService fungerer`() {
        val unleashService = UnleashService(Configuration())
        val response = unleashService.IsEnabled(Toggle.SHADOW_PROSESSING)
        println(response)
    }
    @Ignore
    @Test
    fun `Shadow prosessing skal svare ja ca 50% av tiden`() {
        val unleashService = UnleashService(Configuration())
        //sov i 1 sekund for å håndtere initialisering av unleash (kjører i egen tråd)
        Thread.sleep(1000)
        var list = mutableListOf<Boolean>()
        for (n in 1..10) {
            list.add(unleashService.IsEnabled(Toggle.SHADOW_PROSESSING))
        }
        /*
        * det er statistisk 50% og ikke komplett 50/50. vi kan derfor ikke asserte på at vi får 5 av hver..
        * */
        println("Anntall disabled: " + list.filter { !it }.size)
        println("Anntall enabled: " + list.filter { it }.size)
    }
}

