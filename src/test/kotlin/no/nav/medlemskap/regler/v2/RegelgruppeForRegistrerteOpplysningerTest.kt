package no.nav.medlemskap.regler.v2

import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.personer.Personleser
import no.nav.medlemskap.regler.v2.registrerte_opplysninger.RegelgruppeForRegistrerteOpplysninger
import org.junit.jupiter.api.Test

class RegelgruppeForRegistrerteOpplysningerTest {

    private val personleser = Personleser()

    @Test
    fun test() {
        val regler = RegelgruppeForRegistrerteOpplysninger(Personfakta.initialiserFakta(personleser.enkelNorsk()))
        val resultat = regler.evaluer()
        println(objectMapper.writeValueAsString(resultat))
    }


}
