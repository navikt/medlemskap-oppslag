package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Ytelse
import org.threeten.extra.Interval
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class Datohjelper(val periode: InputPeriode, val ytelse: Ytelse) {

    fun kontrollPeriodeForPersonhistorikk(): Periode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusMonths(12),
                    tom = førsteSykedag()
            )
            Ytelse.DAGPENGER -> defaultDagpengePeriode()
            Ytelse.ENSLIG_FORSORGER -> defaultEnsligForsørgerPeriode()
        }
    }

    fun kontrollPeriodeForMedl(): Periode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusMonths(12),
                    tom = førsteSykedag()
            )
            Ytelse.DAGPENGER -> defaultDagpengePeriode()
            Ytelse.ENSLIG_FORSORGER -> defaultEnsligForsørgerPeriode()
        }
    }

    fun førsteSykedag() = periode.fom.minusDays(1)
    fun førsteDagpengedag() = periode.fom.minusDays(1)
    fun førsteEnsligForsørgerdag() = periode.fom.minusDays(1)

    private fun defaultDagpengePeriode(): Periode = Periode(førsteDagpengedag().minusMonths(12), førsteDagpengedag())
    private fun defaultEnsligForsørgerPeriode(): Periode = Periode(førsteEnsligForsørgerdag().minusMonths(12), førsteEnsligForsørgerdag())

    fun tilOgMedDag(): LocalDate {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> førsteSykedag()
            Ytelse.DAGPENGER -> førsteDagpengedag()
            Ytelse.ENSLIG_FORSORGER -> førsteEnsligForsørgerdag()
        }
    }

    fun kontrollPeriodeForArbeidsforhold(): Periode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusMonths(12),
                    tom = førsteSykedag()
            )
            Ytelse.DAGPENGER -> defaultDagpengePeriode()
            Ytelse.ENSLIG_FORSORGER -> defaultEnsligForsørgerPeriode()
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