package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Periode
import org.threeten.extra.Interval
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

    private fun førsteSykedag() = datagrunnlag.periode.fom.minusDays(1)


    fun kontrollPeriodeForArbeidsforhold(): Periode{
        return when(ytelse){
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusDays(28),
                    tom = førsteSykedag()
            )
        }

    }

    fun kontrollPeriodeForYrkeskode(): Periode{
        return when(ytelse){
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusDays(12),
                    tom = førsteSykedag()
            )
        }
    }

    fun kontrollPeriodeForYrkesforholdType(): Periode {
        return when(ytelse){
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusMonths(12),
                    tom = førsteSykedag()
            )
        }
    }

    fun kontrollPeriodeForNorskArbeidsgiver(): Periode {
        return when(ytelse) {
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusMonths(12),
                    tom = førsteSykedag()
            )
        }
    }


    fun kontrollPeriodeForSkipsregister(): Periode {
        return when(ytelse){
            Ytelse.SYKEPENGER -> Periode(
                    fom = førsteSykedag().minusMonths(12),
                    tom = førsteSykedag()
            )
        }

    fun erDatoerSammenhengende(sluttDato: LocalDate, startDato: LocalDate?): Boolean {
        return sluttDato.isAfter(startDato?.minusDays(3))
    }
}

fun lagInterval(periode: Periode): Interval {
    val fom = periode.fom ?: LocalDate.MIN
    val tom = periode.tom ?: LocalDate.MAX
    return Interval.of(lagInstant(fom), lagInstant(tom))
}

fun Periode.interval(): Interval {
    val fom = this.fom ?: LocalDate.MIN
    val tom = this.tom ?: LocalDate.MAX
    return Interval.of(lagInstant(fom), lagInstant(tom))
}

fun lagInstant(date: LocalDate) = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
