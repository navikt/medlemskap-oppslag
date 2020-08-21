package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.harPeriodeMedMedlemskap

class PeriodeMedMedlemskapRegel(
        ytelse: Ytelse,
        private val periode: InputPeriode,
        private val medlemskap: List<Medlemskap>
) : MedlemRegel(RegelId.REGEL_1_3, ytelse, periode, medlemskap) {

    override fun operasjon(): Resultat {
        return when {
            medlemskap harPeriodeMedMedlemskap kontrollPeriodeForMedl -> ja()
            else -> nei()
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): PeriodeMedMedlemskapRegel {
            return PeriodeMedMedlemskapRegel(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    medlemskap = datagrunnlag.medlemskap
            )
        }
    }
}