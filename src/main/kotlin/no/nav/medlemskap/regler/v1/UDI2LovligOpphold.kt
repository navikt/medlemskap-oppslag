package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonUavklart
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa

class UDI2LovligOpphold(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {

        val harBrukerGyldigOppholdstillatelseIKontrollperiodeRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_3),
            hvisJa = regelflytJa(ytelse, REGEL_UDI_LOVLIG_OPPHOLD),
            hvisNei = konklusjonUavklart(ytelse, REGEL_UDI_LOVLIG_OPPHOLD),
        )


        val harIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_23),
            hvisJa = konklusjonUavklart(ytelse, REGEL_UDI_LOVLIG_OPPHOLD),
            hvisNei = harBrukerGyldigOppholdstillatelseIKontrollperiodeRegelflyt
        )


        return harIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisumFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): UDI2LovligOpphold {
            with(datagrunnlag) {
                return UDI2LovligOpphold(
                    periode = periode,
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = overstyrteRegler
                )
            }
        }
    }
}