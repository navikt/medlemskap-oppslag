package no.nav.medlemskap.domene

import junit.framework.Assert.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class PeriodeTest {

    @Test
    fun erGyldigPeriode() {
    }

    @Test
    fun overlapper_hvis_dato_er_lik_fom() {
        val periode = Periode(fom = LocalDate.of(2018, 2, 1), tom = LocalDate.of(2018, 2, 10))

        val dato = LocalDate.of(2018, 2, 1)
        assertTrue(periode.overlapper(dato))
    }

    @Test
    fun overlapper_hvis_dato_er_lik_tom() {
        val periode = Periode(fom = LocalDate.of(2018, 2, 1), tom = LocalDate.of(2018, 2, 10))

        val dato = LocalDate.of(2018, 2, 10)
        assertTrue(periode.overlapper(dato))
    }

    @Test
    fun testOverlapper() {
        // TODO
    }

    @Test
    fun encloses() {

        // TODO
    }

    @Test
    fun fomNotNull() {
        // TODO
    }

    @Test
    fun tomNotNull() {
        // TODO
    }

    @Test
    fun interval() {
        // TODO
    }
}
