package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.REGEL_15
import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonUavklart
import no.nav.medlemskap.regler.v1.udi.GyldigOppholdstillatelseIKontrollperiodeRegel

class ReglerForAndreStatsborgere(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel>,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelMap, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {

        val harBrukerGyldigOppholdstillatelseIKontrollperiodeRegel = lagRegelflyt(
            regel = hentRegel(REGEL_15),
            hvisJa = konklusjonUavklart(ytelse, RegelId.REGEL_ANDRE_BORGERE),
            hvisNei = konklusjonUavklart(ytelse, RegelId.REGEL_ANDRE_BORGERE),
            hvisUavklart = konklusjonUavklart(ytelse, RegelId.REGEL_ANDRE_BORGERE)
        )

        return harBrukerGyldigOppholdstillatelseIKontrollperiodeRegel
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForAndreStatsborgere {
            with(datagrunnlag) {
                return ReglerForAndreStatsborgere(
                    periode = periode,
                    ytelse = ytelse,
                    regelMap = lagRegelMap(datagrunnlag),
                    overstyrteRegler = overstyrteRegler
                )
            }
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                GyldigOppholdstillatelseIKontrollperiodeRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
