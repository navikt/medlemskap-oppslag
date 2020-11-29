package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytUavklart
import no.nav.medlemskap.regler.v1.validering.InputDatoValideringRegel

class ReglerForRequestValidering(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel>,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelMap, overstyrteRegler) {

    private fun hentHovedflyt(): Regelflyt {

        return lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_0_1),
            hvisJa = regelflytJa(ytelse, RegelId.REGEL_REQUEST_VALIDERING),
            hvisNei = regelflytUavklart(ytelse, RegelId.REGEL_REQUEST_VALIDERING)
        )
    }

    override fun hentRegelflyter(): List<Regelflyt> {
        return listOf(hentHovedflyt())
    }

    fun kjørRegel(): Resultat {
        return hentHovedflyt().utfør()
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForRequestValidering {
            with(datagrunnlag) {
                return ReglerForRequestValidering(
                    periode = periode,
                    ytelse = ytelse,
                    regelMap = lagRegelMap(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
                )
            }
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                InputDatoValideringRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
