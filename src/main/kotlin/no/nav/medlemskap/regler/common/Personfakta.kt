package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.*
import org.threeten.extra.Interval
import java.time.LocalDate
import java.time.ZoneId


class Personfakta(private val datagrunnlag: Datagrunnlag) {

    val SISTE_DAG_I_KONTROLLPERIODE = datagrunnlag.periode.fom.minusDays(1)
    val FØRSTE_DAG_I_KONTROLLPERIODE = SISTE_DAG_I_KONTROLLPERIODE.minusMonths(12)


    val SISTE_DAG_I_KONTROLLPERIODE_1_3 = datagrunnlag.periode.fom.minusDays(1)
    val FOERSTE_DAG_I_KONTROLLPERIODE_1_3 = SISTE_DAG_I_KONTROLLPERIODE_1_3.minusDays(28)



    companion object {
        fun initialiserFakta(datagrunnlag: Datagrunnlag) = Personfakta(datagrunnlag)
    }

    fun personensPerioderIMedl(): List<Medlemskapsunntak> = datagrunnlag.medlemskapsunntak

    fun personensOppgaverIGsak(): List<Oppgave> = datagrunnlag.oppgaver

    fun personensDokumenterIJoark(): List<Journalpost> = datagrunnlag.dokument

    fun hentStatsborgerskapFor(dato: LocalDate): List<String> =
            datagrunnlag.personhistorikk.statsborgerskap.filter {
                lagInterval(Periode(it.fom, it.tom)).contains(lagInstant(dato))
            }.map { it.landkode }

    private fun periodefilter(periodeDatagrunnlag: Interval, periode: Periode): Boolean {
        return periodeDatagrunnlag.overlaps(lagInterval(periode)) || periodeDatagrunnlag.encloses(lagInterval(periode))
    }

    fun arbeidsforhold() : List<Arbeidsforhold> {
       return datagrunnlag.arbeidsforhold.filter {
            periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)),
            Periode(FOERSTE_DAG_I_KONTROLLPERIODE_1_3, SISTE_DAG_I_KONTROLLPERIODE_1_3))
        }
    }

    fun arbeidsgiversLandForPeriode(): List<String> {
        return hentArbeidsforholdIPeriode().mapNotNull { it.arbeidsgiver.landkode }
    }

    private fun lagInterval(periode: Periode): Interval {
        val fom = periode.fom ?: LocalDate.MIN
        val tom = periode.tom ?: LocalDate.MAX
        return Interval.of(lagInstant(fom), lagInstant(tom))
    }

    private fun lagInstant(date: LocalDate) = date.atStartOfDay(ZoneId.systemDefault()).toInstant()

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
