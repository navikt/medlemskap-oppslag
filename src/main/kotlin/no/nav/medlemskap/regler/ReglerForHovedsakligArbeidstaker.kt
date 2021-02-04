package no.nav.medlemskap.regler

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.v1.arbeidsforhold.ErBrukerHovedsakligArbeidstakerRegel

class ReglerForHovedsakligArbeidstaker(
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel>,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelMap, overstyrteRegler) {
    override fun hentHovedflyt(): Regelflyt {
        val hovedsakligArbeidstakerFlyt = lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_18),
            hvisJa = Regelflyt.regelflytJa(ytelse, RegelId.REGEL_HOVEDSAKLIG_ARBEIDSTAKER),
            hvisNei = Regelflyt.regelflytUavklart(ytelse, RegelId.REGEL_HOVEDSAKLIG_ARBEIDSTAKER, Konklusjonstype.MEDLEM)
        )

        return hovedsakligArbeidstakerFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag, overstyrteRegler: Map<RegelId, Svar> = emptyMap()): ReglerForHovedsakligArbeidstaker {
            return ReglerForHovedsakligArbeidstaker(
                ytelse = datagrunnlag.ytelse,
                regelMap = lagRegelMap(datagrunnlag),
                overstyrteRegler = overstyrteRegler
            )
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                ErBrukerHovedsakligArbeidstakerRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
