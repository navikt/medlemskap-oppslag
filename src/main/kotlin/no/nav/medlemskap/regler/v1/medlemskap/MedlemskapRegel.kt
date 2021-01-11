package no.nav.medlemskap.regler.v1.medlemskap

import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Kontrollperiode.Companion.kontrollPeriodeForMedl
import no.nav.medlemskap.domene.Kontrollperiode.Companion.startDatoForYtelse
import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.domene.Medlemskap.Companion.erMedlemskapsperioderOver12Mnd
import no.nav.medlemskap.domene.Medlemskap.Companion.harGyldigeMedlemskapsperioder
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.BasisRegel
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import java.time.LocalDate

abstract class MedlemskapRegel(
    regelId: RegelId,
    ytelse: Ytelse,
    periode: InputPeriode,
    førsteDagForYtelse: LocalDate?,
    private val medlemskap: List<Medlemskap>
) : BasisRegel(regelId, ytelse) {
    val kontrollPeriodeForMedl = kontrollPeriodeForMedl(startDatoForYtelse(periode, førsteDagForYtelse))

    protected fun erMedlemskapPeriodeOver12MndPeriode(finnPeriodeMedMedlemskap: Boolean): Resultat {
        return when {
            medlemskap.erMedlemskapsperioderOver12Mnd(finnPeriodeMedMedlemskap, kontrollPeriodeForMedl)
                && medlemskap harGyldigeMedlemskapsperioder kontrollPeriodeForMedl -> ja(regelId)
            else -> nei(regelId)
        }
    }
}
