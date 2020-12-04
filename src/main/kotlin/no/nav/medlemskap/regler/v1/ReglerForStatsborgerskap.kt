package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.v1.grunnforordningen.ErBrukerEøsBorgerRegel
import no.nav.medlemskap.regler.v1.lovvalg.HarBrukerNorskStatsborgerskapRegel

class ReglerForStatsborgerskap(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel>,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelMap, overstyrteRegler) {

    override fun hentRegelflyter(): List<Regelflyt> {
        val harBrukerNorskStatsborgerskapFlyt = lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_11),
            hvisJa = regelflytJa(ytelse, RegelId.REGEL_STATSBORGERSKAP),
            hvisNei = regelflytJa(ytelse, RegelId.REGEL_STATSBORGERSKAP)
        )

        val erBrukerEØSborgerFlyt = lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_2),
            hvisJa = harBrukerNorskStatsborgerskapFlyt,
            hvisNei = regelflytJa(ytelse, RegelId.REGEL_STATSBORGERSKAP)
        )

        return listOf(erBrukerEØSborgerFlyt)
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForStatsborgerskap {
            with(datagrunnlag) {
                return ReglerForStatsborgerskap(
                    periode = periode,
                    ytelse = ytelse,
                    regelMap = lagRegelMap(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
                )
            }
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                ErBrukerEøsBorgerRegel.fraDatagrunnlag(datagrunnlag),
                HarBrukerNorskStatsborgerskapRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
