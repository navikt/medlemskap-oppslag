package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Periode
import org.threeten.extra.Interval
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

enum class Ytelse { SYKEPENGER }

class Datohjelper(val datagrunnlag: Datagrunnlag) {

    private val ytelse = Ytelse.SYKEPENGER // TODO: Vurder om dette skal være noe vi sjekker

    fun kontrollperiodeForStatsborgerskap(): Periode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusMonths(12),
                    tom = førsteSykedag()
            )
        }
    }

    fun kontrollPeriodeForMedl(): Periode {
        return when (ytelse){
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusMonths(12),
                    tom = førsteSykedag()
            )
        }
    }

    private fun førsteSykedag() = datagrunnlag.periode.fom.minusDays(1)


    fun kontrollPeriodeForArbeidsforholdIOpptjeningsperiode(): Periode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusDays(28),
                    tom = førsteSykedag()
            )
        }
    }

    fun kontrollPeriodeForSkipsregister(): Periode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusMonths(12),
                    tom = førsteSykedag()
            )
        }
    }

    fun kontrollPeriodeForNorskAdresse(): Periode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusMonths(12),
                    tom = førsteSykedag()
            )
        }
    }

    fun kontrollPeriodeForYrkeskode(): Periode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusDays(12),
                    tom = førsteSykedag()
            )
        }
    }

    fun kontrollPeriodeForYrkesforholdType(): Periode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusMonths(12),
                    tom = førsteSykedag()
            )
        }
    }

    fun kontrollPeriodeForNorskArbeidsgiver(): Periode {
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

    fun erDatoerSammenhengende(sluttDato: LocalDate, startDato: LocalDate?): Boolean {
        return sluttDato.isAfter(startDato?.minusDays(3))
    }
}

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