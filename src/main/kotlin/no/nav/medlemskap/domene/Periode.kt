package no.nav.medlemskap.domene

import no.nav.medlemskap.regler.common.startOfDayInstant
import org.threeten.extra.Interval
import java.time.Instant
import java.time.LocalDate

data class Periode(
    val fom: LocalDate?,
    val tom: LocalDate?
) {
    fun erGyldigPeriode(): Boolean {
        return (fomNotNull().isBefore(tomNotNull()) || fomNotNull().isEqual(tomNotNull()))
    }

    fun overlapper(annenPeriode: Periode): Boolean {
        if (!erGyldigPeriode()) {
            return false
        }

        return interval().overlaps(annenPeriode.interval()) || interval().encloses(annenPeriode.interval())
    }

    fun overlapper(dato: LocalDate): Boolean {
        if (!erGyldigPeriode()) {
            return false
        }

        return overlapper(Periode(fom = dato, tom = dato))
    }

    fun encloses(annenPeriode: Periode): Boolean {
        if (!erGyldigPeriode()) {
            return false
        }

        return interval().encloses(annenPeriode.interval())
    }

    fun encloses(dato: LocalDate): Boolean {
        return encloses(Periode(fom = dato, tom = dato))
    }

    fun fomNotNull() = this.fom ?: LocalDate.MIN

    fun tomNotNull() = this.tom ?: LocalDate.MAX

    fun interval(): Interval = Interval.of(this.intervalStartInclusive(), this.intervalEndExclusive())

    private fun intervalStartInclusive(): Instant = this.fom?.startOfDayInstant() ?: Instant.MIN

    private fun intervalEndExclusive(): Instant = this.tom?.plusDays(1)?.startOfDayInstant() ?: Instant.MAX
}
