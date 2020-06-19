package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.common.harIkkeArbeidsforhold12MndTilbakeCounter
import no.nav.medlemskap.common.merEnn10ArbeidsforholdCounter
import no.nav.medlemskap.common.usammenhengendeArbeidsforholdCounter
import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.domene.Arbeidsgiver
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.regler.common.erDatoerSammenhengende
import no.nav.medlemskap.regler.common.lagInterval
import no.nav.medlemskap.services.aareg.AaRegOpplysningspliktigArbeidsgiverType
import no.nav.medlemskap.services.ereg.Ansatte
import org.threeten.extra.Interval
import java.time.LocalDate
import java.util.stream.Collectors

object ArbeidsforholdFunksjoner {

    infix fun List<Arbeidsforhold>.erArbeidsgivereOrganisasjon(kontrollPeriode: Periode): Boolean {
        return arbeidsforholdForNorskArbeidsgiver(kontrollPeriode).stream().allMatch { it.arbeidsgivertype == AaRegOpplysningspliktigArbeidsgiverType.Organisasjon }
    }

    infix fun List<Arbeidsforhold>.antallAnsatteHosArbeidsgivere(kontrollPeriode: Periode): List<Int?> =
            ansatteHosArbeidsgivere(kontrollPeriode).map { it.antall }

    infix fun List<Arbeidsforhold>.ansatteHosArbeidsgivere(kontrollPeriode: Periode): List<Ansatte> =
            arbeidsgivereIArbeidsforholdForNorskArbeidsgiver(kontrollPeriode).mapNotNull { it.ansatte }.flatten()


    infix fun List<Arbeidsforhold>.arbeidsgivereIArbeidsforholdForNorskArbeidsgiver(kontrollPeriode: Periode): List<Arbeidsgiver> {
        return arbeidsforholdForNorskArbeidsgiver(kontrollPeriode).stream().map { it.arbeidsgiver }.collect(Collectors.toList())
    }

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
        return arbeidsforholdForNorskArbeidsgiver(kontrollPeriode).flatMap { it.arbeidsgiver.konkursStatus.orEmpty() }
    }

    /**
     * På dette tidspunktet er det kjent at bruker er i et aktivt arbeidsforhold.
     * Trenger derfor kun å sjekke at bruker har et arbeidsforhold minumum 12 mnd tilbake og at påfølgende arbeidsforholdene er sammenhengende.
     */
    infix fun List<Arbeidsforhold>.erSammenhengendeIKontrollPeriode(kontrollPeriode: Periode): Boolean {

        var forrigeTilDato: LocalDate? = null
        val arbeidsforholdForNorskArbeidsgiver = this.arbeidsforholdForNorskArbeidsgiver(kontrollPeriode)

        if (arbeidsforholdForNorskArbeidsgiver.size > 10) {
            merEnn10ArbeidsforholdCounter().increment()
            return false
        }

        val harArbeidsforhold12MndTilbake = arbeidsforholdForNorskArbeidsgiver.stream().anyMatch { it.periode.fom?.isBefore(kontrollPeriode.fom?.plusDays(1))!! }
        if (!harArbeidsforhold12MndTilbake) {
            harIkkeArbeidsforhold12MndTilbakeCounter().increment()
            return false
        }

        val sortertArbeidsforholdEtterPeriode = arbeidsforholdForNorskArbeidsgiver.stream().sorted().collect(Collectors.toList())
        for (arbeidsforhold in sortertArbeidsforholdEtterPeriode) { //Sjekker at alle påfølgende arbeidsforhold er sammenhengende
            if (forrigeTilDato != null && !erDatoerSammenhengende(forrigeTilDato, arbeidsforhold.periode.fom)) {
                usammenhengendeArbeidsforholdCounter().increment()
                return false
            }
            forrigeTilDato = arbeidsforhold.periode.tom
        }

        return true
    }


    private fun List<Arbeidsforhold>.arbeidsforholdForNorskArbeidsgiver(kontrollPeriode: Periode) =
            this.filter {
                periodefilter(lagInterval(Periode(it.periode.fom, it.periode.tom)),
                        kontrollPeriode)
            }

    private fun periodefilter(periodeDatagrunnlag: Interval, periode: Periode): Boolean {
        return periodeDatagrunnlag.overlaps(lagInterval(periode)) || periodeDatagrunnlag.encloses(lagInterval(periode))
    }
}