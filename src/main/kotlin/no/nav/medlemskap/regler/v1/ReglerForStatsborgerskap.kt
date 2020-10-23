package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.v1.grunnforordningen.ErBrukerEøsBorgerRegel
import no.nav.medlemskap.regler.v1.lovvalg.HarBrukerNorskStatsborgerskapRegel

class ReglerForStatsborgerskap(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel>,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelMap, overstyrteRegler) {

    override fun kjørRegelflyter(): List<Resultat> {
        return listOf(kjørUavhengigeRegelflyterMedEttResultat(RegelId.REGEL_STATSBORGERSKAP))
    }

    override fun hentRegelflyter(): List<Regelflyt> {
        val harBrukerNorskStatsborgerskapFlyt = lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_11),
            hvisJa = regelflytJa(ytelse),
            hvisNei = regelflytJa(ytelse)
        )

        val erBrukerEØSborgerFlyt = lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_2),
            hvisJa = regelflytJa(ytelse),
            hvisNei = regelflytNei(ytelse)
        )

        return listOf(erBrukerEØSborgerFlyt, harBrukerNorskStatsborgerskapFlyt)
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
