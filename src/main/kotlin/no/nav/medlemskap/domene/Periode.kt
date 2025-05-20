package no.nav.medlemskap.domene

import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsavtale
import no.nav.medlemskap.regler.common.startOfDayInstant
import org.threeten.extra.Interval
import java.time.Instant
import java.time.LocalDate

data class Periode(
    val fom: LocalDate?,
    val tom: LocalDate?
) {

    companion object {

        fun Periode.starterFørKontrollPerioden(periode: Kontrollperiode): Boolean {
            return this.fom != null && this.fom.isBefore(periode.fom)
        }

        fun Periode.slutterEtterKontrollPerioden(periode: Kontrollperiode): Boolean {
            return this.tom == null || this.tom.isAfter(periode.tom)
        }
    }



    fun erGyldigPeriode(): Boolean {
        return (fomNotNull().isBefore(tomNotNull()) || fomNotNull().isEqual(tomNotNull()))
    }

    fun overlapper(annenPeriode: Kontrollperiode): Boolean {
        val periode = Periode(annenPeriode.fom, annenPeriode.tom)
        if (!erGyldigPeriode()) {
            return false
        }

        return interval().overlaps(periode.interval())
    }


    fun overlapper(annenPeriode: Periode): Boolean {
        if (!erGyldigPeriode()) {
            return false
        }

        return interval().overlaps(annenPeriode.interval())
    }

    fun overlapper(dato: LocalDate): Boolean {
        if (!erGyldigPeriode()) {
            return false
        }

        return overlapper(Periode(fom = dato, tom = dato))
    }

    fun enclosesAndFomNotNull(annenPeriode: Periode): Boolean {
        if (this.fom == null) {
            return false
        } else {
            if (!erGyldigPeriode()) {
                return false
            }
            return interval().encloses(annenPeriode.interval())
        }
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

    fun intersection(periode: Kontrollperiode): Kontrollperiode {
        return Kontrollperiode(this.fomNotNull().nyesteDato(periode.fom), this.tomNotNull().eldsteDato(periode.tom))
    }

    private fun LocalDate.eldsteDato(dato: LocalDate): LocalDate {
        return if (this.isBefore(dato)) this
        else dato
    }

    private fun LocalDate.nyesteDato(dato: LocalDate): LocalDate {
        return if (this.isAfter(dato)) this
        else dato
    }

    fun erFomOgTomIkkeNull() = this.fom != null && this.tom != null

    fun fomNotNull() = this.fom ?: LocalDate.MIN

    fun tomNotNull() = this.tom ?: LocalDate.MAX

    fun interval(): Interval = Interval.of(this.intervalStartInclusive(), this.intervalEndExclusive())

    private fun intervalStartInclusive(): Instant = this.fom?.startOfDayInstant() ?: Instant.MIN

    private fun intervalEndExclusive(): Instant = this.tom?.plusDays(1)?.startOfDayInstant() ?: Instant.MAX


}
