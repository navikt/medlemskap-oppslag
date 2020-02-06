package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Fakta
import no.nav.medlemskap.regler.common.Resultattype
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class RegelsettForMedlemskapTest {

    private val personleser = Personleser()

    @Test
    fun `person med norsk statsborgerskap, kun arbeid i Norge, får ja`() {
        assertEquals(Resultattype.JA, evaluer(personleser.enkelNorskArbeid()))
    }

    private fun evaluer(datagrunnlag: Datagrunnlag): Resultattype {
        val regelsett = RegelsettForMedlemskap(Fakta.initialiserFakta(datagrunnlag))
        val resultat = regelsett.evaluer()
        println(objectMapper.writeValueAsString(resultat))
        return resultat.resultat
    }

}
