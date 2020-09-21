package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.common.*
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Ytelse.Companion.metricName
import no.nav.medlemskap.regler.common.erDatoerSammenhengende
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors
import kotlin.math.abs

object ArbeidsforholdFunksjoner {

    infix fun List<Arbeidsforhold>.erAlleArbeidsgivereOrganisasjon(kontrollPeriode: Kontrollperiode): Boolean {
        return arbeidsforholdForKontrollPeriode(kontrollPeriode).stream().allMatch { it.arbeidsgivertype == OpplysningspliktigArbeidsgiverType.Organisasjon }
    }

    infix fun List<Arbeidsforhold>.antallAnsatteHosArbeidsgivere(kontrollPeriode: Kontrollperiode): List<Int> =
        ansatteHosArbeidsgivere(kontrollPeriode).map { it.antall ?: 0 }

    infix fun List<Arbeidsforhold>.arbeidsforholdForYrkestype(kontrollPeriode: Kontrollperiode): List<String> =
        this.filter {
            it.periode.overlapper(kontrollPeriode.periode)
        }.map { it.arbeidsfolholdstype.navn }

    infix fun List<Arbeidsforhold>.sisteArbeidsforholdYrkeskode(kontrollPeriode: Kontrollperiode): List<String> =
        this.filter {
            it.periode.overlapper(kontrollPeriode.periode)
        }.flatMap { it.arbeidsavtaler }.map { it.yrkeskode }

    infix fun List<Arbeidsforhold>.sisteArbeidsforholdSkipsregister(kontrollPeriode: Kontrollperiode): List<String> =
        this.filter {
            it.periode.overlapper(kontrollPeriode.periode)
        }.flatMap { it -> it.arbeidsavtaler.map { it.skipsregister?.name ?: "" } }

    infix fun List<Arbeidsforhold>.konkursStatuserArbeidsgivere(kontrollPeriode: Kontrollperiode): List<String?>? {
        return arbeidsforholdForKontrollPeriode(kontrollPeriode).flatMap { it.arbeidsgiver.konkursStatus.orEmpty() }
    }

    fun List<Arbeidsforhold>.filtrerUtArbeidsgivereMedFærreEnn6Ansatte(kontrollPeriode: Kontrollperiode) =
        arbeidsforholdForKontrollPeriode(kontrollPeriode).stream().map { it.arbeidsgiver }.collect(Collectors.toList())
            .filter { !it.ansatte?.finnesMindreEnn(6).isNullOrEmpty() }

    fun List<Arbeidsgiver>.registrereArbeidsgivere(ytelse: Ytelse) {
        this.forEach { antallTreffPåArbeidsgiver(it.identifikator, ytelse).increment() }
    }

    fun List<Ansatte>.finnesMindreEnn(tall: Int) = this.filter { it.antall ?: 0 < tall }

    fun List<Arbeidsgiver>.registrerAntallAnsatte(ytelse: Ytelse) =
        this.forEach { arbeidsgiver ->
            arbeidsgiver.ansatte?.forEach { ansatte ->
                antallAnsatteTilUavklart(ansatte.antall.toString(), ytelse).increment()
            }
        }

    /**
     * Sjekk om arbeidsfoholdet er sammenhengende i kontrollperioden
     */
    fun List<Arbeidsforhold>.erSammenhengendeIKontrollPeriode(kontrollPeriode: Kontrollperiode, ytelse: Ytelse): Boolean {

        var forrigeTilDato: LocalDate? = null
        val arbeidsforholdForNorskArbeidsgiver = this.arbeidsforholdForKontrollPeriode(kontrollPeriode)

        if (arbeidsforholdForNorskArbeidsgiver.size > 10) {
            merEnn10ArbeidsforholdCounter(ytelse).increment()
            return false
        }

        if (arbeidsforholdForNorskArbeidsgiver.any { it.periode.fom == null }) {
            harIkkeArbeidsforhold12MndTilbakeCounter(ytelse).increment()
            return false
        }

        if (arbeidsforholdForNorskArbeidsgiver.none { it.periode.fom?.isBefore(kontrollPeriode.fom.plusDays(1))!! }) {
            harIkkeArbeidsforhold12MndTilbakeCounter(ytelse).increment()

            if (arbeidsforholdForNorskArbeidsgiver.isNotEmpty()) {
                val antallDagerDiff = abs(ChronoUnit.DAYS.between(kontrollPeriode.fom, arbeidsforholdForNorskArbeidsgiver.min()!!.periode.fom))
                antallDagerUtenArbeidsforhold(ytelse).record(antallDagerDiff.toDouble())
            }

            return false
        }

        val sortertArbeidsforholdEtterPeriode = arbeidsforholdForNorskArbeidsgiver.sorted()
        for (arbeidsforhold in sortertArbeidsforholdEtterPeriode) { // Sjekker at alle påfølgende arbeidsforhold er sammenhengende
            if (forrigeTilDato != null && !erDatoerSammenhengende(forrigeTilDato, arbeidsforhold.periode.fom)) {
                val antallDagerDiff = abs(ChronoUnit.DAYS.between(forrigeTilDato, arbeidsforhold.periode.fom))
                antallDagerMellomArbeidsforhold(ytelse).record(antallDagerDiff.toDouble())
                usammenhengendeArbeidsforholdCounter(ytelse).increment()
                return false
            }
            forrigeTilDato = arbeidsforhold.periode.tom
        }

        if (forrigeTilDato != null) {
            return !forrigeTilDato.isBefore(kontrollPeriode.tom)
        }

        return true
    }

    infix fun List<Arbeidsforhold>.arbeidsforholdForDato(dato: LocalDate): List<Arbeidsforhold> =
        this.filter {
            it.periode.overlapper(Periode(dato, dato))
        }.sorted()

    fun List<Arbeidsforhold>.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(gittStillingsprosent: Double, kontrollPeriode: Kontrollperiode, ytelse: Ytelse): Boolean {

        val arbeidsforholdForKontrollPeriode = this.arbeidsforholdForKontrollPeriode(kontrollPeriode)

        var samletStillingsprosent = 0.0

        for (arbeidsforhold in arbeidsforholdForKontrollPeriode) {
            val vektetStillingsprosentForArbeidsforhold = arbeidsforhold.vektetStillingsprosentForArbeidsforhold(kontrollPeriode)

            if (vektetStillingsprosentForArbeidsforhold < gittStillingsprosent &&
                this.arbeidsforholdForKontrollPeriode(kontrollPeriode).ingenAndreParallelleArbeidsforhold(arbeidsforhold)
            ) {
                return false
            }

            samletStillingsprosent += vektetStillingsprosentForArbeidsforhold
            stillingsprosentCounter(vektetStillingsprosentForArbeidsforhold, ytelse.metricName()).increment()
        }

        return samletStillingsprosent >= gittStillingsprosent
    }

    private fun Arbeidsforhold.vektetStillingsprosentForArbeidsforhold(kontrollPeriode: Kontrollperiode): Double {
        val totaltAntallDager = kontrollPeriode.fom.until(kontrollPeriode.tom, ChronoUnit.DAYS).toDouble()
        var vektetStillingsprosentForArbeidsforhold = 0.0
        for (arbeidsavtale in this.arbeidsavtaler) {
            val stillingsprosent = arbeidsavtale.stillingsprosent ?: 100.0
            val tilDato = arbeidsavtale.periode.tom ?: this.periode.tom
                ?: kontrollPeriode.tom
            var antallDager = kontrollPeriode.fom.until(tilDato, ChronoUnit.DAYS).toDouble()
            if (antallDager > totaltAntallDager) {
                antallDager = totaltAntallDager
            }
            vektetStillingsprosentForArbeidsforhold += (antallDager / totaltAntallDager) * stillingsprosent
        }
        return vektetStillingsprosentForArbeidsforhold
    }

    private fun List<Arbeidsforhold>.ingenAndreParallelleArbeidsforhold(arbeidsforhold: Arbeidsforhold): Boolean =
        this.none { it.periode.encloses(arbeidsforhold.periode) && it != arbeidsforhold }

    private infix fun List<Arbeidsforhold>.ansatteHosArbeidsgivere(kontrollPeriode: Kontrollperiode): List<Ansatte> =
        arbeidsgivereIArbeidsforholdForNorskArbeidsgiver(kontrollPeriode).mapNotNull { it.ansatte }.flatten()

    private infix fun List<Arbeidsforhold>.arbeidsgivereIArbeidsforholdForNorskArbeidsgiver(kontrollPeriode: Kontrollperiode): List<Arbeidsgiver> {
        return arbeidsforholdForKontrollPeriode(kontrollPeriode).stream().map { it.arbeidsgiver }.collect(Collectors.toList())
    }

    private fun List<Arbeidsforhold>.arbeidsforholdForKontrollPeriode(kontrollPeriode: Kontrollperiode) =
        this.filter {
            it.periode.overlapper(kontrollPeriode.periode)
        }

    fun fraOgMedDatoForArbeidsforhold(periode: InputPeriode) = periode.fom.minusYears(1).minusDays(1)
}
