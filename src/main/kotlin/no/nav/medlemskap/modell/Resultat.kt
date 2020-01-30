package no.nav.medlemskap.modell

/*
package no.nav.medlemskap.modell

import no.nav.medlemskap.domene.Personhistorikk
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.nare.core.evaluations.Evaluering

data class Resultat(val datagrunnlag: Datagrunnlag, val evaluering: Evaluering)
*/

data class Resultat(
        val identifikator: String,
        val avklaring: String,
        val resultat: Resultattype,
        val beskrivelse: String,
        val delresultat: List<Resultat>
)


enum class Resultattype {
    JA, NEI, UAVKLART
}
