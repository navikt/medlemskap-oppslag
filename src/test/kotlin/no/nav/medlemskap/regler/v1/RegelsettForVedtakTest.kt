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
    fun `person med oppgave i gosys får ja på manuelle vedtak`() {
        assertEquals(Svar.JA, evaluerReglerForOpplysninger(personleser.amerikanskGosys()))
    }

    @Test
    fun `person med dokument i joark får ja på manuelle vedtak`() {
        assertEquals(Svar.JA, evaluerReglerForOpplysninger(personleser.amerikanskJoark()))
    }

    @Test
    fun `amerikansk person med vedtak i medl får uavklart på manuelle vedtak`() {
        assertSvar("OPPLYSNINGER-MEDL", Svar.JA, evaluer(personleser.amerikanskMedl()), Svar.UAVKLART)
    }

    @Test
    fun `norsk person med opplysninger i medl`() {
        assertSvar("OPPLYSNINGER-MEDL", Svar.JA, evaluer(personleser.norskMedOpplysningerIMedl()), Svar.UAVKLART)
    }

    @Test
    fun `norsk person med delvis sammenfallende medlemskap`() {
        assertSvar("OPPLYSNINGER-MEDL", Svar.JA, evaluer(personleser.norskMedMedlemskapDelvisInnenfor12MndPeriode()), Svar.UAVKLART)
    }

    private fun evaluerReglerForOpplysninger(datagrunnlag: Datagrunnlag): Svar {
        val regelsett = ReglerForRegistrerteOpplysninger(initialiserFakta(datagrunnlag))
        return regelsett.hentHovedRegel().utfør(mutableListOf()).svar
    }

}
