package no.nav.medlemskap.domene.personhistorikk

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap.Companion.erAnnenStatsborger
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap.Companion.harEndretSisteÅret
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class StatsborgerskapTest {

    @Test
    fun overlapper() {
        val statsborgerskap = Statsborgerskap("NOR", LocalDate.of(2015, 1, 1), null)

        assertThat(statsborgerskap.overlapper(LocalDate.of(2016, 1, 1))).isTrue()
    }

    @Test
    fun erAnnenStatsborger_norsk_borger_er_ikke_annen_statsborger() {
        val statsborgerskap = listOf(
            Statsborgerskap("NOR", null, null),
            Statsborgerskap("USA", null, null)
        )

        val inputPeriode = InputPeriode(LocalDate.of(2019, 1, 1), LocalDate.of(2020, 12, 31))
        val førsteDagForYtelse = LocalDate.of(2019, 1, 1)

        assertThat(statsborgerskap.erAnnenStatsborger(inputPeriode, førsteDagForYtelse)).isFalse()
    }

    @Test
    fun erAnnenStatsborger_belgisk_borger_er_ikke_annen_statsborger() {
        val statsborgerskap = listOf(
            Statsborgerskap("BEL", null, null)
        )

        val inputPeriode = InputPeriode(LocalDate.of(2019, 1, 1), LocalDate.of(2020, 12, 31))
        val førsteDagForYtelse = LocalDate.of(2019, 1, 1)

        assertThat(statsborgerskap.erAnnenStatsborger(inputPeriode, førsteDagForYtelse)).isFalse()
    }

    @Test
    fun erAnnenStatsborger_amerikansk_borger_er_annen_statsborger() {
        val statsborgerskap = listOf(
            Statsborgerskap("USA", null, null)
        )

        val inputPeriode = InputPeriode(LocalDate.of(2019, 1, 1), LocalDate.of(2020, 12, 31))
        val førsteDagForYtelse = LocalDate.of(2019, 1, 1)

        assertThat(statsborgerskap.erAnnenStatsborger(inputPeriode, førsteDagForYtelse)).isTrue()
    }

    @Test
    fun `Endret statsborgerskap gir true`() {
        val statsborgerskapListe = listOf(statsborgerskapUendretSisteÅret, statsborgerskapEndretSisteÅret)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        Assertions.assertTrue(statsborgerskapListe.harEndretSisteÅret(kontrollperiode))
    }

    @Test
    fun `Uendret statsborgerskap gir false`() {
        val statsborgerskapListe = listOf(statsborgerskapUendretSisteÅret)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        Assertions.assertFalse(statsborgerskapListe.harEndretSisteÅret(kontrollperiode))
    }

    @Test
    fun `Statsborgerskap med fom lik null og innenfor perioden gir true`() {
        val statsborgerskapListe = listOf(statsborgerskapMedFomLikNullOgTomInnenforPeriode)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        Assertions.assertTrue(statsborgerskapListe.harEndretSisteÅret(kontrollperiode))
    }

    @Test
    fun `Statsborgerskap med fom lik null og tom utenfor perioden gir false`() {
        val statsborgerskapListe = listOf(statsborgerskapMedTomLikNullOgFomUtenforPeriode)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        Assertions.assertFalse(statsborgerskapListe.harEndretSisteÅret(kontrollperiode))
    }

    @Test
    fun `Statsborgerskap med tom lik null og fom innenfor perioden gir true`() {
        val statsborgerskapListe = listOf(statsborgerskapMedTomLikNullOgFomInnenforPeriode)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        Assertions.assertTrue(statsborgerskapListe.harEndretSisteÅret(kontrollperiode))
    }

    companion object {
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
}
