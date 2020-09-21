package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.Lovvalg
import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.domene.PeriodeStatus
import no.nav.medlemskap.regler.common.Funksjoner.er
import java.time.LocalDate

object MedlFunksjoner {

    infix fun List<Medlemskap>.finnesPersonIMedlForKontrollPeriode(kontrollPeriode: Kontrollperiode): Boolean =
        this.filter {
            it.overlapper(kontrollPeriode.periode)
        }.isNotEmpty()

    infix fun List<Medlemskap>.finnesUavklartePerioder(kontrollPeriode: Kontrollperiode): Boolean =
        this.filter {
            it.overlapper(kontrollPeriode.periode) &&
                (
                    (it.lovvalg != null && it.lovvalg != Lovvalg.ENDL) ||
                        (it.periodeStatus != null && it.periodeStatus != PeriodeStatus.GYLD)
                    )
        }.isNotEmpty()

    infix fun List<Medlemskap>.harMedlPeriodeMedOgUtenMedlemskap(kontrollPeriode: Kontrollperiode): Boolean =
        this.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode).any { it.erMedlem } &&
            this.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode).any { !it.erMedlem }

    infix fun List<Medlemskap>.harPeriodeMedMedlemskap(kontrollPeriode: Kontrollperiode): Boolean =
        this.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode).any { it.erMedlem && it.lovvalgsland er "NOR" }

    infix fun List<Medlemskap>.harGyldigeMedlemskapsperioder(kontrollPeriode: Kontrollperiode): Boolean =
        this.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode).none { it.tilOgMed.isAfter(it.fraOgMed.plusYears(5)) }

    fun List<Medlemskap>.erMedlemskapsperioderOver12Mnd(erMedlem: Boolean, kontrollPeriode: Kontrollperiode): Boolean =
        this.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode).filter { it.erMedlem == erMedlem }
            .harSammenhengendeMedlemskapIHeleGittPeriode(kontrollPeriode)

    infix fun List<Medlemskap>.gjeldendeDekning(kontrollPeriode: Kontrollperiode): String? =
        this.medlemskapsPerioderOver12MndPeriode(true, kontrollPeriode).sorted().last().dekning

    private fun List<Medlemskap>.harSammenhengendeMedlemskapIHeleGittPeriode(kontrollPeriode: Kontrollperiode) =
        this.any { it.fraOgMed.isBefore(kontrollPeriode.fom.plusDays(1)) } &&
            this.any { it.tilOgMed.isAfter(kontrollPeriode.tom.minusDays(1)) } &&
            this.sammenhengendePerioder()

    private fun List<Medlemskap>.sammenhengendePerioder() = this.sorted().zipWithNext { a, b -> b.fraOgMed.isBefore(a.tilOgMed.plusDays(2)) }.all { it }

    infix fun List<Medlemskap>.tidligsteFraOgMedDatoForMedl(kontrollPeriode: Kontrollperiode): LocalDate =
        this.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode).min()!!.fraOgMed

    private fun List<Medlemskap>.medlemskapsPerioderOver12MndPeriode(erMedlem: Boolean, kontrollPeriode: Kontrollperiode): List<Medlemskap> =
        this.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode).filter {
            it.erMedlem == erMedlem
        }

    private infix fun List<Medlemskap>.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode: Kontrollperiode): List<Medlemskap> =
        this.filter {
            it.overlapper(kontrollPeriode.periode) &&
                (it.lovvalg == null || it.lovvalg == Lovvalg.ENDL) &&
                (it.periodeStatus == null || it.periodeStatus == PeriodeStatus.GYLD)
        }
}
