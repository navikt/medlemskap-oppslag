package no.nav.medlemskap.domene

import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.regler.common.startOfDayInstant
import org.threeten.extra.Interval
import java.time.Instant
import java.time.LocalDate

data class Datagrunnlag(
    val periode: InputPeriode,
    val brukerinput: Brukerinput,
    val pdlpersonhistorikk: Personhistorikk,
    val medlemskap: List<Medlemskap> = listOf(),
    val arbeidsforhold: List<Arbeidsforhold> = listOf(),
    val oppgaver: List<Oppgave> = listOf(),
    val dokument: List<Journalpost> = listOf(),
    val ytelse: Ytelse,
    val dataOmBarn: List<DataOmBarn>?,
    val dataOmEktefelle: DataOmEktefelle?
)

data class Periode(
    val fom: LocalDate?,
    val tom: LocalDate?
) {
    fun erGyldigPeriode(): Boolean {
        return (fomNotNull().isBefore(tomNotNull()))
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

    fun fomNotNull() = this.fom ?: LocalDate.MIN

    fun tomNotNull() = this.tom ?: LocalDate.MAX

    fun interval(): Interval = Interval.of(this.intervalStartInclusive(), this.intervalEndExclusive())

    private fun intervalStartInclusive(): Instant = this.fom?.startOfDayInstant() ?: Instant.MIN

    private fun intervalEndExclusive(): Instant = this.tom?.plusDays(1)?.startOfDayInstant() ?: Instant.MAX
}

data class Kontrollperiode(
    val fom: LocalDate,
    val tom: LocalDate
) {
    val periode = Periode(fom, tom)
}

data class Bruksperiode(
    val fom: LocalDate?,
    val tom: LocalDate?
)

data class Gyldighetsperiode(
    val fom: LocalDate?,
    val tom: LocalDate?
)
