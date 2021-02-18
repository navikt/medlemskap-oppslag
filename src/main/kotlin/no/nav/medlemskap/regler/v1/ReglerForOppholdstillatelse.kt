package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonUavklart
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytUavklart
import no.nav.medlemskap.regler.v1.statsborgerskap.ErBrukerBritiskBorgerRegel
import no.nav.medlemskap.regler.v1.udi.*

class ReglerForOppholdstillatelse(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel>,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelMap, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {

        val erBrukerBritiskEllerSveitsiskBorgerRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_4),
            hvisJa = regelflytUavklart(ytelse, REGEL_OPPHOLDSTILLATELSE),
            hvisNei = regelflytJa(ytelse, REGEL_OPPHOLDSTILLATELSE)
        )

        val dekkerArbeidstillatelsenArbeidsperiodenRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_3_1),
            hvisJa = erBrukerBritiskEllerSveitsiskBorgerRegelflyt,
            hvisNei = regelflytUavklart(ytelse, REGEL_OPPHOLDSTILLATELSE)
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
                    regelMap = lagRegelMap(datagrunnlag),
                    overstyrteRegler = overstyrteRegler
                )
            }
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                GyldigOppholdstillatelseIKontrollperiodeRegel.fraDatagrunnlag(datagrunnlag),
                GyldigArbeidstillatelseIKontrollperiodeRegel.fraDatagrunnlag(datagrunnlag),
                ErBrukerBritiskBorgerRegel.fraDatagrunnlag(datagrunnlag),
                DekkerOppholdstillatelseArbeidsperiodeRegel.fraDatagrunnlag(datagrunnlag),
                DekkerArbeidstillatelsenArbeidsperiodenRegel.fraDatagrunnlag(datagrunnlag),
                ErOppholdstillatelseUavklartRegel.fraDatagrunnlag(datagrunnlag),
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}