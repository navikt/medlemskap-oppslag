package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonUavklart
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa

class UDIBritiskeBorgere(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {

        val harBrukerEOSellerEFTAOppholdOgBritiskEktefelleRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_4),
            hvisJa = konklusjonUavklart(ytelse, REGEL_BRITISKE_BORGERE),
            hvisNei = regelflytJa(ytelse, REGEL_BRITISKE_BORGERE)
        )

        val harBritiskBrukerEOSellerEFTAOppholdRegelFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_30),
            hvisJa = konklusjonUavklart(ytelse, REGEL_BRITISKE_BORGERE),
            hvisNei = harBrukerEOSellerEFTAOppholdOgBritiskEktefelleRegelflyt
        )

        return harBritiskBrukerEOSellerEFTAOppholdRegelFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): UDIBritiskeBorgere {
            with(datagrunnlag) {
                return UDIBritiskeBorgere(
                    periode = periode,
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = overstyrteRegler
                )
            }
        }
    }
}