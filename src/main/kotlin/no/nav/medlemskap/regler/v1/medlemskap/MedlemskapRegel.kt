package no.nav.medlemskap.regler.v1.medlemskap

import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.erMedlemskapsperioderOver12Mnd
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.harGyldigeMedlemskapsperioder

abstract class MedlemskapRegel(
        regelId: RegelId,
        ytelse: Ytelse,
        periode: InputPeriode,
        private val medlemskap: List<Medlemskap>
) : BasisRegel(regelId, ytelse) {
    val kontrollPeriodeForMedl = Datohjelper(periode, ytelse).kontrollPeriodeForMedl()

    protected fun erMedlemskapPeriodeOver12MndPeriode(finnPeriodeMedMedlemskap: Boolean): Resultat {
        return when {
            medlemskap.erMedlemskapsperioderOver12Mnd(finnPeriodeMedMedlemskap, kontrollPeriodeForMedl)
                    && medlemskap harGyldigeMedlemskapsperioder kontrollPeriodeForMedl -> ja()
            else -> nei()
        }
    }

}