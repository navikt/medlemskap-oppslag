package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonUavklart
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa

class UDI1Validering(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {


        val harBrukerFlereOppholdstillatelserSomOverlapper = lagRegelflyt(
            regel = hentRegel(REGEL_19_2),
            hvisJa = konklusjonUavklart(ytelse, REGEL_UDI_VALIDERING),
            hvisNei = regelflytJa(ytelse, REGEL_UDI_VALIDERING),
        )

        val harBrukerOppholdPaSammeVilkarFlagg = lagRegelflyt(
            regel = hentRegel(REGEL_19_8),
            hvisJa = konklusjonUavklart(ytelse, REGEL_UDI_VALIDERING),
            hvisNei = harBrukerFlereOppholdstillatelserSomOverlapper
        )

        val erOppholdstillatelseUavklartRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_1),
            hvisJa = konklusjonUavklart(ytelse, REGEL_UDI_VALIDERING),
            hvisNei = harBrukerOppholdPaSammeVilkarFlagg
        )
        return erOppholdstillatelseUavklartRegelflyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): UDI1Validering {
            with(datagrunnlag) {
                return UDI1Validering(
                    periode = periode,
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = overstyrteRegler
                )
            }
        }
    }
}