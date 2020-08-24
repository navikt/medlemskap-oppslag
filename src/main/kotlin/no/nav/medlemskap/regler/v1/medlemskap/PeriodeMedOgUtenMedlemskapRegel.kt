package no.nav.medlemskap.regler.v1.medlemskap

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.harMedlPeriodeMedOgUtenMedlemskap

class PeriodeMedOgUtenMedlemskapRegel(
        ytelse: Ytelse,
        private val periode: InputPeriode,
        private val medlemskap: List<Medlemskap>
) : MedlemskapRegel(RegelId.REGEL_1_2, ytelse, periode, medlemskap) {

    override fun operasjon(): Resultat {
        return when {
            medlemskap harMedlPeriodeMedOgUtenMedlemskap kontrollPeriodeForMedl -> ja()
            else -> nei()
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): PeriodeMedOgUtenMedlemskapRegel {
            return PeriodeMedOgUtenMedlemskapRegel(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    medlemskap = datagrunnlag.medlemskap
            )
        }
    }
}