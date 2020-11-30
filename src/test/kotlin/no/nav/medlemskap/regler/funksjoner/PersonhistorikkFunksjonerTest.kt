package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.regler.funksjoner.PersonhistorikkFunksjoner.erBrukerDoedEtterPeriode
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PersonhistorikkFunksjonerTest {

    @Test
    fun `Liste med dødsfall før periode gir false`() {

        val doedsfallListe = listOf(doedsfallFoerInputPeriode)
        val kontrollperiode = InputPeriode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        assertFalse(doedsfallListe.erBrukerDoedEtterPeriode(kontrollperiode))
    }

    @Test
    fun `Liste med dødsfall i periode gir false`() {

        val doedsfallListe = listOf(doedfallEtterInputPeriode)
        val kontrollperiode = InputPeriode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )

        assertTrue(doedsfallListe.erBrukerDoedEtterPeriode(kontrollperiode))
    }

    @Test
    fun `Liste med dødsfall i etter gir true`() {

        val doedsfallListe = listOf(doedsfallFoerInputPeriode)
        val kontrollperiode = InputPeriode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )

        assertFalse(doedsfallListe.erBrukerDoedEtterPeriode(kontrollperiode))
    }
    @Test
    fun `Liste med dødsfall før OG etter  periode gir false`() {
        val doedsfallListe = listOf(doedsfallFoerInputPeriode, doedfallEtterInputPeriode)
        val kontrollperiode = InputPeriode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )

        assertFalse(doedsfallListe.erBrukerDoedEtterPeriode(kontrollperiode))
    }

    val doedfallEtterInputPeriode = LocalDate.of(2020, 1, 1)

    val doedsfallFoerInputPeriode = LocalDate.of(2018, 1, 1)
}
