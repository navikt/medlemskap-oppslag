package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.v1.overstyring.OverstyringRegel

class ReglerForOverstyring(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel>,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelMap, overstyrteRegler) {

    private fun hentHovedflyt(): Regelflyt {

        return lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_0_5),
            hvisJa = regelflytJa(ytelse, RegelId.REGEL_OVERSTYRING),
            hvisNei = regelflytJa(ytelse, RegelId.REGEL_OVERSTYRING)
        )
    }

    override fun hentRegelflyter(): List<Regelflyt> {
        return listOf(hentHovedflyt())
    }

    fun reglerSomSkalOverstyres(resultater: List<Resultat>): Map<RegelId, Svar> {
        val overstyringResultat = resultater.flatMap { it.delresultat }.find { it.regelId == RegelId.REGEL_0_5 }

        if (overstyringResultat?.svar == Svar.JA) {
            return OverstyringRegel.reglerSomSkalOverstyres()
        } else {
            return overstyrteRegler
        }
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForOverstyring {
            with(datagrunnlag) {
                return ReglerForOverstyring(
                    periode = periode,
                    ytelse = ytelse,
                    regelMap = lagRegelMap(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
                )
            }
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                OverstyringRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
