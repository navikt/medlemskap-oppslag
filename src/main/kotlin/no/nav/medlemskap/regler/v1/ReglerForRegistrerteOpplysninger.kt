package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.REGEL_OPPLYSNINGER
import no.nav.medlemskap.regler.v1.registrerteOpplysninger.*

class ReglerForRegistrerteOpplysninger(
        ytelse: Ytelse,
        regelMap: Map<RegelId, Regel>,
        private val reglerForMedl: ReglerForMedl
) : Regler(ytelse, regelMap) {

    override fun hentRegelflyt(): Regelflyt {
        val erBrukerEØSborgerFlyt = lagRegelflyt(
                regel = hentRegel(RegelId.REGEL_2),
                hvisJa = regelflytJa(ytelse),
                hvisNei = konklusjonUavklart(ytelse)
        )

        val harBrukerRegistrerteOpplysningerFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_OPPLYSNINGER),
                hvisJa = reglerForMedl.hentRegelflyt(),
                hvisNei = erBrukerEØSborgerFlyt,
                hvisUavklart = konklusjonUavklart(ytelse)
        )

        return harBrukerRegistrerteOpplysningerFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForRegistrerteOpplysninger {
            return ReglerForRegistrerteOpplysninger(
                    ytelse = datagrunnlag.ytelse,
                    regelMap = lagRegelMap(datagrunnlag),
                    reglerForMedl = ReglerForMedl.fraDatagrunnlag(datagrunnlag)
            )
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                    FinnesOpplysningerIGosysRegel.fraDatagrunnlag(datagrunnlag),
                    FinnesOpplysningerIJoarkRegel.fraDatagrunnlag(datagrunnlag),
                    FinnesOpplysningerIMedlRegel.fraDatagrunnlag(datagrunnlag),
                    HarBrukerRegistrerteOpplysningerRegel.fraDatagrunnlag(datagrunnlag),
                    ErBrukerEøsBorgerRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
