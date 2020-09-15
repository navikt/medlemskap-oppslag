package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.v1.lovvalg.ErBrukerBosattINorgeRegel

class ReglerForBosatt(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel>
) : Regler(ytelse, regelMap) {

    private fun hentHovedflyt(): Regelflyt {
        val erBrukerBosattINorgeFlyt = lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_10),
            hvisJa = regelflytJa(ytelse),
            hvisNei = regelflytUavklart(ytelse)
        )

        return erBrukerBosattINorgeFlyt
    }

    override fun hentRegelflyter(): List<Regelflyt> {
        return listOf(hentHovedflyt())
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForBosatt {
            with(datagrunnlag) {
                return ReglerForBosatt(
                    periode = periode,
                    ytelse = ytelse,
                    regelMap = lagRegelMap(datagrunnlag)
                )
            }
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                ErBrukerBosattINorgeRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
