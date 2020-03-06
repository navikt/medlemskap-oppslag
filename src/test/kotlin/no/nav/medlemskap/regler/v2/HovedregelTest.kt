package no.nav.medlemskap.regler.v2

import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Test

class HovedregelTest {
    private val personleser = Personleser()

    @Test
    fun test() {
        val hovedregler = Hovedregler(Personfakta.initialiserFakta(personleser.enkelNorskArbeid()))
        val resultat = hovedregler.evaluer()
        println(objectMapper.writeValueAsString(resultat))
    }

}
