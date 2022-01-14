package no.nav.medlemskap.domene.arbeidsforhold

import no.nav.medlemskap.clients.ereg.Ansatte.Companion.finnesMindreEnn
import no.nav.medlemskap.common.*
import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.Ytelse.Companion.name
import no.nav.medlemskap.regler.common.Funksjoner.isNotNullOrEmpty
import no.nav.medlemskap.regler.common.erDatoerSammenhengende
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

data class Arbeidsforhold(
    val periode: Periode,
    val utenlandsopphold: List<Utenlandsopphold>?,
    val arbeidsgivertype: OpplysningspliktigArbeidsgiverType,
    val arbeidsgiver: Arbeidsgiver,
    val arbeidsforholdstype: Arbeidsforholdstype,
    var arbeidsavtaler: List<Arbeidsavtale>,
    val permisjonPermittering: List<PermisjonPermittering>?
) : Comparable<Arbeidsforhold> {

    /**
     * Comparator som sorterer arbeidsforhold etter periode.
     * Null-verdier regnes som høyere, slik at aktive arbeidsforhold vil havne sist i listen.
     */
    override fun compareTo(other: Arbeidsforhold): Int {

        if (this.periode.fom != null && other.periode.fom != null && this.periode.fom != other.periode.fom) {
            return this.periode.fom.compareTo(other.periode.fom)
        }

        if (this.periode.tom == null && other.periode.tom == null) {
            return this.periode.fom?.compareTo(other.periode.fom)!! // En gyldig periode har alltid minst én dato
        }

        if (this.periode.tom == null) return 1
        if (other.periode.tom == null) return -1

        return this.periode.tom.compareTo(other.periode.tom)
    }

    companion object {
        private val offentligSektorJuridiskeEnhetstyper = listOf("STAT", "FKF", "FYLK", "KF", "KOMM", "SF", "SÆR")

        fun erArbeidsforholdetOffentligSektor(
            arbeidsforhold: List<Arbeidsforhold>,
            kontrollPeriode: Kontrollperiode,
            ytelse: Ytelse
        ): Boolean =
            vektetOffentligSektorArbeidsforhold(arbeidsforhold, kontrollPeriode, ytelse)

        fun List<Arbeidsforhold>.harArbeidsforholdIKontrollperiodeUtenlandsopphold(kontrollPeriode: Kontrollperiode): Boolean =
            this.arbeidsforholdForKontrollPeriode(kontrollPeriode).any { it.utenlandsopphold.isNotNullOrEmpty() }

        fun List<Arbeidsforhold>.erUtenlandsoppholdInnenforKontrollperiode(kontrollPeriode: Kontrollperiode): Boolean {
            val arbeidsforholdForKontrollperiode = this.arbeidsforholdForKontrollPeriode(kontrollPeriode)
            val utenlandsopphold = arbeidsforholdForKontrollperiode.flatMap { it.utenlandsopphold ?: listOf() }

            return utenlandsopphold.any {
                (
                    if (it.periode?.fom == null || it.periode.tom == null) {
                        Periode(
                            it.rapporteringsperiode.atDay(1),
                            it.rapporteringsperiode.atEndOfMonth()
                        ).overlapper(kontrollPeriode.periode)
                    } else {
                        it.periode.overlapper(kontrollPeriode.periode)
                    }
                    )
            }
        }

        private fun vektetOffentligSektorArbeidsforhold(
            arbeidsforhold: List<Arbeidsforhold>,
            kontrollPeriode: Kontrollperiode,
            ytelse: Ytelse
        ): Boolean =
            arbeidsforhold.filter {
                it.arbeidsgiver.juridiskeEnheter != null &&
                    it.arbeidsgiver.juridiskeEnheter.all { enhetstype -> enhetstype != null && enhetstype.enhetstype in offentligSektorJuridiskeEnhetstyper }
            }.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(25.0, kontrollPeriode, ytelse)

        infix fun List<Arbeidsforhold>.erAlleArbeidsgivereOrganisasjon(kontrollPeriode: Kontrollperiode): Boolean {
            return arbeidsforholdForKontrollPeriode(kontrollPeriode).stream()
                .allMatch { it.arbeidsgivertype == OpplysningspliktigArbeidsgiverType.Organisasjon }
        }

        infix fun List<Arbeidsforhold>.antallAnsatteHosArbeidsgivere(kontrollPeriode: Kontrollperiode): List<Int> =
            ansatteHosArbeidsgivere(kontrollPeriode).map { it.antall ?: 0 }

        infix fun List<Arbeidsforhold>.antallAnsatteHosArbeidsgiversJuridiskeEnheter(kontrollPeriode: Kontrollperiode): List<Int?> =
            arbeidsforholdForKontrollPeriodeMedStillingsprosentOver0(kontrollPeriode).filter {
                it.arbeidsgiver.juridiskeEnheter.isNotNullOrEmpty()
            }.flatMap { p -> p.arbeidsgiver.juridiskeEnheter!!.map { r -> r?.antallAnsatte ?: 0 } }

        infix fun List<Arbeidsforhold>.arbeidsforholdForYrkestype(kontrollPeriode: Kontrollperiode): List<Arbeidsforholdstype> =
            arbeidsforholdForKontrollPeriode(kontrollPeriode).map { it.arbeidsforholdstype }

        infix fun List<Arbeidsforhold>.sisteArbeidsforholdYrkeskode(kontrollPeriode: Kontrollperiode): List<String> =
            arbeidsforholdForKontrollPeriode(kontrollPeriode).flatMap { it.arbeidsavtaler }.map { it.yrkeskode }

        infix fun List<Arbeidsforhold>.skipsregisterErNOREllerNISOgFartsområdeErInnenriks(kontrollPeriode: Kontrollperiode): Boolean {
            val yrkeskoderForSkipsfart = YrkeskoderForSkipsfart.values().map { it.yrkesKode }

            return arbeidsforholdForKontrollPeriode(kontrollPeriode)
                .flatMap { it.arbeidsavtaler }
                .all {
                    it.skipsregister == Skipsregister.NOR ||
                        (it.skipsregister == Skipsregister.NIS && it.fartsomraade == Fartsomraade.INNENRIKS) ||
                        (it.skipsregister == Skipsregister.NIS && it.yrkeskode in yrkeskoderForSkipsfart)
                }
        }

        infix fun List<Arbeidsforhold>.konkursStatuserArbeidsgivere(kontrollPeriode: Kontrollperiode): List<String?>? {
            return arbeidsforholdForKontrollPeriode(kontrollPeriode).flatMap { it.arbeidsgiver.konkursStatus.orEmpty() }
        }

        fun List<Arbeidsforhold>.filtrerUtArbeidsgivereMedFærreEnn6Ansatte(kontrollPeriode: Kontrollperiode) =
            arbeidsforholdForKontrollPeriode(kontrollPeriode).map { it.arbeidsgiver }
                .filter { !it.ansatte?.finnesMindreEnn(6).isNullOrEmpty() }

        /**
         * Sjekk om arbeidsfoholdet er sammenhengende i kontrollperioden
         */
        fun List<Arbeidsforhold>.erSammenhengendeIKontrollPeriode(
            kontrollPeriode: Kontrollperiode,
            ytelse: Ytelse,
            tillatDagersHullIPeriode: Long
        ): Boolean {

            val arbeidsforholdForNorskArbeidsgiver = this.arbeidsforholdForKontrollPeriode(kontrollPeriode)

            if (arbeidsforholdForNorskArbeidsgiver.size > 10) {
                merEnn10ArbeidsforholdCounter(ytelse).increment()
                return false
            }

            if (arbeidsforholdForNorskArbeidsgiver.any { it.periode.fom == null }) {
                harIkkeArbeidsforhold12MndTilbakeCounter(ytelse).increment()
                return false
            }

            if (arbeidsforholdForNorskArbeidsgiver.none { it.periode.fom?.isBefore(kontrollPeriode.fom.plusDays(4))!! }) {
                harIkkeArbeidsforhold12MndTilbakeCounter(ytelse).increment()

                if (arbeidsforholdForNorskArbeidsgiver.isNotEmpty()) {
                    val antallDagerDiff = abs(
                        ChronoUnit.DAYS.between(
                            kontrollPeriode.fom,
                            arbeidsforholdForNorskArbeidsgiver.minOrNull()!!.periode.fom
                        )
                    )
                    antallDagerUtenArbeidsforhold(ytelse).record(antallDagerDiff.toDouble())
                }

                return false
            }

            var totaltAntallDagerDiff: Long = 0
            val finnesDeltidArbeidsforhold = arbeidsforholdForNorskArbeidsgiver.finnesDeltidArbeidsforhold()
            var forrigeTilDato: LocalDate? = null
            val sortertArbeidsforholdEtterPeriode = arbeidsforholdForNorskArbeidsgiver.sorted()
            for (arbeidsforhold in sortertArbeidsforholdEtterPeriode) { // Sjekker at alle påfølgende arbeidsforhold er sammenhengende
                if (forrigeTilDato != null && !erDatoerSammenhengende(forrigeTilDato, arbeidsforhold.periode.fom, tillatDagersHullIPeriode)) {
                    val antallDagerDiff = abs(ChronoUnit.DAYS.between(forrigeTilDato, arbeidsforhold.periode.fom))
                    totaltAntallDagerDiff += antallDagerDiff
                    antallDagerMellomArbeidsforhold(ytelse).record(antallDagerDiff.toDouble())
                    usammenhengendeArbeidsforholdCounter(ytelse).increment()

                    if (finnesDeltidArbeidsforhold || totaltAntallDagerDiff > lovligAntallDagerBorte(ytelse, kontrollPeriode, tillatDagersHullIPeriode)) {
                        return false
                    }
                }
                if (arbeidsforhold.periode.tom == null || forrigeTilDato == null || arbeidsforhold.periode.tom.isAfter(
                    forrigeTilDato
                )
                ) {
                    forrigeTilDato = arbeidsforhold.periode.tom
                }

                if (forrigeTilDato == null || forrigeTilDato.isAfter(kontrollPeriode.tom)) return true
            }

            if (forrigeTilDato != null) {
                return !forrigeTilDato.isBefore(kontrollPeriode.tom)
            }

            return true
        }

        private fun lovligAntallDagerBorte(ytelse: Ytelse, kontrollPeriode: Kontrollperiode, tillatDagersHullIPeriode: Long): Int {
            when (ytelse) {
                Ytelse.SYKEPENGER -> {
                    if (!kontrollPeriode.isReferansePeriode)
                        return 35
                    else {
                        return tillatDagersHullIPeriode.toInt()
                    }
                }
                else -> return 35
            }
        }

        infix fun List<Arbeidsforhold>.arbeidsforholdForDato(dato: LocalDate): List<Arbeidsforhold> =
            this.filter {
                it.periode.overlapper(Periode(dato, dato))
            }.sorted()

        fun List<Arbeidsforhold>.finnesDeltidArbeidsforhold(): Boolean =
            this.any { it.arbeidsavtaler.any { p -> p.stillingsprosent != null && p.stillingsprosent < 100.0 } }

        fun List<Arbeidsforhold>.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(
            gittStillingsprosent: Double,
            kontrollPeriode: Kontrollperiode,
            ytelse: Ytelse
        ): Boolean {
            val arbeidsforholdForKontrollPeriode =
                this.arbeidsforholdForKontrollPeriodeMedStillingsprosentOver0(kontrollPeriode)
            var samletStillingsprosent = 0.0

            val list = mutableListOf<Arbeidsforhold>()
            for (arbeidsforhold in arbeidsforholdForKontrollPeriode) {
                val vektetStillingsprosentForArbeidsforhold =
                    arbeidsforhold.vektetStillingsprosentForArbeidsforhold(kontrollPeriode, false)

                list.addAll(
                    this.arbeidsforholdForKontrollPeriode(kontrollPeriode)
                        .parallelleArbeidsforhold(arbeidsforhold, kontrollPeriode)
                )
                if (vektetStillingsprosentForArbeidsforhold < gittStillingsprosent &&
                    this.arbeidsforholdForKontrollPeriode(kontrollPeriode)
                        .ingenAndreParallelleArbeidsforhold(arbeidsforhold, kontrollPeriode)
                ) {
                    if (!list.contains(arbeidsforhold)) return false
                }

                samletStillingsprosent += vektetStillingsprosentForArbeidsforhold
                stillingsprosentCounter(vektetStillingsprosentForArbeidsforhold, ytelse.name()).increment()
            }
            samletStillingsprosentCounter(samletStillingsprosent, ytelse.name()).increment()
            return samletStillingsprosent >= gittStillingsprosent
        }

        fun Arbeidsforhold.vektetStillingsprosentForArbeidsforhold(
            kontrollPeriode: Kontrollperiode,
            beregnGjennomsnittForKontrollperioden: Boolean = false
        ): Double {

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

        fun List<Arbeidsforhold>.beregnGjennomsnittligStillingsprosentForGrafana(kontrollPeriode: Kontrollperiode): Double {
            var sumStillingsprosent = 0.0

            for (arbeidsforhold in this) {
                sumStillingsprosent += arbeidsforhold.vektetStillingsprosentForArbeidsforhold(kontrollPeriode, true)
            }

            return sumStillingsprosent
        }

        private fun List<Arbeidsforhold>.ingenAndreParallelleArbeidsforhold(
            arbeidsforhold: Arbeidsforhold,
            kontrollPeriode: Kontrollperiode
        ): Boolean =
            this.none {
                it.periode.intersection(kontrollPeriode).periode.encloses(
                    arbeidsforhold.periode.intersection(
                        kontrollPeriode
                    ).periode
                ) &&
                    it != arbeidsforhold &&
                    it.arbeidsavtaler.any { p -> p.stillingsprosent == null || p.stillingsprosent > 0.0 }
            }

        private fun List<Arbeidsforhold>.parallelleArbeidsforhold(
            arbeidsforhold: Arbeidsforhold,
            kontrollPeriode: Kontrollperiode
        ): List<Arbeidsforhold> =
            this.filter {
                it.periode.intersection(kontrollPeriode).periode.encloses(
                    arbeidsforhold.periode.intersection(
                        kontrollPeriode
                    ).periode
                ) &&
                    it != arbeidsforhold &&
                    it.arbeidsavtaler.any { p -> p.stillingsprosent == null || p.stillingsprosent > 0.0 }
            }

        private infix fun List<Arbeidsforhold>.ansatteHosArbeidsgivere(kontrollPeriode: Kontrollperiode): List<Ansatte> =
            arbeidsgivereIArbeidsforholdForNorskArbeidsgiver(kontrollPeriode).mapNotNull { it.ansatte }.flatten()

        private infix fun List<Arbeidsforhold>.arbeidsgivereIArbeidsforholdForNorskArbeidsgiver(kontrollPeriode: Kontrollperiode): List<Arbeidsgiver> {
            return arbeidsforholdForKontrollPeriode(kontrollPeriode).map { it.arbeidsgiver }
        }

        infix fun List<Arbeidsforhold>.orgnummerForKontrollperiode(kontrollPeriode: Kontrollperiode): List<String> {
            return arbeidsforholdForKontrollPeriode(kontrollPeriode).map { it.arbeidsgiver.organisasjonsnummer ?: "null" }
        }

        infix fun List<Arbeidsforhold>.aaRegUtenlandsoppholdLandkodeForKontrollperiode(kontrollPeriode: Kontrollperiode): List<String> {
            val landkoder = arbeidsforholdForKontrollPeriode(kontrollPeriode)
                .flatMap {
                    it.utenlandsopphold?.hentLandkoder() ?: listOf("null")
                }

            return if (landkoder.all { it.equals(null) }) listOf("null") else landkoder
        }

        infix fun List<Arbeidsforhold>.aaRegUtenlandsoppholdPeriodeForKontrollperiode(kontrollPeriode: Kontrollperiode): List<Periode?> {
            val utenlandsopphold = arbeidsforholdForKontrollPeriode(kontrollPeriode).flatMap {
                it.utenlandsopphold ?: listOf(null)
            }
            return if (utenlandsopphold.all { it == null }) listOf(null) else utenlandsopphold.map { it?.periode }
        }

        infix fun List<Arbeidsforhold>.skipsregisterFartsomradeOgSkipstypeForKontrollperiode(kontrollPeriode: Kontrollperiode): List<String?> {
            val skipsinfo = arbeidsavtalerForKontrollperiode(kontrollPeriode).map {
                listOf(
                    it.skipsregister?.name,
                    it.fartsomraade?.name,
                    it.skipstype?.name
                )
            }.flatten()

            return if (skipsinfo.all { it == null }) listOf("null") else skipsinfo
        }

        private fun List<Utenlandsopphold>.hentLandkoder(): List<String> =
            this.map { it.landkode }

        private fun List<Arbeidsforhold>.arbeidsforholdForKontrollPeriode(kontrollPeriode: Kontrollperiode) =
            this.filter {
                it.periode.overlapper(kontrollPeriode.periode)
            }

        private fun List<Arbeidsforhold>.arbeidsavtalerForKontrollperiode(kontrollPeriode: Kontrollperiode): List<Arbeidsavtale> {
            return arbeidsforholdForKontrollPeriode(kontrollPeriode).flatMap { it.arbeidsavtaler }
        }

        fun List<Arbeidsforhold>.arbeidsforholdForKontrollPeriodeMedStillingsprosentOver0(kontrollPeriode: Kontrollperiode) =
            this.filter {
                it.periode.overlapper(kontrollPeriode.periode) &&
                    it.arbeidsavtaler.any { p -> p.stillingsprosent == null || p.stillingsprosent > 0.0 }
            }

        fun fraOgMedDatoForArbeidsforhold(førsteDatoForYtelse: LocalDate): LocalDate {
            return førsteDatoForYtelse.minusYears(1).minusDays(1)
        }
    }
}
