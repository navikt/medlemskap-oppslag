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

        val erBrukerBritiskEllerSveitsiskBorgerRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_4),
            hvisJa = konklusjonUavklart(ytelse, REGEL_OPPHOLDSTILLATELSE),
            hvisNei = regelflytJa(ytelse, REGEL_OPPHOLDSTILLATELSE)
        )

        val dekkerArbeidstillatelsenArbeidsperiodenRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_3_1),
            hvisJa = erBrukerBritiskEllerSveitsiskBorgerRegelflyt,
            hvisNei = konklusjonUavklart(ytelse, REGEL_OPPHOLDSTILLATELSE)
        )

        val harBrukerGyldigArbeidstillatelseIKontrollperiodeRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_3),
            hvisJa = erBrukerBritiskEllerSveitsiskBorgerRegelflyt,
            hvisNei = dekkerArbeidstillatelsenArbeidsperiodenRegelflyt
        )

        val dekkerOppholdstillatelseArbeidsperiodeRegel = lagRegelflyt(
            regel = hentRegel(REGEL_19_2_1),
            hvisJa = harBrukerGyldigArbeidstillatelseIKontrollperiodeRegelflyt,
            hvisNei = konklusjonUavklart(ytelse, REGEL_OPPHOLDSTILLATELSE)
        )

        val harBrukerGyldigOppholdstillatelseIKontrollperiodeRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_2),
            hvisJa = harBrukerGyldigArbeidstillatelseIKontrollperiodeRegelflyt,
            hvisNei = dekkerOppholdstillatelseArbeidsperiodeRegel,
            hvisUavklart = konklusjonUavklart(ytelse, REGEL_OPPHOLDSTILLATELSE)
        )

        val erOppholdstillatelseUavklartRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_1),
            hvisJa = konklusjonUavklart(ytelse, REGEL_OPPHOLDSTILLATELSE),
            hvisNei = harBrukerGyldigOppholdstillatelseIKontrollperiodeRegelflyt
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
