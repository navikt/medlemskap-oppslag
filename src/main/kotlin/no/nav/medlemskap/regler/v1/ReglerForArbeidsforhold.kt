package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.alleEr
import no.nav.medlemskap.regler.common.Funksjoner.finnes
import no.nav.medlemskap.regler.common.Funksjoner.finnesMindreEnn
import no.nav.medlemskap.regler.common.Funksjoner.inneholderNoe
import no.nav.medlemskap.regler.common.Funksjoner.kunInneholder
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.antallAnsatteHosArbeidsgivere
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.arbeidsforholdForYrkestype
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.erAlleArbeidsgivereOrganisasjon
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.erSammenhengendeIKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.konkursStatuserArbeidsgivere
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.sisteArbeidsforholdSkipsregister
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.sisteArbeidsforholdYrkeskode
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.registrerStatsborgerskapGrafana

class ReglerForArbeidsforhold(
        val arbeidsforhold: List<Arbeidsforhold>,
        val periode: InputPeriode,
        ytelse: Ytelse,
        val reglerForLovvalg: ReglerForLovvalg
) : Regler(ytelse) {
    private val kontrollPeriodeForArbeidsforhold = Datohjelper(periode, ytelse).kontrollPeriodeForArbeidsforhold()

    override fun hentRegelflyt(): Regelflyt {
        val jobberBrukerPaaNorskSkipFlyt = lagRegelflyt(
                regel = jobberBrukerPaaNorskSkip,
                hvisJa = reglerForLovvalg.hentRegelflyt(),
                hvisNei = regelFlytUavklart(ytelse)
        )

        val erBrukerPilotEllerKabinansattFlyt = lagRegelflyt(
                regel = erBrukerPilotEllerKabinansatt,
                hvisJa = regelFlytUavklart(ytelse),
                hvisNei = reglerForLovvalg.hentRegelflyt()
        )

        val erArbeidsforholdetMaritimtFlyt = lagRegelflyt(
                regel = erArbeidsforholdetMaritimt,
                hvisJa = jobberBrukerPaaNorskSkipFlyt,
                hvisNei = erBrukerPilotEllerKabinansattFlyt
        )

        val erForetakAktivtFlyt = lagRegelflyt(
                regel = erForetakAktivt,
                hvisJa = erArbeidsforholdetMaritimtFlyt,
                hvisNei = regelFlytUavklart(ytelse)
        )

        val harForetakMerEnn5AnsatteFlyt = lagRegelflyt(
                regel = harForetakMerEnn5Ansatte,
                hvisJa = erForetakAktivtFlyt,
                hvisNei = regelFlytUavklart(ytelse)
        )

        val erArbeidsgiverOrganisasjonFlyt = lagRegelflyt(
                regel = erArbeidsgiverOrganisasjon,
                hvisJa = harForetakMerEnn5AnsatteFlyt,
                hvisNei = regelFlytUavklart(ytelse)
        )

        val harBrukerSammenhengendeArbeidsforholdSiste12MndFlyt = lagRegelflyt(
                regel = harBrukerSammenhengendeArbeidsforholdSiste12Mnd,
                hvisJa = erArbeidsgiverOrganisasjonFlyt,
                hvisNei = regelFlytUavklart(ytelse)
        )

        return harBrukerSammenhengendeArbeidsforholdSiste12MndFlyt
    }


    val harBrukerSammenhengendeArbeidsforholdSiste12Mnd = Regel(
            regelId = REGEL_3,
            ytelse = ytelse,
            operasjon = { sjekkSammenhengendeArbeidsforhold() }
    )

    val erArbeidsgiverOrganisasjon = Regel(
            regelId = REGEL_4,
            ytelse = ytelse,
            operasjon = { sjekkArbeidsgiver() }
    )

    val harForetakMerEnn5Ansatte = Regel(
            regelId = REGEL_5,
            ytelse = ytelse,
            operasjon = { sjekkOmForetakMerEnn5Ansatte() }
    )

    val erForetakAktivt = Regel(
            regelId = REGEL_6,
            ytelse = ytelse,
            operasjon = { sjekKonkursstatus() }
    )

    val erArbeidsforholdetMaritimt = Regel(
            regelId = REGEL_7,
            ytelse = ytelse,
            operasjon = { sjekkMaritim() }
    )

    val jobberBrukerPaaNorskSkip = Regel(
            regelId = REGEL_7_1,
            ytelse = ytelse,
            operasjon = { sjekkSkipsregister() }
    )

    val erBrukerPilotEllerKabinansatt = Regel(
            regelId = REGEL_8,
            ytelse = ytelse,
            operasjon = { sjekkYrkeskodeLuftfart() }
    )

    private val yrkeskoderLuftfart = YrkeskoderForLuftFart.values().map { it.styrk }


    private fun sjekkSammenhengendeArbeidsforhold(): Resultat =
            if (!arbeidsforhold.erSammenhengendeIKontrollPeriode(kontrollPeriodeForArbeidsforhold, ytelse)) {
                reglerForLovvalg.personhistorikk.statsborgerskap.registrerStatsborgerskapGrafana(kontrollPeriodeForArbeidsforhold, ytelse, REGEL_3)
                nei("Arbeidstaker har ikke sammenhengende arbeidsforhold siste 12 mnd")
            } else {
                ja()
            }

    private fun sjekkArbeidsgiver(): Resultat =
            when {
                !arbeidsforhold.erAlleArbeidsgivereOrganisasjon(kontrollPeriodeForArbeidsforhold) -> nei("Ikke alle arbeidsgivere er av typen organisasjon")
                else -> ja()
            }


    private fun sjekkOmForetakMerEnn5Ansatte(): Resultat {

        if (arbeidsforhold.antallAnsatteHosArbeidsgivere(kontrollPeriodeForArbeidsforhold) finnesMindreEnn 6) {
            reglerForLovvalg.personhistorikk.statsborgerskap.registrerStatsborgerskapGrafana(kontrollPeriodeForArbeidsforhold, ytelse, REGEL_5)
            return nei("Ikke alle arbeidsgivere har 6 ansatte eller flere")
        }

        return ja()
    }


    private fun sjekkMaritim(): Resultat =
            when {
                arbeidsforhold.arbeidsforholdForYrkestype(kontrollPeriodeForArbeidsforhold) alleEr Arbeidsforholdstype.MARITIM.navn -> ja()
                else -> nei()
            }


    private fun sjekkYrkeskodeLuftfart(): Resultat =
            when {
                arbeidsforhold sisteArbeidsforholdYrkeskode kontrollPeriodeForArbeidsforhold inneholderNoe yrkeskoderLuftfart -> ja()
                else -> nei()
            }


    private fun sjekkSkipsregister(): Resultat =
            when {
                arbeidsforhold sisteArbeidsforholdSkipsregister kontrollPeriodeForArbeidsforhold kunInneholder Skipsregister.NOR.name -> ja()
                else -> nei()
            }


    private fun sjekKonkursstatus(): Resultat =
            when {
                arbeidsforhold.konkursStatuserArbeidsgivere(kontrollPeriodeForArbeidsforhold).finnes() -> nei("Arbeidstaker har hatt arbeidsforhold til arbeidsgiver som har konkurs-status satt")
                else -> ja()
            }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForArbeidsforhold {
            val reglerForLovvalg = ReglerForLovvalg.fraDatagrunnlag(datagrunnlag)

            return ReglerForArbeidsforhold(datagrunnlag.arbeidsforhold, datagrunnlag.periode, datagrunnlag.ytelse, reglerForLovvalg)
        }
    }
}