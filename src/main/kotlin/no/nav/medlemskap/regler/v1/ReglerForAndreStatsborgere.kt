package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytUavklart
import no.nav.medlemskap.regler.v1.lovvalg.HarBrukerJobbetUtenforNorgeRegel
import no.nav.medlemskap.regler.v1.statsborgerskap.ErBrukerBritiskBorgerRegel
import no.nav.medlemskap.regler.v1.udi.*

class ReglerForAndreStatsborgere(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel>,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelMap, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {
        val arbeidUtenforNorgeRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_9),
            hvisJa = regelflytUavklart(ytelse, REGEL_ANDRE_BORGERE),
            hvisNei = regelflytJa(ytelse, REGEL_ANDRE_BORGERE)
        )

        val harBrukerJobbet80ProsentEllerMerSiste3Månedeneflyt = lagRegelflyt(
            regel = hentRegel(REGEL_20),
            hvisJa = arbeidUtenforNorgeRegelflyt,
            hvisNei = regelflytUavklart(ytelse, REGEL_BOSATT)
        )

        val erBrukerBritiskEllerSveitsiskBorgerRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_5),
            hvisJa = regelflytUavklart(ytelse, REGEL_ANDRE_BORGERE),
            hvisNei = harBrukerJobbet80ProsentEllerMerSiste3Månedeneflyt
        )

        val dekkerArbeidstillatelsenArbeidsperiodenRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_4),
            hvisJa = erBrukerBritiskEllerSveitsiskBorgerRegelflyt,
            hvisNei = regelflytUavklart(ytelse, REGEL_ANDRE_BORGERE)
        )

        val harBrukerGyldigArbeidstillatelseIKontrollperiodeRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_3),
            hvisJa = erBrukerBritiskEllerSveitsiskBorgerRegelflyt,
            hvisNei = dekkerArbeidstillatelsenArbeidsperiodenRegelflyt
        )

        val dekkerOppholdstillatelseArbeidsperiodeRegel = lagRegelflyt(
            regel = hentRegel(REGEL_19_2),
            hvisJa = harBrukerGyldigArbeidstillatelseIKontrollperiodeRegelflyt,
            hvisNei = regelflytUavklart(ytelse, REGEL_ANDRE_BORGERE)
        )

        val harBrukerGyldigOppholdstillatelseIKontrollperiodeRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_1),
            hvisJa = harBrukerGyldigArbeidstillatelseIKontrollperiodeRegelflyt,
            hvisNei = dekkerOppholdstillatelseArbeidsperiodeRegel,
            hvisUavklart = regelflytUavklart(ytelse, REGEL_ANDRE_BORGERE)
        )

        val erOppholdstillatelseUavklartRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_19_6),
            hvisJa = regelflytUavklart(ytelse, REGEL_ANDRE_BORGERE),
            hvisNei = harBrukerGyldigOppholdstillatelseIKontrollperiodeRegelflyt
        )

        return erOppholdstillatelseUavklartRegelflyt
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
                ErBrukerBritiskBorgerRegel.fraDatagrunnlag(datagrunnlag),
                HarBrukerJobbetUtenforNorgeRegel.fraDatagrunnlag(datagrunnlag),
                DekkerOppholdstillatelseArbeidsperiodeRegel.fraDatagrunnlag(datagrunnlag),
                DekkerArbeidstillatelsenArbeidsperiodenRegel.fraDatagrunnlag(datagrunnlag),
                ErOppholdstillatelseUavklartRegel.fraDatagrunnlag(datagrunnlag),
                HarBrukerJobbet80ProsentEllerMerSiste3MånedeneRegel.fraDatagrunnlag(datagrunnlag, REGEL_20)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
