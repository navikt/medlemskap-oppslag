package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.*
import org.threeten.extra.Interval
import java.time.LocalDate
import java.util.stream.Collectors


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

    fun arbeidsforholdForNorskArbeidsgiver(): List<Arbeidsforhold> {
        return datagrunnlag.arbeidsforhold.filter {
            periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)),
                    datohjelper.kontrollPeriodeForNorskarbeidsgiver())
        }
    }

    fun arbeidsgivereIArbeidsforholdForNorskArbeidsgiver() : List<Arbeidsgiver> {
        return arbeidsforholdForNorskArbeidsgiver().stream().map { it.arbeidsgiver }.collect(Collectors.toList())
    }

    fun antallAnsatteHosArbeidsgivere() : List<Int?> {
        return arbeidsgivereIArbeidsforholdForNorskArbeidsgiver().stream().map { it.antallAnsatte }.collect(Collectors.toList())
    }

    fun harArbeidsforholdSiste12Mnd(): Boolean {

        val arbeidsavtalerSiste12Mnd = arbeidsforholdForNorskArbeidsgiver().stream().map { it.arbeidsavtaler }
        var forrigeTilDato: LocalDate? = datohjelper.kontrollPeriodeForNorskarbeidsgiver().fom

        for (list in arbeidsavtalerSiste12Mnd) {
            for (arbeidsavtale in list) {
                arbeidsavtale.periode.fom?.isBefore(forrigeTilDato?.plusDays(2))
                if (arbeidsavtale.periode.tom?.isAfter(forrigeTilDato)!!) forrigeTilDato = arbeidsavtale.periode.tom
            }
        }
        return false
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
