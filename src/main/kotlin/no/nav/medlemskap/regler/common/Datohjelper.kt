package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Periode
import org.threeten.extra.Interval
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class Ytelse { SYKEPENGER }

class Datohjelper(val periode: InputPeriode) {

    private val ytelse = Ytelse.SYKEPENGER // TODO: Vurder om dette skal være noe vi sjekker

    fun kontrollPeriodeForPersonhistorikk(): Periode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusMonths(12),
                    tom = førsteSykedag()
            )
        }
    }

    fun kontrollPeriodeForMedl(): Periode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusMonths(12),
                    tom = periode.tom
            )
        }
    }

    fun førsteSykedag() = periode.fom.minusDays(1)

    fun tilOgMedDag(): LocalDate {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> førsteSykedag()
        }
    }

    fun kontrollPeriodeForArbeidsforholdIOpptjeningsperiode(): Periode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusDays(28),
                    tom = førsteSykedag()
            )
        }
    }

    fun kontrollPeriodeForArbeidsforhold(): Periode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusMonths(12),
                    tom = førsteSykedag()
            )
        }
    }

    fun kontrollPeriodeForStillingsprosent(): Periode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusMonths(12),
                    tom = førsteSykedag()
            )
        }
    }

    companion object {
        private val datoFormatter = DateTimeFormatter.ofPattern("d.MM.yyyy")

        fun parseDato(dato: String): LocalDate {
            return LocalDate.parse(dato, datoFormatter)
        }
    }
}

fun erDatoerSammenhengende(sluttDato: LocalDate, startDato: LocalDate?): Boolean = sluttDato.isAfter(startDato?.minusDays(3))

fun lagInterval(periode: Periode): Interval = periode.interval()

fun Periode.interval(): Interval {
    return Interval.of(this.intervalStartInclusive(), this.intervalEndExclusive())
}

fun lagInstantStartOfDay(date: LocalDate) = date.atStartOfDay(ZoneId.systemDefault()).toInstant()

fun LocalDate.startOfDayInstant() = this.atStartOfDay(ZoneId.systemDefault()).toInstant()

fun Periode.fomNotNull() = this.fom ?: LocalDate.MIN
fun Periode.tomNotNull() = this.tom ?: LocalDate.MAX

fun Periode.intervalStartInclusive(): Instant = this.fom?.startOfDayInstant() ?: Instant.MIN
fun Periode.intervalEndExclusive(): Instant = this.tom?.plusDays(1)?.startOfDayInstant() ?: Instant.MAX