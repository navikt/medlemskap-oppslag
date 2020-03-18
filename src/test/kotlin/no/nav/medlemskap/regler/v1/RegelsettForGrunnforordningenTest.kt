package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Personfakta.Companion.initialiserFakta
import no.nav.medlemskap.regler.personer.Personleser
import no.nav.medlemskap.regler.v2.common.Svar
import no.nav.medlemskap.regler.v2.regler.ReglerForGrunnforordningen
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RegelsettForGrunnforordningenTest {

    private val personleser = Personleser()

    @Test
    fun `person med ett norsk statsborgerskap gir resultat ja`() {
        assertEquals(Svar.JA, evaluer(personleser.enkelNorsk()))
    }

    @Test
    fun `person med flere statsborgerskap i periode får nei`() {
        assertEquals(Svar.NEI, evaluer(personleser.norskMedFlereStatsborgerskap()))
    }

    @Test
    fun `person med flere statsborgerskap som overlapper periode får nei`() {
        assertEquals(Svar.NEI, evaluer(personleser.norskMedFlereStatsborgerskapUtenforPeriode()))
    }

    @Test
    fun `person med ett amerikansk statsborgerskap gir resuktat nei`() {
        assertEquals(Svar.NEI, evaluer(personleser.enkelAmerikansk()))
    }

    private fun evaluer(datagrunnlag: Datagrunnlag): Svar {
        val regelsett = ReglerForGrunnforordningen(initialiserFakta(datagrunnlag))
        return regelsett.hentHovedRegel().utfør(mutableListOf()).svar
    }

}
