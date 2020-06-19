package no.nav.medlemskap.regler.common

import no.nav.medlemskap.common.dekningKoderCounter
import no.nav.medlemskap.common.stillingsprosentCounter
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Funksjoner.er
import no.nav.medlemskap.regler.common.Funksjoner.harSammenhengendeMedlemskapIHeleGittPeriode
import org.threeten.extra.Interval
import java.time.LocalDate
import java.time.temporal.ChronoUnit


class Personfakta(val datagrunnlag: Datagrunnlag) {

    private val datohjelper = Datohjelper(datagrunnlag.periode)
    private val statsborgerskap = datagrunnlag.personhistorikk.statsborgerskap
    val arbeidsforhold = datagrunnlag.arbeidsforhold
    private val bostedadresser = datagrunnlag.personhistorikk.bostedsadresser


    companion object {
        fun initialiserFakta(datagrunnlag: Datagrunnlag) = Personfakta(datagrunnlag)
    }

    fun finnesPersonIMedlSiste12mnd(): Boolean {
        return brukerensPerioderIMedlSiste12Mnd().isNotEmpty()
    }

    fun personensPerioderIMedl(): List<Medlemskap> = datagrunnlag.medlemskap

    fun personensMedlemskapsperioderIMedlForPeriode(kontrollPeriode: Periode): List<Medlemskap> {
        return datagrunnlag.medlemskap.filter {
            lagInterval(Periode(it.fraOgMed, it.tilOgMed)).overlaps(kontrollPeriode.interval())
        }
    }

    fun brukerensPerioderIMedlSiste12Mnd(): List<Medlemskap> {
        return personensMedlemskapsperioderIMedlForPeriode(datohjelper.kontrollPeriodeForMedl())
    }

    fun personensOppgaverIGsak(): List<Oppgave> = datagrunnlag.oppgaver

    fun personensDokumenterIJoark(): List<Journalpost> = datagrunnlag.dokument

    fun hentStatsborgerskapVedStartAvKontrollperiode(): List<String> =
            hentStatsborgerskapFor(datohjelper.kontrollperiodeForStatsborgerskap().fom!!)

    fun hentStatsborgerskapVedSluttAvKontrollperiode(): List<String> =
            hentStatsborgerskapFor(datohjelper.kontrollperiodeForStatsborgerskap().tom!!)


    fun arbeidsforholdForStillingsprosent(): List<Arbeidsforhold> {
        return arbeidsforhold.filter {
            periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)),
                    datohjelper.kontrollPeriodeForStillingsprosent())
        }
    }

    fun arbeidsforholdForDato(dato: LocalDate): List<Arbeidsforhold> {
        return arbeidsforhold.filter {
            periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)),
                    Periode(dato, dato))
        }.sorted()
    }

    fun bostedAdresseForDato(dato: LocalDate): List<Adresse> {
        return bostedadresser.filter {
            periodefilter(lagInterval(Periode(it.fom, it.tom)),
                    Periode(dato, dato))
        }
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

            if (vektetStillingsprosentForArbeidsforhold < gittStillingsprosent
                    && ingenAndreParallelleArbeidsforhold(arbeidsforhold)) {
                return false
            }
            stillingsprosentCounter(vektetStillingsprosentForArbeidsforhold).increment()
        }

        return true
    }

    private fun beregnVektetStillingsprosentForArbeidsforhold(arbeidsforhold: Arbeidsforhold): Double {
        val kontrollPeriodeForStillingsprosent = datohjelper.kontrollPeriodeForStillingsprosent()
        val totaltAntallDager = kontrollPeriodeForStillingsprosent.fom!!.until(kontrollPeriodeForStillingsprosent.tom!!, ChronoUnit.DAYS).toDouble()
        var vektetStillingsprosentForArbeidsforhold = 0.0
        for (arbeidsavtale in arbeidsforhold.arbeidsavtaler) {
            val stillingsprosent = arbeidsavtale.stillingsprosent ?: 100.0
            val tilDato = arbeidsavtale.periode.tom ?: arbeidsforhold.periode.tom
            ?: kontrollPeriodeForStillingsprosent.tom
            var antallDager = kontrollPeriodeForStillingsprosent.fom.until(tilDato, ChronoUnit.DAYS).toDouble()
            if (antallDager > totaltAntallDager) {
                antallDager = totaltAntallDager
            }
            vektetStillingsprosentForArbeidsforhold += (antallDager / totaltAntallDager) * stillingsprosent
        }
        return vektetStillingsprosentForArbeidsforhold
    }

    private fun ingenAndreParallelleArbeidsforhold(arbeidsforhold: Arbeidsforhold): Boolean {
        val andreArbeidsforhold = arbeidsforholdForStillingsprosent() as ArrayList
        andreArbeidsforhold.remove(arbeidsforhold)

        if (andreArbeidsforhold.any { it.periode.interval().encloses(arbeidsforhold.periode.interval()) }) return false

        return true
    }

    private fun hentStatsborgerskapFor(dato: LocalDate): List<String> =
            statsborgerskap.filter {
                Periode(it.fom, it.tom).interval().contains(lagInstantStartOfDay(dato))
            }.map { it.landkode }

    private fun periodefilter(periodeDatagrunnlag: Interval, periode: Periode): Boolean {
        return periodeDatagrunnlag.overlaps(lagInterval(periode)) || periodeDatagrunnlag.encloses(lagInterval(periode))
    }

    private fun medlemskapsPerioderOver12MndPeriode(erMedlem: Boolean): List<Medlemskap> {
        return brukerensPerioderIMedlSiste12Mnd().filter {
            it.erMedlem == erMedlem && it.lovvalg er "ENDL"
        }
    }

    fun erMedlemskapsperioderOver12Mnd(erMedlem: Boolean): Boolean {
        return brukerensPerioderIMedlSiste12Mnd().filter { it.erMedlem == erMedlem && it.lovvalg er "ENDL" }
                .harSammenhengendeMedlemskapIHeleGittPeriode(datohjelper.kontrollPeriodeForMedl())
    }

    fun medlemskapsPerioderOver12MndPeriodeDekning(): List<String> {
        return medlemskapsPerioderOver12MndPeriode(true).map {
            dekningKoderCounter(it.dekning ?: "Uten kodeverdi i dekning")
            it.dekning.orEmpty()
        }
    }

    fun harSammeArbeidsforholdSidenFomDatoFraMedl(): Boolean {

        return arbeidsforholdForDato(tidligsteFraOgMedDatoForMedl()).isNotEmpty() &&
                (ulikeArbeidsforholdMenSammeArbeidsgiver() ||
                        arbeidsforholdForDato(tidligsteFraOgMedDatoForMedl()) == arbeidsforholdForDato(datohjelper.tilOgMedDag()))

    }

    private fun ulikeArbeidsforholdMenSammeArbeidsgiver() =
            (arbeidsforholdForDato(tidligsteFraOgMedDatoForMedl()) != arbeidsforholdForDato(datohjelper.tilOgMedDag()) &&
                    arbeidsforholdForDato(tidligsteFraOgMedDatoForMedl()).map { it.arbeidsgiver.identifikator } == arbeidsforholdForDato(datohjelper.tilOgMedDag()).map { it.arbeidsgiver.identifikator })

    private fun tidligsteFraOgMedDatoForMedl(): LocalDate {
        return brukerensPerioderIMedlSiste12Mnd().sorted().first().fraOgMed
    }

    fun harMedlPeriodeMedOgUtenMedlemskap(): Boolean {
        return brukerensPerioderIMedlSiste12Mnd().any { it.erMedlem }
                && brukerensPerioderIMedlSiste12Mnd().any { !it.erMedlem }
    }

    fun harGyldigeMedlemskapsperioder(): Boolean {
        return brukerensPerioderIMedlSiste12Mnd().none { it.tilOgMed.isAfter(it.fraOgMed.plusYears(5)) }
    }

    fun harSammeAdresseSidenFomDatoFraMedl(): Boolean {
        return bostedAdresseForDato(tidligsteFraOgMedDatoForMedl()).size == 1 &&
                bostedAdresseForDato(tidligsteFraOgMedDatoForMedl()) == bostedAdresseForDato(datohjelper.tilOgMedDag())
    }
}