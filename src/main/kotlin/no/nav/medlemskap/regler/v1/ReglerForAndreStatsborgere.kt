package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytUavklart
import no.nav.medlemskap.regler.v1.lovvalg.ErBrukerBosattINorgeRegel
import no.nav.medlemskap.regler.v1.lovvalg.HarBrukerJobbetUtenforNorgeRegel
import no.nav.medlemskap.regler.v1.udi.HarBrukerJobbet80ProsentEllerMerSiste3MånedeneRegel

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
            hvisNei = regelflytUavklart(ytelse, REGEL_ANDRE_BORGERE)
        )

        val erBrukerBosattINorgeRegelflyt = lagRegelflyt(
            regel = hentRegel(REGEL_10),
            hvisJa = arbeidUtenforNorgeRegelflyt,
            hvisNei = harBrukerJobbet80ProsentEllerMerSiste3Månedeneflyt
        )

        return erBrukerBosattINorgeRegelflyt
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
                HarBrukerJobbetUtenforNorgeRegel.fraDatagrunnlag(datagrunnlag),
                HarBrukerJobbet80ProsentEllerMerSiste3MånedeneRegel.fraDatagrunnlag(datagrunnlag, REGEL_20),
                ErBrukerBosattINorgeRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
