package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Journalpost
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.funksjoner.JoarkFunksjoner.finnesDokumenterMedTillatteTeamer

class FinnesOpplysningerIJoarkRegel(
        ytelse: Ytelse,
        private val dokument: List<Journalpost> = emptyList()
) : BasisRegel(RegelId.REGEL_C, ytelse) {

    override fun operasjon(): Resultat {
        return when {
            dokument.finnesDokumenterMedTillatteTeamer() -> ja()
            else -> nei()
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): FinnesOpplysningerIJoarkRegel {
            return FinnesOpplysningerIJoarkRegel(
                    ytelse = datagrunnlag.ytelse,
                    dokument = datagrunnlag.dokument
            )
        }
    }
}