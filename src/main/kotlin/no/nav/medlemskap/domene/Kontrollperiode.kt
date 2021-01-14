package no.nav.medlemskap.domene

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Kontrollperiode(
    val fom: LocalDate,
    val tom: LocalDate
) {
    val periode = Periode(fom, tom)
    val antallDager = fom.until(tom, ChronoUnit.DAYS).toDouble()

    companion object {
        fun kontrollPeriodeForPersonhistorikk(startDatoForYtelse: LocalDate): Kontrollperiode {
            return Kontrollperiode(
                fom = startDatoForYtelse.minusMonths(12),
                tom = startDatoForYtelse
            )
        }

        fun kontrollPeriodeForMedl(startDatoForYtelse: LocalDate): Kontrollperiode {
            return Kontrollperiode(
                fom = startDatoForYtelse.minusMonths(12),
                tom = startDatoForYtelse
            )
        }

        fun kontrollPeriodeForArbeidsforhold(startDatoForYtelse: LocalDate): Kontrollperiode {
            return Kontrollperiode(
                fom = startDatoForYtelse.minusMonths(12),
                tom = startDatoForYtelse
            )
        }

        fun startDatoForYtelse(inputPeriode: InputPeriode, førsteDagForYtelse: LocalDate?): LocalDate {
            if (førsteDagForYtelse != null) {
                return førsteDagForYtelse
            } else {
                return inputPeriode.fom.minusDays(1)
            }
        }

        fun kontrollPeriodeForOppholdstillatelse(inputPeriode: InputPeriode, førsteDagForYtelse: LocalDate?): Kontrollperiode {
            return Kontrollperiode(
                fom = startDatoForYtelse(inputPeriode, førsteDagForYtelse).minusMonths(12),
                tom = startDatoForYtelse(inputPeriode, førsteDagForYtelse).plusMonths(2)
            )
        }
    }
}