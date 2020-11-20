package no.nav.medlemskap.regler.v1.medlemskap

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.harMedlPeriodeMedOgUtenMedlemskap
import java.time.LocalDate

class PeriodeMedOgUtenMedlemskapRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    førsteDagForYtelse: LocalDate?,
    private val medlemskap: List<Medlemskap>
) : MedlemskapRegel(RegelId.REGEL_1_2, ytelse, periode, førsteDagForYtelse, medlemskap) {

    override fun operasjon(): Resultat {
        return when {
            medlemskap harMedlPeriodeMedOgUtenMedlemskap kontrollPeriodeForMedl -> ja(RegelId.REGEL_1_2.begrunnelse)
            else -> nei()
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): PeriodeMedOgUtenMedlemskapRegel {
            return PeriodeMedOgUtenMedlemskapRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                medlemskap = datagrunnlag.medlemskap
            )
        }
    }
}
