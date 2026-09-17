package no.nav.medlemskap.regler.v1.frilanser

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt
import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonJa
import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonUavklart
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regler
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.v1.RegelFactory

class ReglerForFrilanser(
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {
        val ErBrukerFrilanserFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_16),
            hvisJa = konklusjonUavklart(ytelse, REGEL_FRILANSER),
            hvisNei = regelflytJa(ytelse, REGEL_FRILANSER)
        )
        return ErBrukerFrilanserFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForFrilanser {
            with(datagrunnlag) {
                return ReglerForFrilanser(
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
                )
            }
        }
    }
}
