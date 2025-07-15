package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonUavklart
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa

class UDI4BritiskeBorgere(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {

        val harBrukerEOSellerEFTAOppholdOgBritiskEktefelleRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_4),
            hvisJa = konklusjonUavklart(ytelse, REGEL_BRITISKEBORTERE),
            hvisNei = regelflytJa(ytelse, REGEL_BRITISKEBORTERE)
        )

        val harBritiskBrukerEOSellerEFTAOppholdRegelFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_30),
            hvisJa = konklusjonUavklart(ytelse, REGEL_BRITISKEBORTERE),
            hvisNei = harBrukerEOSellerEFTAOppholdOgBritiskEktefelleRegelflyt
        )

        return harBritiskBrukerEOSellerEFTAOppholdRegelFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): UDI4BritiskeBorgere {
            with(datagrunnlag) {
                return UDI4BritiskeBorgere(
                    periode = periode,
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = overstyrteRegler
                )
            }
        }
    }
}
