package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.*
import org.threeten.extra.Interval
import java.time.LocalDate
import java.time.ZoneId


class Personfakta(private val datagrunnlag: Datagrunnlag) {

    private val FØRSTE_DAG_MINUS_1 = datagrunnlag.periode.fom.minusDays(1)

    companion object {
        fun initialiserFakta(datagrunnlag: Datagrunnlag) = Personfakta(datagrunnlag)
    }

    fun personensPerioderIMedl(): List<Medlemskapsunntak> = datagrunnlag.medlemskapsunntak

    fun personensOppgaverIGsak(): List<Oppgave> = datagrunnlag.oppgaver

    fun personensDokumenterIJoark(): List<Journalpost> = datagrunnlag.dokument

    fun hentAktuelleStatsborgerskap(): List<Statsborgerskap> {
        val periodeSomSkalSjekkes =
                lagInterval(Periode(
                        FØRSTE_DAG_MINUS_1.minusMonths(12),
                        FØRSTE_DAG_MINUS_1
                ))
        return datagrunnlag.personhistorikk.statsborgerskap.filter {
            periodefilter(periodeSomSkalSjekkes, Periode(it.fom, it.tom))
        }
    }

    private fun periodefilter(periodeDatagrunnlag: Interval, periode: Periode): Boolean {
        return periodeDatagrunnlag.overlaps(lagInterval(periode)) || periodeDatagrunnlag.encloses(lagInterval(periode))
    }

    fun arbeidsforhold(): List<Arbeidsforhold> = datagrunnlag.arbeidsforhold

    fun arbeidsgiversLandForPeriode(): List<String> {
        return hentArbeidsforholdIPeriode().mapNotNull { it.arbeidsgiver.landkode }
    }

    private fun lagInterval(periode: Periode): Interval {
        val fom = periode.fom ?: LocalDate.MIN
        val tom = periode.tom ?: LocalDate.MAX
        return Interval.of(fom.atStartOfDay(ZoneId.systemDefault()).toInstant(), tom.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    fun sisteArbeidsforholdtype(): List<String> {
        return hentArbeidsforholdIPeriode().map { it.arbeidsfolholdstype.navn }
    }

    fun sisteArbeidsforholdYrkeskode(): List<String> {
        return hentArbeidsforholdIPeriode().flatMap { it.arbeidsavtaler }.map { it.yrkeskode }
    }

    fun sisteArbeidsforholdSkipsregister(): List<String> {
        return hentArbeidsforholdIPeriode().flatMap { it -> it.arbeidsavtaler.map { it.skipsregister?.name.toString() } }
    }

    fun hentBrukerinputArbeidUtenforNorge(): Boolean = datagrunnlag.brukerinput.arbeidUtenforNorge

    private fun hentArbeidsforholdIPeriode(): List<Arbeidsforhold> {
        val periodeDatagrunnlag = lagInterval(Periode(datagrunnlag.periode.fom, datagrunnlag.periode.tom))
        return datagrunnlag.arbeidsforhold.filter {
            periodefilter(periodeDatagrunnlag, Periode(it.periode.fom, it.periode.tom))
        }
    }
}
