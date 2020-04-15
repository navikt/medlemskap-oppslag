package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.services.aareg.AaRegOrganisasjonType
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
                    datohjelper.kontrollPeriodeForNorskArbeidsgiver())
        }
    }


    //Sjekker her at det ikke finnes "brudd" i perioden mens sjekken for
    // selve typen gjøres i regeltjenesten
    fun arbeidsforholdForYrkestype(): List<Arbeidsforhold> {
        return datagrunnlag.arbeidsforhold.filter {
            periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)),
                    datohjelper.kontrollPeriodeForYrkesforholdType())
        }
    }

    fun arbeidsgivereIArbeidsforholdForNorskArbeidsgiver(): List<Arbeidsgiver> {
        return arbeidsforholdForNorskArbeidsgiver().stream().map { it.arbeidsgiver }.collect(Collectors.toList())
    }

    fun erArbeidsgivereOrganisasjon(): Boolean {
        return arbeidsgivereIArbeidsforholdForNorskArbeidsgiver().stream().allMatch { it.type == AaRegOrganisasjonType.Organisasjon.name }
    }

    fun antallAnsatteHosArbeidsgivere(): List<Int?> {
        return arbeidsgivereIArbeidsforholdForNorskArbeidsgiver().stream().map { it.antallAnsatte }.collect(Collectors.toList())
    }

    /**
     * På dette tidspunktet er det kjent at bruker er i et aktivt arbeidsforhold.
     * Trenger derfor kun å sjekke at bruker har et arbeidsforhold minumum 12 mnd tilbake og at påfølgende arbeidsforholdene er sammenhengende.
     */
    fun harSammenhengendeArbeidsforholdSiste12Mnd(): Boolean {

        var forrigeTilDato: LocalDate? = null

        val arbeidsforholdForNorskArbeidsgiver = arbeidsforholdForNorskArbeidsgiver()

        val harArbeidsforhold12MndTilbake = arbeidsforholdForNorskArbeidsgiver.stream().anyMatch { it.periode.fom?.isBefore(datohjelper.kontrollPeriodeForNorskArbeidsgiver().fom?.plusDays(1))!! }

        val sortertArbeidsforholdEtterPeriode = arbeidsforholdForNorskArbeidsgiver.stream()
                .sorted { o1, o2 -> o1.periode.tom?.compareTo(o2.periode.tom)!! }.collect(Collectors.toList())

        for (arbeidsforhold in sortertArbeidsforholdEtterPeriode) { //Sjekker at alle påfølgende arbeidsforhold er sammenhengende
            if (forrigeTilDato != null && !forrigeTilDato.isAfter(arbeidsforhold.periode.fom?.minusDays(3))) {
                return false
            }
            forrigeTilDato = arbeidsforhold.periode.tom
        }

        return harArbeidsforhold12MndTilbake
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

    fun hentKonkursStatuser(): List<String>? {
      return arbeidsforholdForNorskArbeidsgiver().flatMap { it.arbeidsgiver.konkursStatus.orEmpty() }
    }
}

