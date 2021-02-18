package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Regelflyt
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regler
import no.nav.medlemskap.regler.common.Svar

class ReglerForStatsborgerskap(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {
        val harBrukerNorskStatsborgerskapFlyt = lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_11),
            hvisJa = regelflytJa(ytelse, RegelId.REGEL_STATSBORGERSKAP),
            hvisNei = regelflytJa(ytelse, RegelId.REGEL_STATSBORGERSKAP)
        )

        val erBrukerEØSborgerFlyt = lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_2),
            hvisJa = harBrukerNorskStatsborgerskapFlyt,
            hvisNei = regelflytJa(ytelse, RegelId.REGEL_STATSBORGERSKAP)
        )

        return erBrukerEØSborgerFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForStatsborgerskap {
            with(datagrunnlag) {
                return ReglerForStatsborgerskap(
                    periode = periode,
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
                )
            }
        }
    }
}
