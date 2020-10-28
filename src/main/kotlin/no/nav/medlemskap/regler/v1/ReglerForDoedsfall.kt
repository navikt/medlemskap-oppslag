package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.v1.lovvalg.ErBrukerDoedRegel

class ReglerForDoedsfall(
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel> = emptyMap(),
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelMap, overstyrteRegler) {

    private fun hentHovedflyt(): Regelflyt {
        val erBrukerDoedRegelFlyt = lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_13),
            hvisJa = regelflytJa(ytelse, RegelId.REGEL_DOED),
            hvisNei = regelflytJa(ytelse, RegelId.REGEL_DOED)
        )
        return erBrukerDoedRegelFlyt
    }

    override fun hentRegelflyter(): List<Regelflyt> {
        return listOf(hentHovedflyt())
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForDoedsfall {
            with(datagrunnlag) {
                return ReglerForDoedsfall(
                    ytelse = ytelse,
                    regelMap = lagRegelMap(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
                )
            }
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                ErBrukerDoedRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
