package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Personfakta.Companion.initialiserFakta
import org.junit.jupiter.api.Assertions.assertEquals

import no.nav.medlemskap.regler.assertSvar
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.evaluer
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Test



class RegelsettForHovedreglerTest {

    private val personleser = Personleser()


    @Test
    fun `person med noe i medl får uavklart konklusjon`() {
        assertEquals(Svar.UAVKLART, evaluer(personleser.norskMedMedlOpplysninger()))
    }

    @Test
    fun `person uten noe i medl, men amerikansk får uvaklart`() {
        assertEquals(Svar.UAVKLART, evaluer(personleser.amerikanskUtenMedlOpplysninger()))
    }

    private fun evaluer(datagrunnlag: Datagrunnlag): Svar {
        val regelsett = Hovedregler(initialiserFakta(datagrunnlag))
        return regelsett.hentHovedRegel().utfør(mutableListOf()).svar
    }

}
