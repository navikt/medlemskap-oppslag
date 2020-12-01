package no.nav.medlemskap.regler.v1.medlemskap

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.finnesUavklartePerioder
import java.time.LocalDate

class ErPerioderAvklartRegel(
    ytelse: Ytelse,
    val medlemskap: List<Medlemskap>,
    val periode: InputPeriode,
    val førsteDagForYtelse: LocalDate?
) : MedlemskapRegel(RegelId.REGEL_1_1, ytelse, periode, førsteDagForYtelse, medlemskap) {

    override fun operasjon(): Resultat {
        return when {
            medlemskap finnesUavklartePerioder kontrollPeriodeForMedl -> nei(regelId)
            else -> ja(regelId)
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErPerioderAvklartRegel {
            return ErPerioderAvklartRegel(
                ytelse = datagrunnlag.ytelse,
                medlemskap = datagrunnlag.medlemskap,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse
            )
        }
    }
}
