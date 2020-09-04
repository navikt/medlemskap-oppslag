package no.nav.medlemskap.regler.common

import com.neovisionaries.i18n.LanguageAlpha3Code
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LandkodeTest {

    @Test
    fun `ISO 3166-1 Alpha-2 (To bokstaver) landkoder gir tre-bokstav`() {

        Assertions.assertEquals("NOR", LanguageAlpha3Code.getByCode("no").name.toUpperCase())
    }

    @Test
    fun `ISO 3166-1 Alpha-2 (To bokstaver) landkoder gir tre-bokstav med store boksaver`() {

        Assertions.assertEquals("NOR", LanguageAlpha3Code.getByCode("NO".toLowerCase()).name.toUpperCase())
    }
}