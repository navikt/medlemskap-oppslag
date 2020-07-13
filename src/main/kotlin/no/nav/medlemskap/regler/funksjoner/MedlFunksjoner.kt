package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.regler.common.Funksjoner.er
import no.nav.medlemskap.regler.common.interval
import no.nav.medlemskap.regler.common.lagInterval
import java.time.LocalDate

object MedlFunksjoner {


    infix fun List<Medlemskap>.finnesPersonIMedlForKontrollPeriode(kontrollPeriode: Periode): Boolean =
            this.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode).isNotEmpty()

    infix fun List<Medlemskap>.finnesUavklartePerioder(kontrollPeriode: Periode): Boolean =
            this.filter {
                lagInterval(Periode(it.fraOgMed, it.tilOgMed)).overlaps(kontrollPeriode.interval())
                        && ((!it.lovvalg.isNullOrEmpty() && it.lovvalg != "ENDL")
                        || (!it.periodeStatus.isNullOrEmpty() && it.periodeStatus != "GYLD"))
            }.isNotEmpty()

    infix fun List<Medlemskap>.harMedlPeriodeMedOgUtenMedlemskap(kontrollPeriode: Periode): Boolean =
            this.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode).any { it.erMedlem }
                    && this.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode).any { !it.erMedlem }

    infix fun List<Medlemskap>.harPeriodeMedMedlemskap(kontrollPeriode: Periode): Boolean =
            this.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode).any { it.erMedlem && it.lovvalgsland er "NOR" && it.lovvalg er "ENDL" }

    infix fun List<Medlemskap>.harGyldigeMedlemskapsperioder(kontrollPeriode: Periode): Boolean =
            this.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode).none { it.tilOgMed.isAfter(it.fraOgMed.plusYears(5)) }

    fun List<Medlemskap>.erMedlemskapsperioderOver12Mnd(erMedlem: Boolean, kontrollPeriode: Periode): Boolean =
            this.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode).filter { it.erMedlem == erMedlem && it.lovvalg er "ENDL" }
                    .harSammenhengendeMedlemskapIHeleGittPeriode(kontrollPeriode)

    infix fun List<Medlemskap>.gjeldendeDekning(kontrollPeriode: Periode): String? =
            this.medlemskapsPerioderOver12MndPeriode(true, kontrollPeriode).sorted().last().dekning

    private fun List<Medlemskap>.harSammenhengendeMedlemskapIHeleGittPeriode(kontrollPeriode: Periode) =
            this.any { it.fraOgMed.isBefore(kontrollPeriode.fom?.plusDays(1)) }
                    && this.any { it.tilOgMed.isAfter(kontrollPeriode.tom?.minusDays(1)) }
                    && this.sammenhengendePerioder()

    private fun List<Medlemskap>.sammenhengendePerioder() = this.sorted().zipWithNext { a, b -> b.fraOgMed.isBefore(a.tilOgMed.plusDays(2)) }.all { it }

    infix fun List<Medlemskap>.tidligsteFraOgMedDatoForMedl(kontrollPeriode: Periode): LocalDate =
            this.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode).min()!!.fraOgMed


    private fun List<Medlemskap>.medlemskapsPerioderOver12MndPeriode(erMedlem: Boolean, kontrollPeriode: Periode): List<Medlemskap> =
            this.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode).filter {
                it.erMedlem == erMedlem
            }

    private infix fun List<Medlemskap>.brukerensMedlemskapsperioderIMedlForPeriode(kontrollPeriode: Periode): List<Medlemskap> =
            this.filter {
                lagInterval(Periode(it.fraOgMed, it.tilOgMed)).overlaps(kontrollPeriode.interval())
                        && !it.lovvalg.isNullOrEmpty() && it.lovvalg er "ENDL"
                        && !it.periodeStatus.isNullOrEmpty() && it.periodeStatus er "GYLD"
            }
}