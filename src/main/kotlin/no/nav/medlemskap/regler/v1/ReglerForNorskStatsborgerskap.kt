package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.REGEL_11
import no.nav.medlemskap.regler.v1.lovvalg.HarBrukerNorskStatsborgerskapRegel

class ReglerForNorskStatsborgerskap(
        val periode: InputPeriode,
        ytelse: Ytelse,
        regelMap: Map<RegelId, Regel>
) : Regler(ytelse, regelMap) {

    override fun hentRegelflyt(): Regelflyt {
        val harBrukerNorskStatsborgerskapFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11),
                hvisJa = regelflytJa(ytelse),
                hvisNei = regelflytNei(ytelse)
        )

        return harBrukerNorskStatsborgerskapFlyt
    }


    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForNorskStatsborgerskap {
            with(datagrunnlag) {
                return ReglerForNorskStatsborgerskap(
                        periode = periode,
                        ytelse = ytelse,
                        regelMap = lagRegelMap(datagrunnlag)
                )
            }
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                    HarBrukerNorskStatsborgerskapRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
