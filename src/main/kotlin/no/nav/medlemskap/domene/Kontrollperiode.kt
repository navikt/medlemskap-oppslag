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
        fun kontrollPeriodeForPersonhistorikk(inputPeriode: InputPeriode, førsteDagForYtelse: LocalDate?): Kontrollperiode {
            return Kontrollperiode(
                fom = førsteDatoForYtelse(inputPeriode, førsteDagForYtelse).minusMonths(12),
                tom = førsteDatoForYtelse(inputPeriode, førsteDagForYtelse)
            )
        }

        fun kontrollPeriodeForMedl(inputPeriode: InputPeriode, førsteDagForYtelse: LocalDate?): Kontrollperiode {
            return Kontrollperiode(
                fom = førsteDatoForYtelse(inputPeriode, førsteDagForYtelse).minusMonths(12),
                tom = førsteDatoForYtelse(inputPeriode, førsteDagForYtelse)
            )
        }

        fun kontrollPeriodeForArbeidsforhold(inputPeriode: InputPeriode, førsteDagForYtelse: LocalDate?): Kontrollperiode {
            return Kontrollperiode(
                fom = førsteDatoForYtelse(inputPeriode, førsteDagForYtelse).minusMonths(12),
                tom = førsteDatoForYtelse(inputPeriode, førsteDagForYtelse)
            )
        }

        fun førsteDatoForYtelse(inputPeriode: InputPeriode, førsteDagForYtelse: LocalDate?): LocalDate {
            if (førsteDagForYtelse != null) {
                return førsteDagForYtelse
            } else {
                return inputPeriode.fom.minusDays(1)
            }
        }
    }
}
