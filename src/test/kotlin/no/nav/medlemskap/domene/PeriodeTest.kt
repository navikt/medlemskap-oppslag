package no.nav.medlemskap.domene

import junit.framework.Assert.assertFalse
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
    fun overlapper_ikke_hvis_dato_er_etter_tom() {
        val periode = Periode(fom = LocalDate.of(2018, 2, 1), tom = LocalDate.of(2018, 2, 10))

        val dato = LocalDate.of(2018, 2, 11)
        assertFalse(periode.overlapper(dato))
    }

    @Test
    fun overlapper_ikke_hvis_dato_er_før_fom() {
        val periode = Periode(fom = LocalDate.of(2018, 2, 1), tom = LocalDate.of(2018, 2, 10))

        val dato = LocalDate.of(2018, 1, 28)
        assertFalse(periode.overlapper(dato))
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
