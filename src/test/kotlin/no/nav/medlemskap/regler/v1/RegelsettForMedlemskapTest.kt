package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RegelsettForMedlemskapTest {

    private val personleser = Personleser()

    @Test
    fun `person med norsk statsborgerskap, kun arbeid i Norge, får ja`() {
        assertEquals(Svar.JA, evaluer(personleser.enkelNorskArbeid()))
    }

    private fun evaluer(datagrunnlag: Datagrunnlag): Svar {
        val regelsett = Hovedregler(Personfakta.initialiserFakta(datagrunnlag))
        val resultat = regelsett.kjørHovedregler()
        println(objectMapper.writeValueAsString(resultat))
        return resultat[0].svar
    }

}
