package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.assertSvar
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.evaluer
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * MIDLERTIDIG KLASSE
 * Dette er en testklasse som er lagd for MEDL-endring uten
 * Opprettet en ny fil for tester relatert til MEDL-fordi
 */
class RegelsettForMedlTestMidlertidig {

    private val personleser = Personleser()

    @Test
    fun `amerikansk person med vedtak i medl får uavklart på manuelle vedtak`() {
        assertSvar(REGEL_OPPLYSNINGER, Svar.JA, evaluer(personleser.amerikanskMedl()), Svar.UAVKLART)
    }

    @Test
    fun `norsk person med opplysninger i medl`() {
        assertSvar(REGEL_OPPLYSNINGER, Svar.JA, evaluer(personleser.norskMedOpplysningerIMedl()), Svar.JA)
    }

    @Test
    fun `amerikansk person med vedtak i medl og gosys får uavklart på manuelle vedtak`() {
        assertSvar(REGEL_OPPLYSNINGER, Svar.UAVKLART, evaluer(personleser.amerikanskGosys()), Svar.UAVKLART)
    }


    @Test
    fun `amerikansk person med og uten medlemskap i 12 mnd perioden`() {
        assertSvar(REGEL_1_2, Svar.JA, evaluer(personleser.amerikanskMedOgUtenMedlemskap()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med periode med medlemskap`() {
        assertSvar(REGEL_1_3, Svar.JA, evaluer(personleser.brukerHarPeriodeMedMedlemskap()), Svar.UAVKLART)
    }

    @Test
    fun `norsk person med periode uten medlemskap`() {
        assertSvar(REGEL_1_3_1, Svar.JA, evaluer(personleser.norskUtenMedlemskapIMedl()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person uten medlemskap i hele 12 mnd perioden`() {
        assertSvar(REGEL_1_3_1, Svar.JA, evaluer(personleser.amerikanskUtenMedlemskapOver12MndPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `norsk person uten medlemskap delvis i 12 mnd perioden`() {
        assertSvar(REGEL_1_3_1, Svar.NEI, evaluer(personleser.norskUtenMedlemskapDelvisInnenfor12MndPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person uten medlemskap med endret arbeidsforhold`() {
        assertSvar(REGEL_1_3_2, Svar.NEI, evaluer(personleser.amerikanskUtenMedlemskapEndretArbeidsforhold()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person uten medlemskap med endret arbeidsforhold, men med samme arbeidsgiver`() {
        assertSvar(REGEL_1_3_2, Svar.JA, evaluer(personleser.amerikanskUtenMedlemskapEndretArbeidsforholdSammeArbeidsgiver()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person uten medlemskap uten arbeidsforhold`() {
        assertSvar(REGEL_1_3_2, Svar.NEI, evaluer(personleser.amerikanskUtenMedlemskapUtenArbeidsforhold()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med UAVK (uavklart) statuskode i lovvalg fra medl`() {
        assertSvar(REGEL_OPPLYSNINGER, Svar.JA, evaluer(personleser.amerikanskUtenMedlemskapLovvalgStatuskodeUavklart()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med medlemskap i hele 12 mnd perioden`() {
        assertSvar(REGEL_1_4, Svar.JA, evaluer(personleser.amerikanskMedMedlemskapOver12MndPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med medlemskapdato over 5 år`() {
        assertSvar(REGEL_1_4, Svar.NEI, evaluer(personleser.amerikanskMedMedlemskapUgyldigDato()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk med overlappende medlemskapsperioder`() {
        assertSvar(REGEL_1_4, Svar.JA, evaluer(personleser.amerikanskMedMedlemskapToOverlappendePerioder()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk med hull i medlemskapsperiode`() {
        assertSvar(REGEL_1_4, Svar.NEI, evaluer(personleser.amerikanskMedMedlemskapToUsammenhengendePerioder()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk med medlemskap avsluttet i input periode`() {
        assertSvar(REGEL_1_4, Svar.JA, evaluer(personleser.amerikanskMedMedlemskapAvsluttetIGittInputPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk med to sammenhengende medlemskapsperioder`() {
        assertSvar(REGEL_1_4, Svar.JA, evaluer(personleser.amerikanskMedMedlemskapToSammenhengendePerioder()), Svar.UAVKLART)
    }

    @Test
    fun `norsk person med medlemskap delvis i 12 mnd perioden`() {
        assertSvar(REGEL_1_4, Svar.NEI, evaluer(personleser.norskMedMedlemskapDelvisInnenfor12MndPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med medlemskap med endret arbeidsforhold`() {
        assertSvar(REGEL_1_5, Svar.NEI, evaluer(personleser.amerikanskMedMedlemskapEndretArbeidsforhold()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med medlemskap med uavklart dekning`() {
        assertSvar(REGEL_1_6, Svar.JA, evaluer(personleser.amerikanskMedMedlemskapUavklartDekning()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med medlemskap med dekning null`() {
        assertSvar(REGEL_1_6, Svar.JA, evaluer(personleser.amerikanskMedMedlemskapNullDekning()), Svar.UAVKLART)
    }

    @Test
    fun `amerikansk person med medlemskap med dekning i medl`() {
        val amerikanskMedMedlemskapMedDekningIMedl = evaluer(personleser.amerikanskMedMedlemskapMedDekningIMedl())
        assertSvar(REGEL_1_7, Svar.JA, amerikanskMedMedlemskapMedDekningIMedl, Svar.JA)
        Assertions.assertEquals(Svar.JA, amerikanskMedMedlemskapMedDekningIMedl.harDekning!!)
    }

    @Test
    fun `amerikansk person med medlemskap uten dekning i medl`() {
        val amerikanskMedMedlemskapUtenDekningIMedl = evaluer(personleser.amerikanskMedMedlemskapUtenDekningIMedl())
        assertSvar(REGEL_1_7, Svar.NEI, amerikanskMedMedlemskapUtenDekningIMedl, Svar.UAVKLART)
        Assertions.assertEquals(Svar.NEI, amerikanskMedMedlemskapUtenDekningIMedl.harDekning!!)
    }
}