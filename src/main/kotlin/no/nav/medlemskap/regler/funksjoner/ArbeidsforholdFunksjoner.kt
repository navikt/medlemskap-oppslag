package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.common.*
import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.domene.Arbeidsgiver
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.Ytelse.Companion.metricName
import no.nav.medlemskap.regler.common.Funksjoner
import no.nav.medlemskap.regler.common.erDatoerSammenhengende
import no.nav.medlemskap.regler.common.interval
import no.nav.medlemskap.regler.common.lagInterval
import no.nav.medlemskap.services.aareg.AaRegOpplysningspliktigArbeidsgiverType
import no.nav.medlemskap.services.ereg.Ansatte
import org.threeten.extra.Interval
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors
import kotlin.math.abs

object ArbeidsforholdFunksjoner {

    infix fun List<Arbeidsforhold>.erAlleArbeidsgivereOrganisasjon(kontrollPeriode: Periode): Boolean {
        return arbeidsforholdForKontrollPeriode(kontrollPeriode).stream().allMatch { it.arbeidsgivertype == AaRegOpplysningspliktigArbeidsgiverType.Organisasjon }
    }

    infix fun List<Arbeidsforhold>.antallAnsatteHosArbeidsgivere(kontrollPeriode: Periode): List<Int?> =
            ansatteHosArbeidsgivere(kontrollPeriode).map { it.antall }

    infix fun List<Arbeidsforhold>.arbeidsforholdForYrkestype(kontrollPeriode: Periode): List<String> =
            this.filter {
                periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)), kontrollPeriode)
            }.map { it.arbeidsfolholdstype.navn }


    infix fun List<Arbeidsforhold>.sisteArbeidsforholdYrkeskode(kontrollPeriode: Periode): List<String> =
            this.filter {
                periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)), kontrollPeriode)
            }.flatMap { it.arbeidsavtaler }.map { it.yrkeskode }


    infix fun List<Arbeidsforhold>.sisteArbeidsforholdSkipsregister(kontrollPeriode: Periode): List<String> =
            this.filter {
                periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)), kontrollPeriode)
            }.flatMap { it -> it.arbeidsavtaler.map { it.skipsregister?.name ?: "" } }

    infix fun List<Arbeidsforhold>.konkursStatuserArbeidsgivere(kontrollPeriode: Periode): List<String?>? {
        return arbeidsforholdForKontrollPeriode(kontrollPeriode).flatMap { it.arbeidsgiver.konkursStatus.orEmpty() }
    }

    /**
     * På dette tidspunktet er det kjent at bruker er i et aktivt arbeidsforhold.
     * Trenger derfor kun å sjekke at bruker har et arbeidsforhold minumum 12 mnd tilbake og at påfølgende arbeidsforholdene er sammenhengende.
     */
    fun List<Arbeidsforhold>.erSammenhengendeIKontrollPeriode(kontrollPeriode: Periode, ytelse: Ytelse): Boolean {

        var forrigeTilDato: LocalDate? = null
        val arbeidsforholdForNorskArbeidsgiver = this.arbeidsforholdForKontrollPeriode(kontrollPeriode)

        if (arbeidsforholdForNorskArbeidsgiver.size > 10) {
            merEnn10ArbeidsforholdCounter(ytelse).increment()
            return false
        }

        if (arbeidsforholdForNorskArbeidsgiver.none { it.periode.fom?.isBefore(kontrollPeriode.fom?.plusDays(1))!! }) {
            harIkkeArbeidsforhold12MndTilbakeCounter(ytelse).increment()

            if (arbeidsforholdForNorskArbeidsgiver.isNotEmpty()) {
                val antallDagerDiff = ChronoUnit.DAYS.between(kontrollPeriode.fom, arbeidsforholdForNorskArbeidsgiver.min()!!.periode.fom)
                antallDagerUtenArbeidsforhold(ytelse).record(antallDagerDiff.toDouble())
            }

            return false
        }

        val sortertArbeidsforholdEtterPeriode = arbeidsforholdForNorskArbeidsgiver.stream().sorted().collect(Collectors.toList())
        for (arbeidsforhold in sortertArbeidsforholdEtterPeriode) { //Sjekker at alle påfølgende arbeidsforhold er sammenhengende
            if (forrigeTilDato != null && !erDatoerSammenhengende(forrigeTilDato, arbeidsforhold.periode.fom)) {
                val antallDagerDiff = abs(ChronoUnit.DAYS.between(forrigeTilDato, arbeidsforhold.periode.fom))
                antallDagerMellomArbeidsforhold(ytelse).record(antallDagerDiff.toDouble())
                usammenhengendeArbeidsforholdCounter(ytelse).increment()
                return false
            }
            forrigeTilDato = arbeidsforhold.periode.tom
        }

        return true
    }

    infix fun List<Arbeidsforhold>.arbeidsforholdForDato(dato: LocalDate): List<Arbeidsforhold> =
            this.filter {
                Funksjoner.periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)),
                        Periode(dato, dato))
            }.sorted()


    fun List<Arbeidsforhold>.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(gittStillingsprosent: Double, kontrollPeriode: Periode, ytelse: Ytelse): Boolean {

        val arbeidsforholdForKontrollPeriode = this.arbeidsforholdForKontrollPeriode(kontrollPeriode)

        var samletStillingsprosent = 0.0

        for (arbeidsforhold in arbeidsforholdForKontrollPeriode) {
            val vektetStillingsprosentForArbeidsforhold = arbeidsforhold.vektetStillingsprosentForArbeidsforhold(kontrollPeriode)

            if (vektetStillingsprosentForArbeidsforhold < gittStillingsprosent &&
                    this.arbeidsforholdForKontrollPeriode(kontrollPeriode).ingenAndreParallelleArbeidsforhold(arbeidsforhold)) {
                return false
            }

            samletStillingsprosent += vektetStillingsprosentForArbeidsforhold
            stillingsprosentCounter(vektetStillingsprosentForArbeidsforhold, ytelse.metricName()).increment()
        }

        return samletStillingsprosent >= gittStillingsprosent
    }

    private fun Arbeidsforhold.vektetStillingsprosentForArbeidsforhold(kontrollPeriode: Periode): Double {
        val totaltAntallDager = kontrollPeriode.fom!!.until(kontrollPeriode.tom!!, ChronoUnit.DAYS).toDouble()
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
        this.none { it.periode.interval().encloses(arbeidsforhold.periode.interval()) && it != arbeidsforhold }

    private infix fun List<Arbeidsforhold>.ansatteHosArbeidsgivere(kontrollPeriode: Periode): List<Ansatte> =
            arbeidsgivereIArbeidsforholdForNorskArbeidsgiver(kontrollPeriode).mapNotNull { it.ansatte }.flatten()


    private infix fun List<Arbeidsforhold>.arbeidsgivereIArbeidsforholdForNorskArbeidsgiver(kontrollPeriode: Periode): List<Arbeidsgiver> {
        return arbeidsforholdForKontrollPeriode(kontrollPeriode).stream().map { it.arbeidsgiver }.collect(Collectors.toList())
    }

    private fun List<Arbeidsforhold>.arbeidsforholdForKontrollPeriode(kontrollPeriode: Periode) =
            this.filter {
                periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)),
                        kontrollPeriode)
            }

    private fun periodefilter(periodeDatagrunnlag: Interval, periode: Periode): Boolean {
        return periodeDatagrunnlag.overlaps(lagInterval(periode)) || periodeDatagrunnlag.encloses(lagInterval(periode))
    }
}