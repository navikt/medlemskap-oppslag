package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.common.*
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Ytelse.Companion.metricName
import no.nav.medlemskap.regler.common.Funksjoner.isNotNullOrEmpty
import no.nav.medlemskap.regler.common.erDatoerSammenhengende
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

object ArbeidsforholdFunksjoner {
    private val offentligSektorJuridiskeEnhetstyper = listOf("STAT", "FKF", "FYLK", "KF", "KOMM", "SF")

    fun erArbeidsforholdetOffentligSektor(arbeidsforhold: List<Arbeidsforhold>, kontrollPeriode: Kontrollperiode, ytelse: Ytelse): Boolean =
        vektetOffentligSektorArbeidsforhold(arbeidsforhold, kontrollPeriode, ytelse)

    private fun vektetOffentligSektorArbeidsforhold(arbeidsforhold: List<Arbeidsforhold>, kontrollPeriode: Kontrollperiode, ytelse: Ytelse): Boolean =
        arbeidsforhold.filter {
            it.arbeidsgiver.juridiskeEnheter != null &&
                it.arbeidsgiver.juridiskeEnheter.all { enhetstype -> enhetstype != null && enhetstype.enhetstype in offentligSektorJuridiskeEnhetstyper }
        }.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(25.0, kontrollPeriode, ytelse)

    private fun hentJuridiskEnhetstypeFraMap(juridiskEnhetEnhetstypeMap: Map<String, String?>?): List<String> {
        if (juridiskEnhetEnhetstypeMap != null) {
            return juridiskEnhetEnhetstypeMap.mapNotNull { (_, enhetstype) -> enhetstype }
        }
        return emptyList()
    }

    infix fun List<Arbeidsforhold>.erAlleArbeidsgivereOrganisasjon(kontrollPeriode: Kontrollperiode): Boolean {
        return arbeidsforholdForKontrollPeriode(kontrollPeriode).stream().allMatch { it.arbeidsgivertype == OpplysningspliktigArbeidsgiverType.Organisasjon }
    }

    infix fun List<Arbeidsforhold>.antallAnsatteHosArbeidsgivere(kontrollPeriode: Kontrollperiode): List<Int> =
        ansatteHosArbeidsgivere(kontrollPeriode).map { it.antall ?: 0 }

    infix fun List<Arbeidsforhold>.antallAnsatteHosArbeidsgiversJuridiskeEnheter(kontrollPeriode: Kontrollperiode): List<Int?> =
        arbeidsforholdForKontrollPeriodeMedStillingsprosentOver0(kontrollPeriode).filter {
            it.arbeidsgiver.juridiskeEnheter.isNotNullOrEmpty()
        }.flatMap { p -> p.arbeidsgiver.juridiskeEnheter!!.map { r -> r?.antallAnsatte ?: 0 } }

    infix fun List<Arbeidsforhold>.arbeidsforholdForYrkestype(kontrollPeriode: Kontrollperiode): List<String> =
        this.filter {
            it.periode.overlapper(kontrollPeriode.periode)
        }.map { it.arbeidsforholdstype.navn }

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
        arbeidsforholdForKontrollPeriode(kontrollPeriode).map { it.arbeidsgiver }
            .filter { !it.ansatte?.finnesMindreEnn(6).isNullOrEmpty() }

    fun List<Arbeidsgiver>.registrereArbeidsgivere(ytelse: Ytelse) {
        this.forEach { antallTreffPåArbeidsgiver(it.organisasjonsnummer, ytelse).increment() }
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
        val arbeidsforholdForKontrollPeriode = this.arbeidsforholdForKontrollPeriodeMedStillingsprosentOver0(kontrollPeriode)
        var samletStillingsprosent = 0.0

        val list = mutableListOf<Arbeidsforhold>()
        for (arbeidsforhold in arbeidsforholdForKontrollPeriode) {
            val vektetStillingsprosentForArbeidsforhold = arbeidsforhold.vektetStillingsprosentForArbeidsforhold(kontrollPeriode, false)

            list.addAll(this.arbeidsforholdForKontrollPeriode(kontrollPeriode).parallelleArbeidsforhold(arbeidsforhold, kontrollPeriode))
            if (vektetStillingsprosentForArbeidsforhold < gittStillingsprosent &&
                this.arbeidsforholdForKontrollPeriode(kontrollPeriode).ingenAndreParallelleArbeidsforhold(arbeidsforhold, kontrollPeriode)
            ) {
                if (!list.contains(arbeidsforhold)) return false
            }

            samletStillingsprosent += vektetStillingsprosentForArbeidsforhold
            stillingsprosentCounter(vektetStillingsprosentForArbeidsforhold, ytelse.metricName()).increment()
        }
        samletStillingsprosentCounter(samletStillingsprosent, ytelse.metricName()).increment()
        return samletStillingsprosent >= gittStillingsprosent
    }

    fun Arbeidsforhold.vektetStillingsprosentForArbeidsforhold(kontrollPeriode: Kontrollperiode, beregnGjennomsnittForKontrollperioden: Boolean = false): Double {

        val arbeidsforholdKontrollperiodeIntersection = this.periode.intersection(kontrollPeriode)
        val totaltAntallDager = if (beregnGjennomsnittForKontrollperioden) kontrollPeriode.antallDager
        else arbeidsforholdKontrollperiodeIntersection.antallDager

        var vektetStillingsprosentForArbeidsforhold = 0.0

        this.arbeidsavtaler
            .filter { it.gyldighetsperiode.overlapper(arbeidsforholdKontrollperiodeIntersection.periode) && (it.stillingsprosent == null || it.stillingsprosent > 0.0) }
            .map { (it.gyldighetsperiode.intersection(arbeidsforholdKontrollperiodeIntersection).antallDager to (it.getStillingsprosent())) }
            .forEach { vektetStillingsprosentForArbeidsforhold += ((it.first / totaltAntallDager) * it.second) }

        return Math.round(vektetStillingsprosentForArbeidsforhold * 10.0) / 10.0
    }

    fun List<Arbeidsforhold>.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTidSkygge(gittStillingsprosent: Double, kontrollPeriode: Kontrollperiode): Boolean {
        val arbeidsforholdForKontrollPeriode = this.arbeidsforholdForKontrollPeriodeMedStillingsprosentOver0(kontrollPeriode)
        var samletStillingsprosent = 0.0

        for (arbeidsforhold in arbeidsforholdForKontrollPeriode) {
            val vektetStillingsprosentForArbeidsforhold = arbeidsforhold.vektetStillingsprosentForArbeidsforholdSkygge(kontrollPeriode)

            if (vektetStillingsprosentForArbeidsforhold < gittStillingsprosent &&
                this.arbeidsforholdForKontrollPeriode(kontrollPeriode).ingenAndreParallelleArbeidsforhold(arbeidsforhold, kontrollPeriode)
            ) {
                return false
            }

            samletStillingsprosent += vektetStillingsprosentForArbeidsforhold
        }

        return samletStillingsprosent >= gittStillingsprosent
    }

    private fun Arbeidsforhold.vektetStillingsprosentForArbeidsforholdSkygge(kontrollPeriode: Kontrollperiode, beregnGjennomsnittForKontrollperioden: Boolean = false): Double {

        val arbeidsforholdKontrollperiodeIntersection = this.periode.intersection(kontrollPeriode)
        val totaltAntallDager = if (beregnGjennomsnittForKontrollperioden) kontrollPeriode.antallDager
        else arbeidsforholdKontrollperiodeIntersection.antallDager

        var vektetStillingsprosentForArbeidsforhold = 0.0

        this.arbeidsavtaler
            .filter { it.gyldighetsperiode.overlapper(kontrollPeriode.periode) && it.stillingsprosent != null && it.stillingsprosent > 0.0 }
            .map { (it.gyldighetsperiode.intersection(arbeidsforholdKontrollperiodeIntersection).antallDager to (it.getStillingsprosent())) }
            .forEach { vektetStillingsprosentForArbeidsforhold += ((it.first / totaltAntallDager) * it.second) }

        return Math.round(vektetStillingsprosentForArbeidsforhold * 10.0) / 10.0
    }

    fun List<Arbeidsforhold>.beregnGjennomsnittligStillingsprosentForGrafana(kontrollPeriode: Kontrollperiode): Double {
        var sumStillingsprosent = 0.0

        for (arbeidsforhold in this) {
            sumStillingsprosent += arbeidsforhold.vektetStillingsprosentForArbeidsforhold(kontrollPeriode, true)
        }

        return sumStillingsprosent
    }

    private fun List<Arbeidsforhold>.ingenAndreParallelleArbeidsforhold(arbeidsforhold: Arbeidsforhold, kontrollPeriode: Kontrollperiode): Boolean =
        this.none {
            it.periode.intersection(kontrollPeriode).periode.encloses(arbeidsforhold.periode.intersection(kontrollPeriode).periode) &&
                it != arbeidsforhold &&
                it.arbeidsavtaler.any { p -> p.stillingsprosent == null || p.stillingsprosent > 0.0 }
        }

    private fun List<Arbeidsforhold>.parallelleArbeidsforhold(arbeidsforhold: Arbeidsforhold, kontrollPeriode: Kontrollperiode): List<Arbeidsforhold> =
        this.filter {
            it.periode.intersection(kontrollPeriode).periode.encloses(arbeidsforhold.periode.intersection(kontrollPeriode).periode) &&
                it != arbeidsforhold &&
                it.arbeidsavtaler.any { p -> p.stillingsprosent == null || p.stillingsprosent > 0.0 }
        }

    private infix fun List<Arbeidsforhold>.ansatteHosArbeidsgivere(kontrollPeriode: Kontrollperiode): List<Ansatte> =
        arbeidsgivereIArbeidsforholdForNorskArbeidsgiver(kontrollPeriode).mapNotNull { it.ansatte }.flatten()

    private infix fun List<Arbeidsforhold>.arbeidsgivereIArbeidsforholdForNorskArbeidsgiver(kontrollPeriode: Kontrollperiode): List<Arbeidsgiver> {
        return arbeidsforholdForKontrollPeriode(kontrollPeriode).map { it.arbeidsgiver }
    }

    private fun List<Arbeidsforhold>.arbeidsforholdForKontrollPeriode(kontrollPeriode: Kontrollperiode) =
        this.filter {
            it.periode.overlapper(kontrollPeriode.periode)
        }

    fun List<Arbeidsforhold>.arbeidsforholdForKontrollPeriodeMedStillingsprosentOver0(kontrollPeriode: Kontrollperiode) =
        this.filter {
            it.periode.overlapper(kontrollPeriode.periode) &&
                it.arbeidsavtaler.any { p -> p.stillingsprosent == null || p.stillingsprosent > 0.0 }
        }

    fun fraOgMedDatoForArbeidsforhold(periode: InputPeriode) = periode.fom.minusYears(1).minusDays(1)
}
