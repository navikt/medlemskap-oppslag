package no.nav.medlemskap.regler.v1.medlemskap

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Dekning.Companion.uavklartForYtelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.gjeldendeDekning
import java.time.LocalDate

class ErBrukersDekningUavklartRegel(
    ytelse: Ytelse,
    val medlemskap: List<Medlemskap>,
    val periode: InputPeriode,
    førsteDagForYtelse: LocalDate?
) : MedlemskapRegel(RegelId.REGEL_1_6, ytelse, periode, førsteDagForYtelse, medlemskap) {

    override fun operasjon(): Resultat {
        val gjeldendeDekning = medlemskap.gjeldendeDekning(kontrollPeriodeForMedl)
        val dekning = Dekning.from(gjeldendeDekning)

        return when {
            dekning == null || dekning.uavklartForYtelse(ytelse) -> ja()
            else -> nei()
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErBrukersDekningUavklartRegel {
            return ErBrukersDekningUavklartRegel(
                ytelse = datagrunnlag.ytelse,
                medlemskap = datagrunnlag.medlemskap,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse
            )
        }
    }
}
