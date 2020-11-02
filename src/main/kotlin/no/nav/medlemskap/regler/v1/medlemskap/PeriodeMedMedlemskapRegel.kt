package no.nav.medlemskap.regler.v1.medlemskap

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.harPeriodeMedMedlemskap
import java.time.LocalDate

class PeriodeMedMedlemskapRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    førsteDagForYtelse: LocalDate?,
    private val medlemskap: List<Medlemskap>
) : MedlemskapRegel(RegelId.REGEL_1_3, ytelse, periode, førsteDagForYtelse, medlemskap) {

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
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                medlemskap = datagrunnlag.medlemskap
            )
        }
    }
}
