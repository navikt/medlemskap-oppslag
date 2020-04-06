package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.*
import org.threeten.extra.Interval
import java.time.LocalDate


class Personfakta(private val datagrunnlag: Datagrunnlag) {

    private val datohjelper = Datohjelper(datagrunnlag)
    private val statsborgerskap = datagrunnlag.personhistorikk.statsborgerskap

    companion object {
        fun initialiserFakta(datagrunnlag: Datagrunnlag) = Personfakta(datagrunnlag)
    }

    fun personensPerioderIMedl(): List<Medlemskapsunntak> = datagrunnlag.medlemskapsunntak

    fun personensOppgaverIGsak(): List<Oppgave> = datagrunnlag.oppgaver

    fun personensDokumenterIJoark(): List<Journalpost> = datagrunnlag.dokument


    fun hentStatsborgerskapVedStartAvKontrollperiode(): List<String> =
            hentStatsborgerskapFor(datohjelper.kontrollperiodeForStatsborgerskap().fom!!)

    fun hentStatsborgerskapVedSluttAvKontrollperiode(): List<String> =
            hentStatsborgerskapFor(datohjelper.kontrollperiodeForStatsborgerskap().tom!!)


    fun arbeidsforhold(): List<Arbeidsforhold> {
        return datagrunnlag.arbeidsforhold.filter {
            periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)),
                    datohjelper.kontrollPeriodeForArbeidsforhold())
        }
    }

    fun arbeidsgiversLandForPeriode(): List<String> {
        return arbeidsforhold().mapNotNull { it.arbeidsgiver.landkode }
    }

    fun sisteArbeidsforholdtype(): List<String> {
        return arbeidsforhold().map { it.arbeidsfolholdstype.navn }
    }

    fun sisteArbeidsforholdYrkeskode(): List<String> {
        return arbeidsforhold().flatMap { it.arbeidsavtaler }.map { it.yrkeskode }
    }

    fun sisteArbeidsforholdSkipsregister(): List<String> {
        return arbeidsforhold().flatMap { it -> it.arbeidsavtaler.map { it.skipsregister?.name.toString() } }
    }


    fun hentBrukerinputArbeidUtenforNorge(): Boolean = datagrunnlag.brukerinput.arbeidUtenforNorge

    
    private fun hentStatsborgerskapFor(dato: LocalDate): List<String> =
            statsborgerskap.filter {
                Periode(it.fom, it.tom).interval().contains(lagInstant(dato))
            }.map { it.landkode }

    private fun periodefilter(periodeDatagrunnlag: Interval, periode: Periode): Boolean {
        return periodeDatagrunnlag.overlaps(lagInterval(periode)) || periodeDatagrunnlag.encloses(lagInterval(periode))
    }
}
