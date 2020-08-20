package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.REGEL_OPPLYSNINGER

class ReglerForRegistrerteOpplysninger(
        ytelse: Ytelse,
        regelMap: Map<RegelId, Regel>,
        private val reglerForMedl: ReglerForMedl,
        private val reglerForGrunnforordningen: ReglerForGrunnforordningen
) : Regler(ytelse, regelMap) {
    override fun hentRegelflyt(): Regelflyt {
        val harBrukerRegistrerteOpplysninger = lagRegelflyt(
                regel = hentRegel(REGEL_OPPLYSNINGER),
                hvisJa = reglerForMedl.hentRegelflyt(),
                hvisNei = reglerForGrunnforordningen.hentRegelflyt(),
                hvisUavklart = regelFlytUavklart(ytelse)
        )

        return harBrukerRegistrerteOpplysninger
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForRegistrerteOpplysninger {
            return ReglerForRegistrerteOpplysninger(
                    ytelse = datagrunnlag.ytelse,
                    regelMap = lagRegelMap(datagrunnlag),
                    reglerForGrunnforordningen = ReglerForGrunnforordningen.fraDatagrunnlag(datagrunnlag),
                    reglerForMedl = ReglerForMedl.fraDatagrunnlag(datagrunnlag)
            )
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                    FinnesOpplysningerIGosysRegel.fraDatagrunnlag(datagrunnlag),
                    FinnesOpplysningerIJoarkRegel.fraDatagrunnlag(datagrunnlag),
                    FinnesOpplysningerIMedlRegel.fraDatagrunnlag(datagrunnlag),
                    HarBrukerRegistrerteOpplysningerRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
