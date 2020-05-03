package no.nav.medlemskap.regler.common

import no.nav.medlemskap.common.stillingsprosentCounter
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.services.aareg.AaRegOpplysningspliktigArbeidsgiverType
import no.nav.medlemskap.services.ereg.Ansatte
import org.threeten.extra.Interval
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors


class Personfakta(private val datagrunnlag: Datagrunnlag) {

    private val datohjelper = Datohjelper(datagrunnlag)
    private val statsborgerskap = datagrunnlag.personhistorikk.statsborgerskap
    private val arbeidsforhold = datagrunnlag.arbeidsforhold

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

    fun hentStatsborgerskapVedStartAvKontrollperiodeForNorskStatsborger(): List<String> =
            hentStatsborgerskapFor(datohjelper.kontrollPeriodeForKunNorskStatsborgerskap().fom!!)

    fun hentStatsborgerskapVedSluttAvKontrollperiodeNorskStatsborger(): List<String> =
            hentStatsborgerskapFor(datohjelper.kontrollPeriodeForKunNorskStatsborgerskap().tom!!)


    fun arbeidsforholdIOpptjeningsperiode(): List<Arbeidsforhold> {

        return arbeidsforhold.filter {
            periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)),
                    datohjelper.kontrollPeriodeForArbeidsforholdIOpptjeningsperiode())
        }
    }

    fun arbeidsforholdForNorskArbeidsgiver(): List<Arbeidsforhold> {
        return arbeidsforhold.filter {
            periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)),
                    datohjelper.kontrollPeriodeForNorskArbeidsgiver())
        }
    }

    fun arbeidsforholdForStillingsprosent(): List<Arbeidsforhold> {
        return arbeidsforhold.filter {
            periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)),
                    datohjelper.kontrollPeriodeForStillingsprosent())
        }
    }

    fun arbeidsforholdForYrkestype(): List<String> {
        return arbeidsforhold.filter {
            periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)),
                    datohjelper.kontrollPeriodeForYrkesforholdType())
        }.map { it.arbeidsfolholdstype.navn }
    }

    fun arbeidsgivereIArbeidsforholdForNorskArbeidsgiver(): List<Arbeidsgiver> {
        return arbeidsforholdForNorskArbeidsgiver().stream().map { it.arbeidsgiver }.collect(Collectors.toList())
    }

    fun erArbeidsgivereOrganisasjon(): Boolean {
        return arbeidsforholdForNorskArbeidsgiver().stream().allMatch { it.arbeidsgivertype == AaRegOpplysningspliktigArbeidsgiverType.Organisasjon }
    }


    fun ansatteHosArbeidsgivere(): List<Ansatte> {
        return arbeidsgivereIArbeidsforholdForNorskArbeidsgiver().mapNotNull { it.ansatte }.flatten()
    }
    fun antallAnsatteHosArbeidsgivere(): List<Int?> {
        return ansatteHosArbeidsgivere().map { it.antall }
    }
    /**
     * På dette tidspunktet er det kjent at bruker er i et aktivt arbeidsforhold.
     * Trenger derfor kun å sjekke at bruker har et arbeidsforhold minumum 12 mnd tilbake og at påfølgende arbeidsforholdene er sammenhengende.
     */
    fun harSammenhengendeArbeidsforholdSiste12Mnd(): Boolean {

        var forrigeTilDato: LocalDate? = null
        val arbeidsforholdForNorskArbeidsgiver = arbeidsforholdForNorskArbeidsgiver()

        if (arbeidsforholdForNorskArbeidsgiver.size > 10) return false

        val harArbeidsforhold12MndTilbake = arbeidsforholdForNorskArbeidsgiver.stream().anyMatch { it.periode.fom?.isBefore(datohjelper.kontrollPeriodeForNorskArbeidsgiver().fom?.plusDays(1))!! }
        val sortertArbeidsforholdEtterPeriode = arbeidsforholdForNorskArbeidsgiver.stream().sorted().collect(Collectors.toList())

        for (arbeidsforhold in sortertArbeidsforholdEtterPeriode) { //Sjekker at alle påfølgende arbeidsforhold er sammenhengende
            if (forrigeTilDato != null && !datohjelper.erDatoerSammenhengende(forrigeTilDato, arbeidsforhold.periode.fom)) {
                return false
            }
            forrigeTilDato = arbeidsforhold.periode.tom
        }

        return harArbeidsforhold12MndTilbake
    }

    fun arbeidsgiversLandForPeriode(): List<String> {
        return arbeidsforholdIOpptjeningsperiode().mapNotNull { it.arbeidsgiver.landkode }
    }

    fun sisteArbeidsforholdYrkeskode(): List<String> {
        return datagrunnlag.arbeidsforhold.filter {
            periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)),
                    datohjelper.kontrollPeriodeForYrkeskode())
        }
                .flatMap { it.arbeidsavtaler }.map { it.yrkeskode }
    }

    fun sisteArbeidsforholdSkipsregister(): List<String> {
        return datagrunnlag.arbeidsforhold.filter {
            periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)),
                    datohjelper.kontrollPeriodeForSkipsregister())
        }.flatMap { it -> it.arbeidsavtaler.map { it.skipsregister?.name.toString() } }
    }

    fun hentBrukerinputArbeidUtenforNorge(): Boolean = datagrunnlag.brukerinput.arbeidUtenforNorge

    fun hentBrukerLandskodeInnenfor12Mnd(): List<Adresse> {
        return datagrunnlag.personhistorikk.bostedsadresser.filter {
            it.landkode == "NOR"
                    && (it.tom?.isAfter(datohjelper.kontrollPeriodeForNorskAdresse().fom) ?: true)
                    && it.fom!!.isBefore(datohjelper.kontrollPeriodeForNorskAdresse().tom)
        }
    }

    fun harBrukerJobberMerEnnGittStillingsprosent(gittStillingsprosent: Double): Boolean {

        val kontrollPeriodeForStillingsprosent = datohjelper.kontrollPeriodeForStillingsprosent()
        val totaltAntallDager = kontrollPeriodeForStillingsprosent.fom!!.until(kontrollPeriodeForStillingsprosent.tom!!, ChronoUnit.DAYS).toDouble()

        for (arbeidsforhold in arbeidsforholdForStillingsprosent()) {
            var antallArbeidsavtaler = 0
            var totalVektetStillingsprosent = 0.0
            for (arbeidsavtale in arbeidsforhold.arbeidsavtaler) {
                val stillingsprosent = arbeidsavtale.stillingsprosent ?: 100.0
                val tilDato = arbeidsavtale.periode.tom ?: arbeidsforhold.periode.tom ?: kontrollPeriodeForStillingsprosent.tom
                var antallDager = kontrollPeriodeForStillingsprosent.fom.until(tilDato, ChronoUnit.DAYS).toDouble()
                if (antallDager > totaltAntallDager) {
                    antallDager = totaltAntallDager
                }
                totalVektetStillingsprosent += (antallDager / totaltAntallDager) * stillingsprosent
                antallArbeidsavtaler++
            }
            stillingsprosentCounter(totalVektetStillingsprosent).increment()
            if (totalVektetStillingsprosent < gittStillingsprosent) return false
        }

        return true
    }

    private fun hentStatsborgerskapFor(dato: LocalDate): List<String> =
            statsborgerskap.filter {
                Periode(it.fom, it.tom).interval().contains(lagInstant(dato))
            }.map { it.landkode }

    private fun periodefilter(periodeDatagrunnlag: Interval, periode: Periode): Boolean {
        return periodeDatagrunnlag.overlaps(lagInterval(periode)) || periodeDatagrunnlag.encloses(lagInterval(periode))
    }

    fun konkursStatuserArbeidsgivere(): List<String?>? {
        return arbeidsforholdForNorskArbeidsgiver().flatMap { it.arbeidsgiver.konkursStatus.orEmpty() }
    }
}
