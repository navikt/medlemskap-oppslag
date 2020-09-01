package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.v1.grunnforordningen.ErBrukerEøsBorgerRegel

class ReglerForEøsStatsborgerskap(
        val periode: InputPeriode,
        ytelse: Ytelse,
        regelMap: Map<RegelId, Regel>
) : Regler(ytelse, regelMap) {

    override fun hentRegelflyt(): Regelflyt {
        val erBrukerEØSborgerFlyt = lagRegelflyt(
                regel = hentRegel(RegelId.REGEL_2),
                hvisJa = regelflytJa(ytelse),
                hvisNei = regelflytNei(ytelse)
        )

        return erBrukerEØSborgerFlyt
    }


    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForEøsStatsborgerskap {
            with(datagrunnlag) {
                return ReglerForEøsStatsborgerskap(
                        periode = periode,
                        ytelse = ytelse,
                        regelMap = lagRegelMap(datagrunnlag)
                )
            }
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                    ErBrukerEøsBorgerRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
