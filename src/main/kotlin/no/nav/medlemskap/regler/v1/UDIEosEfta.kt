package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonUavklart
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa

class UDIEosEfta(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {

        val harIkkeEosEftaOppholdstillatelse = lagRegelflyt(
            regel = hentRegel(REGEL_70),
            hvisJa = konklusjonUavklart(ytelse, REGEL_UDI_EOSEFTA),
            hvisNei = regelflytJa(ytelse, REGEL_UDI_EOSEFTA)
        )


        return harIkkeEosEftaOppholdstillatelse
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): UDIEosEfta {
            with(datagrunnlag) {
                return UDIEosEfta(
                    periode = periode,
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = overstyrteRegler
                )
            }
        }
    }
}