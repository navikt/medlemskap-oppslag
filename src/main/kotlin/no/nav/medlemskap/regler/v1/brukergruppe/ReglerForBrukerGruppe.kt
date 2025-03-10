package no.nav.medlemskap.regler.v1.brukergruppe

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Regelflyt
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regler
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.v1.RegelFactory

class ReglerForBrukerGruppe(
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {
        val harBrukerArbeidsforhold = lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_17),
            hvisJa = regelflytJa(ytelse, RegelId.REGEL_BRUKERGRUPPE),
            hvisNei = regelflytJa(ytelse, RegelId.REGEL_BRUKERGRUPPE)
        )
        return harBrukerArbeidsforhold
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForBrukerGruppe {
            with(datagrunnlag) {
                return ReglerForBrukerGruppe(
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
                )
            }
        }
    }
}
