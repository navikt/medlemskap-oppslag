package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.common.medlCounter
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.finnesPersonIMedlForKontrollPeriode

class FinnesOpplysningerIMedlRegel(
        ytelse: Ytelse,
        val periode: InputPeriode,
        val medlemskap: List<Medlemskap> = emptyList()
) : BasisRegel(RegelId.REGEL_A, ytelse) {

    override fun operasjon(): Resultat {
        val kontrollPeriodeForMedl = Datohjelper(periode, ytelse).kontrollPeriodeForMedl()
        if (medlemskap.isNotEmpty()) medlCounter().increment()
        return when {
            medlemskap finnesPersonIMedlForKontrollPeriode kontrollPeriodeForMedl -> ja()
            else -> nei()
        }
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): FinnesOpplysningerIMedlRegel {
            return FinnesOpplysningerIMedlRegel(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    medlemskap = datagrunnlag.medlemskap
            )
        }
    }
}