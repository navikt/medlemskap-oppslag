package no.nav.medlemskap.regler.v1

import io.ktor.features.*
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.v1.lovvalg.ErBrukerBosattINorgeRegel
import no.nav.medlemskap.regler.v1.lovvalg.ErBrukerDoed

class ReglerForDoedsfall (
        ytelse: Ytelse,
        regelMap: Map<RegelId, Regel> = emptyMap()
) : Regler(ytelse, regelMap) {

    private fun hentHovedflyt(): Regelflyt {
        val erBrukerDoedRegelFlyt = lagRegelflyt(
                regel = hentRegel(RegelId.REGEL_13),
                hvisJa = regelflytJa(ytelse, RegelId.REGEL_DOED),
                hvisNei = regelflytNei(ytelse, RegelId.REGEL_DOED)
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
                        regelMap = lagRegelMap(datagrunnlag)
                )
            }
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                    ErBrukerDoed.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }


}