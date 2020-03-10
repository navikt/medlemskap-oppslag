package no.nav.medlemskap.regler.common

import no.nav.medlemskap.common.regelCounter
import no.nav.medlemskap.domene.*
import org.threeten.extra.Interval
import java.time.LocalDate
import java.time.ZoneId


class Personfakta(private val datagrunnlag: Datagrunnlag) {

    companion object {
        fun initialiserFakta(datagrunnlag: Datagrunnlag) = Personfakta(datagrunnlag)
    }

    fun personensPerioderIMedl(): List<Medlemskapsunntak> = datagrunnlag.medlemskapsunntak

    fun personensOppgaverIGsak(): List<Oppgave> = datagrunnlag.oppgaver

    fun personensDokumenterIJoark(): List<Journalpost> = datagrunnlag.dokument

    fun hentStatsborgerskapIPeriode(): List<Statsborgerskap> {
        val periodeDatagrunnlag = lagInterval(Periode(datagrunnlag.periode.fom, datagrunnlag.periode.tom))
        return datagrunnlag.personhistorikk.statsborgerskap.filter {
            filtrerListe(periodeDatagrunnlag, Periode(it.fom, it.tom))
        }
    }

    private fun filtrerListe(periodeDatagrunnlag: Interval, periode: Periode): Boolean {
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
        return hentArbeidsforholdIPeriode().flatMap {  it.arbeidsavtaler }.map { it.yrkeskode }
    }

    fun sisteArbeidsforholdSkipsregister(): List<String> {
        return hentArbeidsforholdIPeriode().flatMap { it -> it.arbeidsavtaler.map { it.skipsregister?.name.toString()} }
    }

    fun hentBrukerinputArbeidUtenforNorge(): Boolean = datagrunnlag.brukerinput.arbeidUtenforNorge

    fun hentTotalStillingprosenter(): List<Double?> {
        val stillingsprosenter = hentArbeidsforholdIPeriode().flatMap { it -> it.arbeidsavtaler.map { it.stillingsprosent  } }
    }

    private fun hentArbeidsforholdIPeriode(): List<Arbeidsforhold> {
        val periodeDatagrunnlag = lagInterval(Periode(datagrunnlag.periode.fom, datagrunnlag.periode.tom))
        return datagrunnlag.arbeidsforhold.filter {filtrerListe(periodeDatagrunnlag, Periode(it.periode.fom, it.periode.tom))
        }
    }

    infix fun oppfyller(avklaring: Avklaring): Resultat {
        val resultat = avklaring.operasjon.invoke(this).apply {
            regelCounter(avklaring.avklaring.replace("?", ""), this.resultat.name).increment()
        }
        return resultat.copy(identifikator = avklaring.identifikator, avklaring = avklaring.avklaring)
    }

    infix fun oppfyller(regelsett: Regelsett): Resultat = regelsett.evaluer(this)

}



