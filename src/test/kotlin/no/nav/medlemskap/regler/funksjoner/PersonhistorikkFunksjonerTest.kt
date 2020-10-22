package no.nav.medlemskap.regler.funksjoner

import junit.framework.Assert
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.regler.funksjoner.PersonhistorikkFunksjoner.erBrukerDoedEtterPeriode
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
        Assert.assertFalse(doedsfallListe.erBrukerDoedEtterPeriode(kontrollperiode))
    }

    val doedsfallFoerInputPeriode = LocalDate.of(2018, 1, 1)
}
