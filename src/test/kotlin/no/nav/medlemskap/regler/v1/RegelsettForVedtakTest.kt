package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.assertSvar
import no.nav.medlemskap.regler.common.Personfakta.Companion.initialiserFakta
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.evaluer
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RegelsettForVedtakTest {

    private val personleser = Personleser()

    @Test
    fun `person uten medl, gosys eller joark data får nei på manuelle vedtak`() {
        assertEquals(Svar.NEI, evaluerReglerForOpplysninger(personleser.enkelNorsk()))
    }

    @Test
    fun `person med data i medl får ja på manuelle vedtak`() {
        assertEquals(Svar.JA, evaluerReglerForOpplysninger(personleser.norskMedOpplysningerIMedl()))
    }

    @Test
    fun `person med oppgave i gosys får ja på manuelle vedtak`() {
        assertEquals(Svar.JA, evaluerReglerForOpplysninger(personleser.amerikanskGosys()))
    }

    @Test
    fun `person med dokument i joark får ja på manuelle vedtak`() {
        assertEquals(Svar.JA, evaluerReglerForOpplysninger(personleser.amerikanskJoark()))
    }

    private fun evaluerReglerForOpplysninger(datagrunnlag: Datagrunnlag): Svar {
        val regelsett = ReglerForRegistrerteOpplysninger(initialiserFakta(datagrunnlag))
        return regelsett.hentHovedRegel().utfør(mutableListOf()).svar
    }
}