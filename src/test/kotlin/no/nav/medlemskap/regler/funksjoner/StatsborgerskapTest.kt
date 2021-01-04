package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap.Companion.harEndretSisteÅret
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class StatsborgerskapTest {

    @Test
    fun `Endret statsborgerskap gir true`() {
        val statsborgerskapListe = listOf(statsborgerskapUendretSisteÅret, statsborgerskapEndretSisteÅret)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        assertTrue(statsborgerskapListe.harEndretSisteÅret(kontrollperiode))
    }

    @Test
    fun `Uendret statsborgerskap gir false`() {
        val statsborgerskapListe = listOf(statsborgerskapUendretSisteÅret)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        assertFalse(statsborgerskapListe.harEndretSisteÅret(kontrollperiode))
    }

    @Test
    fun `Statsborgerskap med fom lik null og innenfor perioden gir true`() {
        val statsborgerskapListe = listOf(statsborgerskapMedFomLikNullOgTomInnenforPeriode)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        assertTrue(statsborgerskapListe.harEndretSisteÅret(kontrollperiode))
    }

    @Test
    fun `Statsborgerskap med fom lik null og tom utenfor perioden gir false`() {
        val statsborgerskapListe = listOf(statsborgerskapMedTomLikNullOgFomUtenforPeriode)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        assertFalse(statsborgerskapListe.harEndretSisteÅret(kontrollperiode))
    }

    @Test
    fun `Statsborgerskap med tom lik null og fom innenfor perioden gir true`() {
        val statsborgerskapListe = listOf(statsborgerskapMedTomLikNullOgFomInnenforPeriode)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        assertTrue(statsborgerskapListe.harEndretSisteÅret(kontrollperiode))
    }

    val statsborgerskapMedTomLikNullOgFomInnenforPeriode = Statsborgerskap(
        "NOR",
        fom = LocalDate.of(2019, 5, 20), tom = null
    )

    val statsborgerskapMedTomLikNullOgFomUtenforPeriode = Statsborgerskap(
        "NOR",
        fom = null, tom = LocalDate.of(2015, 1, 1)
    )

    val statsborgerskapMedFomLikNullOgTomInnenforPeriode = Statsborgerskap(
        "SWE",
        fom = null, tom = LocalDate.of(2019, 1, 1)
    )

    val statsborgerskapUendretSisteÅret = Statsborgerskap(
        "NOR",
        fom = LocalDate.of(2017, 1, 20), tom = null
    )

    val statsborgerskapEndretSisteÅret = Statsborgerskap(
        "JPN",
        fom = LocalDate.of(2010, 1, 1), tom = LocalDate.of(2019, 1, 31)
    )
}
