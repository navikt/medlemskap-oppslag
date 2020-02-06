package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Fakta.Companion.initialiserFakta
import no.nav.medlemskap.regler.common.Resultattype
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class RegelsettForVedtakTest {

    private val personleser = Personleser()

    @Test
    fun `person uten medl, gosys eller joark data får nei på manuelle vedtak`() {
        assertEquals(Resultattype.NEI, evaluer(personleser.enkelNorsk()))
    }

    @Test
    fun `person med vedtak i medl får ja på manuelle vedtak`() {
        assertEquals(Resultattype.JA, evaluer(personleser.amerikanskMedl()))
    }

    @Test
    fun `person med oppgave i gosys får ja på manuelle vedtak`() {
        assertEquals(Resultattype.JA, evaluer(personleser.amerikanskGosys()))
    }

    @Test
    fun `person med dokument i joark får ja på manuelle vedtak`() {
        assertEquals(Resultattype.JA, evaluer(personleser.amerikanskJoark()))
    }

    private fun evaluer(datagrunnlag: Datagrunnlag): Resultattype {
        val regelsett = RegelsettForVedtak(initialiserFakta(datagrunnlag))
        return regelsett.evaluer().resultat
    }

}
