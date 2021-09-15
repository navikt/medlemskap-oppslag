package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.RegelId.REGEL_11_2_temp
import no.nav.medlemskap.regler.common.RegelId.REGEL_TREDJELANDSBORGER_FAMILIE
import no.nav.medlemskap.regler.common.Regelflyt
import no.nav.medlemskap.regler.common.Regler
import no.nav.medlemskap.regler.common.Svar

class ReglerForTredjelandsborgerFamilie(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {

        val erEktefelleEØSborgerFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_11_2_temp),
            hvisJa = Regelflyt.regelflytJa(ytelse, REGEL_TREDJELANDSBORGER_FAMILIE),
            hvisNei = Regelflyt.regelflytJa(ytelse, REGEL_TREDJELANDSBORGER_FAMILIE)
        )

        return erEktefelleEØSborgerFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForTredjelandsborgerFamilie {
            with(datagrunnlag) {
                return ReglerForTredjelandsborgerFamilie(
                    periode = periode,
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
                )
            }
        }
    }
}
