package no.nav.medlemskap.regler.common

import no.nav.medlemskap.common.harIkkeArbeidsforhold12MndTilbakeCounter
import no.nav.medlemskap.common.merEnn10ArbeidsforholdCounter
import no.nav.medlemskap.common.stillingsprosentCounter
import no.nav.medlemskap.common.usammenhengendeArbeidsforholdCounter
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

    fun personensPerioderIMedl(): List<Medlemskap> = datagrunnlag.medlemskap

    fun finnesPersonIMedlSiste12mnd(): Boolean {
        return personensPerioderIMedlSiste12Mnd().isNotEmpty()
    }

    fun personensPerioderIMedlSiste12Mnd(): List<Medlemskap> {
        val medlemskapsperioderForPerson = ArrayList<Medlemskap>()
        for (medlemskap in datagrunnlag.medlemskap) {
            val medlemsintervall = lagInterval(Periode(medlemskap.fraOgMed, medlemskap.tilOgMed))
            val kontrollPeriodeForMedlIntervall = datohjelper.kontrollPeriodeForMedl().interval()
            if (medlemsintervall.overlaps(kontrollPeriodeForMedlIntervall)) medlemskapsperioderForPerson.add(medlemskap)
        }
        return medlemskapsperioderForPerson
    }

    private fun periodefilterForInnlukkedePerioder(periodeDatagrunnlag: Interval, periode: Periode): Boolean {
        return periodeDatagrunnlag.encloses(lagInterval(periode))
    }

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

        if (arbeidsforholdForNorskArbeidsgiver.size > 10) {
            merEnn10ArbeidsforholdCounter().increment()
            return false
        }

        val harArbeidsforhold12MndTilbake = arbeidsforholdForNorskArbeidsgiver.stream().anyMatch { it.periode.fom?.isBefore(datohjelper.kontrollPeriodeForNorskArbeidsgiver().fom?.plusDays(1))!! }
        if (!harArbeidsforhold12MndTilbake) {
            harIkkeArbeidsforhold12MndTilbakeCounter().increment()
            return false
        }

        val sortertArbeidsforholdEtterPeriode = arbeidsforholdForNorskArbeidsgiver.stream().sorted().collect(Collectors.toList())
        for (arbeidsforhold in sortertArbeidsforholdEtterPeriode) { //Sjekker at alle påfølgende arbeidsforhold er sammenhengende
            if (forrigeTilDato != null && !datohjelper.erDatoerSammenhengende(forrigeTilDato, arbeidsforhold.periode.fom)) {
                usammenhengendeArbeidsforholdCounter().increment()
                return false
            }
            forrigeTilDato = arbeidsforhold.periode.tom
        }

        return true
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

    fun harBrukerNorskBodstedsadresseInnenforSiste12Mnd(): Boolean {
        return datagrunnlag.personhistorikk.bostedsadresser.any {
            adresseLandskodeErNorsk(it) && adressensPeriodeOverlapperKontrollPerioden(it)
        }
    }

    fun harBrukerNorskPostadresseInnenforSiste12Mnd(): Boolean {
        return datagrunnlag.personhistorikk.postadresser.any {
            adresseLandskodeErNorsk(it) && adressensPeriodeOverlapperKontrollPerioden(it)
        }
    }

    private fun adresseLandskodeErNorsk(it: Adresse) = it.landkode == "NOR"

    private fun adressensPeriodeOverlapperKontrollPerioden(it: Adresse) =
            periodefilter(lagInterval(Periode(it.fom, it.tom)), datohjelper.kontrollPeriodeForNorskAdresse())

    fun sjekkBrukersPostadresseOgBostedsadresseLandskode(): Boolean {
        val harUtenlandskBostedsadresse = !harBrukerNorskBodstedsadresseInnenforSiste12Mnd()
        val harUtenlandskPostadresse = datagrunnlag.personhistorikk.postadresser.isNotEmpty() &&
                !harBrukerNorskPostadresseInnenforSiste12Mnd()

        return !(harUtenlandskBostedsadresse || harUtenlandskPostadresse)
    }

    fun harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTidIKontrollperiode(gittStillingsprosent: Double): Boolean {

        for (arbeidsforhold in arbeidsforholdForStillingsprosent()) {
            val vektetStillingsprosentForArbeidsforhold = beregnVektetStillingsprosentForArbeidsforhold(arbeidsforhold)

            stillingsprosentCounter(vektetStillingsprosentForArbeidsforhold).increment()

            if (vektetStillingsprosentForArbeidsforhold < gittStillingsprosent
                    && ingenAndreParallelleArbeidsforhold(arbeidsforhold)) {
                return false
            }
        }

        return true
    }

    private fun beregnVektetStillingsprosentForArbeidsforhold(arbeidsforhold: Arbeidsforhold): Double {
        val kontrollPeriodeForStillingsprosent = datohjelper.kontrollPeriodeForStillingsprosent()
        val totaltAntallDager = kontrollPeriodeForStillingsprosent.fom!!.until(kontrollPeriodeForStillingsprosent.tom!!, ChronoUnit.DAYS).toDouble()
        var vektetStillingsprosentForArbeidsforhold = 0.0
        for (arbeidsavtale in arbeidsforhold.arbeidsavtaler) {
            val stillingsprosent = arbeidsavtale.stillingsprosent ?: 100.0
            val tilDato = arbeidsavtale.periode.tom ?: arbeidsforhold.periode.tom ?: kontrollPeriodeForStillingsprosent.tom
            var antallDager = kontrollPeriodeForStillingsprosent.fom.until(tilDato, ChronoUnit.DAYS).toDouble()
            if (antallDager > totaltAntallDager) {
                antallDager = totaltAntallDager
            }
            vektetStillingsprosentForArbeidsforhold += (antallDager / totaltAntallDager) * stillingsprosent
        }
        return vektetStillingsprosentForArbeidsforhold
    }

    private fun ingenAndreParallelleArbeidsforhold(arbeidsforhold: Arbeidsforhold) : Boolean {
        val andreArbeidsforhold = arbeidsforholdForStillingsprosent() as ArrayList
        andreArbeidsforhold.remove(arbeidsforhold)

        if (andreArbeidsforhold.any { it.periode.interval().encloses(arbeidsforhold.periode.interval()) }) return false

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

    fun erPeriodeUtenMedlemskapInnenfor12MndPeriode(): Boolean {
        val personensPerioderIMedlSiste12Mnd = personensPerioderIMedlSiste12Mnd()

        for (medlemskap in personensPerioderIMedlSiste12Mnd) {

        }

        personensPerioderIMedlSiste12Mnd.find {
            Periode(it.fraOgMed, it.tilOgMed).interval().encloses(datohjelper.kontrollPeriodeForMedl().interval())
        }

        return true
    }
}