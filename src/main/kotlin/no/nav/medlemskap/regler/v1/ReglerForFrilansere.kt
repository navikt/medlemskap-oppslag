package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.REGEL_17
import no.nav.medlemskap.regler.common.RegelId.REGEL_FRILANSER
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytUavklart
import no.nav.medlemskap.regler.v1.frilanser.ErArbeidsforholdetFrilanserRegel

class ReglerForFrilansere(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel>,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelMap, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {
        val ErBrukerFrilanserFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_17),
            hvisJa = regelflytUavklart(ytelse, REGEL_FRILANSER),
            hvisNei = regelflytJa(ytelse, REGEL_FRILANSER)
        )

        return ErBrukerFrilanserFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag, overstyrteRegler: Map<RegelId, Svar> = emptyMap()): ReglerForFrilansere {

            return ReglerForFrilansere(
                periode = datagrunnlag.periode,
                ytelse = datagrunnlag.ytelse,
                regelMap = lagRegelMap(datagrunnlag),
                overstyrteRegler = overstyrteRegler
            )
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                ErArbeidsforholdetFrilanserRegel.fraDatagrunnlag(datagrunnlag)
            )
            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
