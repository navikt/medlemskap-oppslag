package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Personfakta.Companion.initialiserFakta
import no.nav.medlemskap.regler.common.Resultattype
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RegelsettForGrunnforordningenTest {

    private val personleser = Personleser()

    @Test
    fun `person med ett norsk statsborgerskap gir resultat ja`() {
        assertEquals(Resultattype.JA, evaluer(personleser.enkelNorsk()))
    }

    @Test
    fun `person med flere statsborgerskap i periode får nei`() {
        assertEquals(Resultattype.NEI, evaluer(personleser.norskMedFlereStatsborgerskap()))
    }

    @Test
    fun `person med flere statsborgerskap som overlapper periode får nei`() {
        assertEquals(Resultattype.NEI, evaluer(personleser.norskMedFlereStatsborgerskapUtenforPeriode()))
    }

    @Test
    fun `person med ett amerikansk statsborgerskap gir resuktat nei`() {
        assertEquals(Resultattype.NEI, evaluer(personleser.enkelAmerikansk()))
    }

    private fun evaluer(datagrunnlag: Datagrunnlag): Resultattype {
        val regelsett = RegelsettForGrunnforordningen()
        return regelsett.evaluer(initialiserFakta(datagrunnlag)).resultat
    }

}
