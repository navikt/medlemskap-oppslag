package no.nav.medlemskap.regler.funksjoner

import junit.framework.Assert.assertEquals
import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.harEndretSisteÅret
import org.junit.jupiter.api.Test
import java.time.LocalDate

class StatsborgerskapFunksjonerTest {

    @Test
    fun `Endret statsborgerskap blir filtrert ut`() {
        val statsborgerskapListe = listOf(statsborgerskapUendretSisteÅret, statsborgerskapEndretSisteÅret)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )

        val filtrertListe = statsborgerskapListe.harEndretSisteÅret(kontrollperiode)
        assertEquals(1, filtrertListe.size)
    }

    @Test
    fun `Uendret statsborgerskap blir filtrert ut`() {
        val statsborgerskapListe = listOf(statsborgerskapUendretSisteÅret)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )

        val filtrertListe = statsborgerskapListe.harEndretSisteÅret(kontrollperiode)
        assertEquals(0, filtrertListe.size)
    }

    @Test
    fun `Statsborgerskap med fom lik null blir filtrert`() {
        val statsborgerskapListe = listOf(statsborgerskapMedFomLikNullOgTomInnenforPeriode)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        val filtrertListe = statsborgerskapListe.harEndretSisteÅret(kontrollperiode)
        assertEquals(1, filtrertListe.size)
    }

    @Test
    fun `Statsborgerskap med fom lik null og tom utenfor perioden blir filtrert`() {
        val statsborgerskapListe = listOf(statsborgerskapMedTomLikNullOgFomUtenforPeriode)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        val filtrertListe = statsborgerskapListe.harEndretSisteÅret(kontrollperiode)
        assertEquals(0, filtrertListe.size)
    }

    @Test
    fun `Statsborgerskap med tom lik null og fom innenfor perioden`() {
        val statsborgerskapListe = listOf(statsborgerskapMedTomLikNullOgFomInnenforPeriode)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        val filtrertListe = statsborgerskapListe.harEndretSisteÅret(kontrollperiode)
        assertEquals(1, filtrertListe.size)
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
