package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt
import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonUavklart
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regler
import no.nav.medlemskap.regler.common.Svar

class ReglerForMaritim(
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {

        val erBrukerEØSborgerFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_7),
            hvisJa = konklusjonUavklart(ytelse, REGEL_MARITIM),
            hvisNei = konklusjonUavklart(ytelse, REGEL_MARITIM),
        )
        return erBrukerEØSborgerFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForMaritim {
            with(datagrunnlag) {
                return ReglerForMaritim(
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
                )
            }
        }
    }
}
