package no.nav.medlemskap.regler.v1.registrerteOpplysninger

import no.nav.medlemskap.common.medlCounter
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.BasisRegel
import no.nav.medlemskap.regler.common.Datohjelper
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.finnesPersonIMedlForKontrollPeriode
import java.time.LocalDate

class FinnesOpplysningerIMedlRegel(
    ytelse: Ytelse,
    val periode: InputPeriode,
    val førsteDagForYtelse: LocalDate?,
    val medlemskap: List<Medlemskap> = emptyList()
) : BasisRegel(RegelId.REGEL_A, ytelse) {

    override fun operasjon(): Resultat {
        val kontrollPeriodeForMedl = Datohjelper(periode, førsteDagForYtelse, ytelse).kontrollPeriodeForMedl()
        if (medlemskap.isNotEmpty()) medlCounter().increment()
        return when {
            medlemskap finnesPersonIMedlForKontrollPeriode kontrollPeriodeForMedl -> ja(RegelId.REGEL_A)
            else -> nei(RegelId.REGEL_A)
        }
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): FinnesOpplysningerIMedlRegel {
            return FinnesOpplysningerIMedlRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                medlemskap = datagrunnlag.medlemskap
            )
        }
    }
}
