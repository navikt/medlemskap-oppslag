package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Personfakta.Companion.initialiserFakta
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.personer.Personleser
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
    fun `person med ulike EØS statsborgerskap på sjekk-datoer får ja`() {
        assertEquals(Svar.JA, evaluer(personleser.norskMedUlikeEøsStatsborgerskap()))
    }

    @Test
    fun `person med ett amerikansk statsborgerskap gir resultat nei`() {
        assertEquals(Svar.NEI, evaluer(personleser.enkelAmerikansk()))
    }

    @Test
    fun `person med dobbelt statsborgerskap der det ene er norsk, gir ja`() {
        assertEquals(Svar.JA, evaluer(personleser.norskDobbeltStatsborgerskap()))
    }

    private fun evaluer(datagrunnlag: Datagrunnlag): Svar {
        val regelsett = ReglerForGrunnforordningen(initialiserFakta(datagrunnlag))
        return regelsett.hentHovedRegel().utfør(mutableListOf()).svar
    }

}
