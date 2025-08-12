package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonUavklart
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa

class UDI3ArbeidsAdgang(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {

        val dekkerArbeidstillatelsenArbeidsperiodenRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_6_1),
            hvisJa = regelflytJa(ytelse, REGEL_ARBEIDSADGANG),
            hvisNei = konklusjonUavklart(ytelse, REGEL_ARBEIDSADGANG)
        )

        val harBrukerGyldigArbeidstillatelseIKontrollperiodeRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_6),
            hvisJa = regelflytJa(ytelse, REGEL_ARBEIDSADGANG),
            hvisNei = dekkerArbeidstillatelsenArbeidsperiodenRegelflyt
        )

        val erArbeidsadgangUavklartRegelFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_5),
            hvisJa = konklusjonUavklart(ytelse, REGEL_ARBEIDSADGANG),
            hvisNei = harBrukerGyldigArbeidstillatelseIKontrollperiodeRegelflyt
        )

        val harBrukerPermanentOppholdstillatelse = lagRegelflyt(
            regel = hentRegel(REGEL_31),
            hvisJa = regelflytJa(ytelse, REGEL_ARBEIDSADGANG),
            hvisNei = erArbeidsadgangUavklartRegelFlyt
        )

        return harBrukerPermanentOppholdstillatelse
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): UDI3ArbeidsAdgang {
            with(datagrunnlag) {
                return UDI3ArbeidsAdgang(
                    periode = periode,
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = overstyrteRegler
                )
            }
        }
    }
}