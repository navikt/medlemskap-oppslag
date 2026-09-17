package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytUavklart

class ReglerForBrukersvarArbeid(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {
        val harBrukerJobbetUtenforNorgeFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_9),
            hvisJa = regelflytUavklart(ytelse, REGEL_BRUKERSVAR_ARBEID),
            hvisNei = regelflytJa(ytelse, REGEL_BRUKERSVAR_ARBEID),
        )

        return harBrukerJobbetUtenforNorgeFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForBrukersvarArbeid {
            with(datagrunnlag) {
                return ReglerForBrukersvarArbeid(
                    periode = periode,
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
                )
            }
        }
    }
}
