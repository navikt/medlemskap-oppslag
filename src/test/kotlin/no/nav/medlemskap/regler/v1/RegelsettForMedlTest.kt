package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.assertSvar
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.evaluer
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class RegelsettForMedlTest {

    private val personleser = Personleser()

    @Test
    fun `amerikansk person med vedtak i medl får uavklart på manuelle vedtak`() {
        assertSvar("1", Svar.JA, evaluer(personleser.amerikanskMedl()), Svar.UAVKLART)
    }

    @Test
    fun `norsk person med opplysninger i medl`() {
        assertSvar("1", Svar.JA, evaluer(personleser.norskMedOpplysningerIMedl()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med og uten medlemskap i 12 mnd perioden`() {
        assertSvar("1.1", Svar.JA, evaluer(personleser.amerikanskMedOgUtenMedlemskap()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med periode med medlemskap`() {
        assertSvar("1.2", Svar.JA, evaluer(personleser.amerikanskMedl()), Svar.UAVKLART)
    }

    @Test
    fun `norsk person med periode uten medlemskap`() {
        assertSvar("1.2", Svar.NEI, evaluer(personleser.norskUtenMedlemskapIMedl()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person uten medlemskap i hele 12 mnd perioden`() {
        assertSvar("1.2.1", Svar.JA, evaluer(personleser.amerikanskUtenMedlemskapOver12MndPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `norsk person uten medlemskap delvis i 12 mnd perioden`() {
        assertSvar("1.2.1", Svar.NEI, evaluer(personleser.norskUtenMedlemskapDelvisInnenfor12MndPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person uten medlemskap med endret arbeidsforhold`() {
        assertSvar("1.2.2", Svar.NEI, evaluer(personleser.amerikanskUtenMedlemskapEndretArbeidsforhold()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person uten medlemskap uten arbeidsforhold`() {
        assertSvar("1.2.2", Svar.NEI, evaluer(personleser.amerikanskUtenMedlemskapUtenArbeidsforhold()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person uten medlemskap med endret adresse`() {
        assertSvar("1.2.3", Svar.NEI, evaluer(personleser.amerikanskUtenMedlemskapEndretAdresse()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person uten medlemskap med uendret adresse og arbeidsforhold`() {
        assertSvar("1.2.3", Svar.JA, evaluer(personleser.amerikanskUtenMedlemskapSammeArbeidsforholdOgAdresse()), Svar.NEI)
    }

    @Test
    fun `amerikansk person med UAVK (uavklart) statuskode i lovvalg fra medl`() {
        assertSvar("1.2", Svar.NEI, evaluer(personleser.amerikanskUtenMedlemskapLovvalgStatuskodeUavklart()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med medlemskap i hele 12 mnd perioden`() {
        assertSvar("1.3", Svar.JA, evaluer(personleser.amerikanskMedMedlemskapOver12MndPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med medlemskapdato over 5 år`() {
        assertSvar("1.3", Svar.NEI, evaluer(personleser.amerikanskMedMedlemskapUgyldigDato()), Svar.UAVKLART)
    }

    @Test
    fun `norsk person med medlemskap delvis i 12 mnd perioden`() {
        assertSvar("1.3", Svar.NEI, evaluer(personleser.norskMedMedlemskapDelvisInnenfor12MndPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med medlemskap med endret arbeidsforhold`() {
        assertSvar("1.4", Svar.NEI, evaluer(personleser.amerikanskMedMedlemskapEndretArbeidsforhold()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med medlemskap med endret adresse`() {
        assertSvar("1.5", Svar.NEI, evaluer(personleser.amerikanskMedMedlemskapEndretAdresse()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med medlemskap med uendret adresse og arbeidsforhold`() {
        assertSvar("1.5", Svar.JA, evaluer(personleser.amerikanskMedMedlemskapSammeArbeidsforholdOgAdresse()), Svar.JA)
    }

    @Test
    fun `amerikansk person med medlemskap med dekning i medl`() {
        assertSvar("1.6", Svar.JA, evaluer(personleser.amerikanskMedMedlemskapMedDekningIMedl()), Svar.JA)
    }

    @Test
    fun `amerikansk person med medlemskap uten dekning i medl`() {
        assertSvar("1.6", Svar.NEI, evaluer(personleser.amerikanskMedMedlemskapUtenDekningIMedl()), Svar.NEI)
    }
}