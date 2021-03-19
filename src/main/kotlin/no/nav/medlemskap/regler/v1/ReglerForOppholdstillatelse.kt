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

class ReglerForOppholdstillatelse(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {

        val dekkerArbeidstillatelsenArbeidsperiodenRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_6_1),
            hvisJa = regelflytJa(ytelse, REGEL_OPPHOLDSTILLATELSE),
            hvisNei = konklusjonUavklart(ytelse, REGEL_OPPHOLDSTILLATELSE)
        )

        val harBrukerGyldigArbeidstillatelseIKontrollperiodeRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_6),
            hvisJa = regelflytJa(ytelse, REGEL_OPPHOLDSTILLATELSE),
            hvisNei = dekkerArbeidstillatelsenArbeidsperiodenRegelflyt
        )

        val erBrukerBritiskEllerSveitsiskBorgerRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_7),
            hvisJa = konklusjonUavklart(ytelse, REGEL_OPPHOLDSTILLATELSE),
            hvisNei = konklusjonUavklart(ytelse, REGEL_OPPHOLDSTILLATELSE)
        )

        val dekkerOppholdstillatelseArbeidsperiodeRegel = lagRegelflyt(
            regel = hentRegel(REGEL_19_3_1),
            hvisJa = harBrukerGyldigArbeidstillatelseIKontrollperiodeRegelflyt,
            hvisNei = erBrukerBritiskEllerSveitsiskBorgerRegelflyt
        )

        val harBrukerOppholdPåSammeVilkårFlagg = lagRegelflyt(
            regel = hentRegel(REGEL_19_8),
            hvisJa = konklusjonUavklart(ytelse, REGEL_OPPHOLDSTILLATELSE),
            hvisNei = dekkerOppholdstillatelseArbeidsperiodeRegel
        )

        val harBrukerGyldigOppholdstillatelseIKontrollperiodeRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_3),
            hvisJa = harBrukerGyldigArbeidstillatelseIKontrollperiodeRegelflyt,
            hvisNei = harBrukerOppholdPåSammeVilkårFlagg,
            hvisUavklart = konklusjonUavklart(ytelse, REGEL_OPPHOLDSTILLATELSE)
        )

        val harBrukerFlereOppholdstillatelserSomOverlapper = lagRegelflyt(
            regel = hentRegel(REGEL_19_2),
            hvisJa = konklusjonUavklart(ytelse, REGEL_OPPHOLDSTILLATELSE),
            hvisNei = harBrukerGyldigOppholdstillatelseIKontrollperiodeRegelflyt
        )

        val erOppholdstillatelseUavklartRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_1),
            hvisJa = konklusjonUavklart(ytelse, REGEL_OPPHOLDSTILLATELSE),
            hvisNei = harBrukerFlereOppholdstillatelserSomOverlapper
        )

        return erOppholdstillatelseUavklartRegelflyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForOppholdstillatelse {
            with(datagrunnlag) {
                return ReglerForOppholdstillatelse(
                    periode = periode,
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = overstyrteRegler
                )
            }
        }
    }
}
