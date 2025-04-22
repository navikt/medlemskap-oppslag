package no.nav.medlemskap.regler.v1.regelflyt.arbeid

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Regelflyt
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytUavklart
import no.nav.medlemskap.regler.common.Regler
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.v1.RegelFactory

class ReglerForUtenlandsforhold(
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {
        val ErPeriodenForUtenlandsoppholdetInnenforSiste12MånederFlyt = lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_25),
            hvisJa = regelflytUavklart(ytelse, RegelId.REGEL_UTENLANDSFORHOLD),
            hvisNei = regelflytJa(ytelse, RegelId.REGEL_UTENLANDSFORHOLD),
        )

        val HarBrukerUtenlandsoppholdIArbeidsforholdetFlyt = lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_22),
            hvisJa = ErPeriodenForUtenlandsoppholdetInnenforSiste12MånederFlyt,
            hvisNei = regelflytJa(ytelse, RegelId.REGEL_UTENLANDSFORHOLD),
        )
        return HarBrukerUtenlandsoppholdIArbeidsforholdetFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForUtenlandsforhold {
            with(datagrunnlag) {
                return ReglerForUtenlandsforhold(
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
                )
            }
        }
    }
}
