package no.nav.medlemskap.regler.v2

import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.personer.Personleser
import no.nav.medlemskap.regler.v2.arbeidsforhold.RegelgruppeForArbeidsforhold
import org.junit.jupiter.api.Test

class RegelgruppeForSpesielleArbeidsforholdTest {

    private val personleser = Personleser()

    @Test
    fun test() {
        val regler = RegelgruppeForArbeidsforhold(Personfakta.initialiserFakta(personleser.enkelNorskUtenlandskSkip()))
        val resultat = regler.evaluer()
        println(objectMapper.writeValueAsString(resultat))
    }

}
