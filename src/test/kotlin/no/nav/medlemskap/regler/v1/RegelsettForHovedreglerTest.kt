package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class RegelsettForHovedreglerTest {

    private val personleser = Personleser()


    @Test
    fun `person med noe i medl får uavklart konklusjon`() {
        assertEquals(Svar.UAVKLART, evaluer(personleser.norskMedMedlOpplysninger()))
    }

    @Test
    fun `person uten noe i medl, men amerikansk får uavklart`() {
        assertEquals(Svar.UAVKLART, evaluer(personleser.amerikanskUtenMedlOpplysninger()))
    }

    private fun evaluer(datagrunnlag: Datagrunnlag): Svar {
        val regelsett = Hovedregler(datagrunnlag)
        return regelsett.kjørHovedregler().svar
    }
}