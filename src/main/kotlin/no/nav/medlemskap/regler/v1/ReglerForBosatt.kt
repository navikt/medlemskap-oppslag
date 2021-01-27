package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytUavklart
import no.nav.medlemskap.regler.v1.lovvalg.ErBrukerBosattINorgeRegel
import no.nav.medlemskap.regler.v1.lovvalg.HarBrukerJobbet80ProsentEllerMerSiste3MånedeneRegel

class ReglerForBosatt(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel>,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelMap, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {

        val harBrukerJobbet80ProsentEllerMerSiste3Månedeneflyt = lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_20),
            hvisJa = regelflytJa(ytelse, RegelId.REGEL_BOSATT),
            hvisNei = regelflytUavklart(ytelse, RegelId.REGEL_BOSATT)
        )

        val erBrukerBosattINorgeFlyt = lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_10),
            hvisJa = regelflytJa(ytelse, RegelId.REGEL_BOSATT),
            hvisNei = harBrukerJobbet80ProsentEllerMerSiste3Månedeneflyt
        )

        return erBrukerBosattINorgeFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForBosatt {
            with(datagrunnlag) {
                return ReglerForBosatt(
                    periode = periode,
                    ytelse = ytelse,
                    regelMap = lagRegelMap(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
                )
            }
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                ErBrukerBosattINorgeRegel.fraDatagrunnlag(datagrunnlag),
                HarBrukerJobbet80ProsentEllerMerSiste3MånedeneRegel.fraDatagrunnlag(datagrunnlag, RegelId.REGEL_20)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
