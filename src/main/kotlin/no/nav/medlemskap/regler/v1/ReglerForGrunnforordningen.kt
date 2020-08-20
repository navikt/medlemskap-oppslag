package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*

class ReglerForGrunnforordningen(
        ytelse: Ytelse,
        private val reglerForArbeidsForhold: ReglerForArbeidsforhold,
        regelMap: Map<RegelId, Regel>
) : Regler(ytelse, regelMap) {

    override fun hentRegelflyt(): Regelflyt {
        val erBrukerEØSborgerFlyt = lagRegelflyt(
                regel = hentRegel(RegelId.REGEL_2),
                hvisJa = reglerForArbeidsForhold.hentRegelflyt(),
                hvisNei = regelFlytUavklart(ytelse)
        )

        return erBrukerEØSborgerFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForGrunnforordningen {
            return ReglerForGrunnforordningen(
                    ytelse = datagrunnlag.ytelse,
                    reglerForArbeidsForhold = ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag),
                    regelMap = lagRegelMap(datagrunnlag)
            )
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                    ErBrukerEøsBorgerRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
