package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonUavklart
import no.nav.medlemskap.regler.v1.udi.DekkerOppholdstillatelseArbeidsperiodeRegel
import no.nav.medlemskap.regler.v1.udi.GyldigArbeidstillatelseIKontrollperiodeRegel
import no.nav.medlemskap.regler.v1.udi.GyldigOppholdstillatelseIKontrollperiodeRegel

class ReglerForAndreStatsborgere(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel>,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelMap, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {

        val harBrukerGyldigArbeidstillatelseIKontrollperiodeRegel = lagRegelflyt(
            regel = hentRegel(REGEL_19_3),
            hvisJa = konklusjonUavklart(ytelse, REGEL_ANDRE_BORGERE),
            hvisNei = konklusjonUavklart(ytelse, REGEL_ANDRE_BORGERE)
        )

        val dekkerOppholdstillatelseArbeidsperiodeRegel = lagRegelflyt(
            regel = hentRegel(REGEL_19_2),
            hvisJa = harBrukerGyldigArbeidstillatelseIKontrollperiodeRegel,
            hvisNei = konklusjonUavklart(ytelse, REGEL_ANDRE_BORGERE)
        )

        val harBrukerGyldigOppholdstillatelseIKontrollperiodeRegel = lagRegelflyt(
            regel = hentRegel(REGEL_19_1),
            hvisJa = harBrukerGyldigArbeidstillatelseIKontrollperiodeRegel,
            hvisNei = dekkerOppholdstillatelseArbeidsperiodeRegel,
            hvisUavklart = konklusjonUavklart(ytelse, REGEL_ANDRE_BORGERE)
        )

        return harBrukerGyldigOppholdstillatelseIKontrollperiodeRegel
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForAndreStatsborgere {
            with(datagrunnlag) {
                return ReglerForAndreStatsborgere(
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
                DekkerOppholdstillatelseArbeidsperiodeRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
