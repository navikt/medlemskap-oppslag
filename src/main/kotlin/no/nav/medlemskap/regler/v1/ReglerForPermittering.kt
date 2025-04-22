package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytUavklart
import no.nav.medlemskap.regler.common.Regler
import no.nav.medlemskap.regler.common.Svar


class ReglerForPermittering(
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {
        val HarPermitteringSiste12MånederFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_15),
            hvisJa = regelflytUavklart(ytelse, REGEL_FELLES_ARBEIDSFORHOLD),
            hvisNei = regelflytJa(ytelse, REGEL_PERMITTERING)
        )
        return HarPermitteringSiste12MånederFlyt
    }


    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForPermittering {
            with(datagrunnlag) {
                return ReglerForPermittering(
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
                )
            }
        }
    }
}
