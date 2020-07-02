package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.assertSvar
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.evaluer
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
class RegelsettForMedlTest {

    private val personleser = Personleser()

    @Test
    fun `amerikansk person med vedtak i medl får uavklart på manuelle vedtak`() {
        assertSvar("A", Svar.JA, evaluer(personleser.amerikanskMedl()), Svar.UAVKLART)
    }

    @Test
    fun `norsk person med opplysninger i medl`() {
        assertSvar("A", Svar.JA, evaluer(personleser.norskMedOpplysningerIMedl()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med vedtak i medl og gosys får uavklart på manuelle vedtak`() {
        assertSvar("B", Svar.JA, evaluer(personleser.amerikanskGosys()), Svar.UAVKLART)
    }


    @Test
    fun `amerikansk person med og uten medlemskap i 12 mnd perioden`() {
        assertSvar("1.1", Svar.JA, evaluer(personleser.amerikanskMedOgUtenMedlemskap()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med periode med medlemskap`() {
        assertSvar("1.2", Svar.JA, evaluer(personleser.brukerHarPeriodeMedMedlemskap()), Svar.UAVKLART)
    }

    @Test
    fun `norsk person med periode uten medlemskap`() {
        assertSvar("1.2.1", Svar.NEI, evaluer(personleser.norskUtenMedlemskapIMedl()), Svar.UAVKLART)
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
    fun `amerikansk person uten medlemskap med endret arbeidsforhold, men med samme arbeidsgiver`() {
        assertSvar("1.2.2", Svar.JA, evaluer(personleser.amerikanskUtenMedlemskapEndretArbeidsforholdSammeArbeidsgiver()), Svar.UAVKLART)
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
    fun `amerikansk med overlappende medlemskapsperioder`() {
        assertSvar("1.3", Svar.JA, evaluer(personleser.amerikanskMedMedlemskapToOverlappendePerioder()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk med hull i medlemskapsperiode`() {
        assertSvar("1.3", Svar.NEI, evaluer(personleser.amerikanskMedMedlemskapToUsammenhengendePerioder()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk med medlemskap avsluttet i input periode`() {
        assertSvar("1.3", Svar.NEI, evaluer(personleser.amerikanskMedMedlemskapAvsluttetIGittInputPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk med to sammenhengende medlemskapsperioder`() {
        assertSvar("1.3", Svar.JA, evaluer(personleser.amerikanskMedMedlemskapToSammenhengendePerioder()), Svar.UAVKLART)
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
        val amerikanskMedMedlemskapMedDekningIMedl = evaluer(personleser.amerikanskMedMedlemskapMedDekningIMedl())
        assertSvar("1.6", Svar.JA, amerikanskMedMedlemskapMedDekningIMedl, Svar.JA)
        Assertions.assertEquals(Svar.JA, amerikanskMedMedlemskapMedDekningIMedl.harDekning!!)
    }

    @Test
    fun `amerikansk person med medlemskap uten dekning i medl`() {
        val amerikanskMedMedlemskapUtenDekningIMedl = evaluer(personleser.amerikanskMedMedlemskapUtenDekningIMedl())
        assertSvar("1.6", Svar.NEI, amerikanskMedMedlemskapUtenDekningIMedl, Svar.JA)
        Assertions.assertEquals(Svar.NEI, amerikanskMedMedlemskapUtenDekningIMedl.harDekning!!)
    }
}