package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Personfakta.Companion.initialiserFakta
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RegelsettForNorskLovvalgTest {

    private val personleser = Personleser()



    @Test
    fun `person med ett arbeidsforhold innenfor kontrollperiode, får ja`() {
        assertEquals(Svar.JA, evaluer(personleser.norskMedEttArbeidsforholdIPeriode()))
    }

    @Test
    fun `person med flere arbeidsforhold innenfor kontrollperiode, får uavklart`() {
        assertEquals(Svar.UAVKLART, evaluer(personleser.enkelNorskFlereArbeidsforholdIPeriode()))
    }

    @Test
    fun `person med ett arbeidsforhold med arbeidsgiver som privatperson innenfor kontrollperiode, får uavklart`() {
        assertEquals(Svar.UAVKLART, evaluer(personleser.norskMedEttArbeidsforholdMedPrivatpersonSomArbeidsgiverIPeriode()))
    }

    @Test
    fun  `person med ett arbeidsforhold med arbeidsgiver har konkurs status får uavklart`() {
        assertEquals(Svar.UAVKLART, evaluer(personleser.enkelNorskMedKonkursArbeidsgiver()))
    }

    @Test
    fun `person med flere arbeidsforhold hvorav en arbeidsgiver har færre enn 6 ansatte innenfor kontrollperiode, får uavklart`() {
        assertEquals(Svar.UAVKLART, evaluer(personleser.norskMedFlereArbeidsforholdHvoravEnArbeidsgiverMedKun4AnsatteIPeriode()))
    }

    @Test
    fun `person med to sammenhengende arbeidsforhold innenfor kontrollperiode, får ja`() {
        assertEquals(Svar.JA, evaluer(personleser.norskMedToSammenhengendeArbeidsforholdIPeriode()))
    }

    @Test
    fun `person med to sammenhengende arbeidsforhold under 12 mnd innenfor kontrollperiode, får uavklart`() {
        assertEquals(Svar.UAVKLART, evaluer(personleser.norskMedToSammenhengendeArbeidsforholdUnder12MndIPeriode()))
    }

    @Test
    fun `person med to usammenhengende arbeidsforhold innenfor kontrollperiode, får uavklart`() {
        assertEquals(Svar.UAVKLART, evaluer(personleser.norskMedToUsammenhengendeArbeidsforholdIPeriode()))
    }

    @Test
    fun `person med overlappende arbeidsforhold innenfor kontrollperiode, får ja`() {
        assertEquals(Svar.JA, evaluer(personleser.norskMedOverlappendeArbeidsforholdIPeriode()))
    }

    @Test
    fun `person med ett arbeidsforhold som har en arbeidsavtale med mindre enn 25% stillingsprosent, får uavklart`() {
        assertEquals(Svar.UAVKLART, evaluer(personleser.norskMedEttArbeidsforholdMedArbeidsavtaleUnder25ProsentStillingIPeriode()))
    }

    @Test
    fun `person med ett arbeidsforhold med to arbeidsavtaler som utgjør mindre enn 25% i stillingsprosent, får uavklart`() {
        assertEquals(Svar.UAVKLART, evaluer(personleser.norskMedToArbeidsavtalerISammeArbeidsforholdMedForLavTotalStillingProsentIPeriode()))
    }

    @Test
    fun `person med ett arbeidsforhold med to overlappende arbeidsavtaler som til sammen utgjør mer enn 25% i stillingsprosent, får ja`() {
        assertEquals(Svar.JA, evaluer(personleser.norskMedToOverlappendeArbeidsavtalerSomTilSammenErOver25ProsentIPeriode()))
    }

    @Test
    fun `person med en norsk arbeidsgiver og kun arbeid i Norge, ikke maritim eller pilot, får ja`() {
        assertEquals(Svar.JA, evaluer(personleser.enkelNorskArbeid()))
    }

    @Test
    fun `person med en norsk arbeidsgiver og kun arbeid i Norge, maritimt på norsk skip, får ja`() {
        assertEquals(Svar.JA, evaluer(personleser.enkelNorskMaritim()))
    }

    @Test
    fun `person med en norsk arbeidsgiver, pilot, får uavklart`() {
        assertEquals(Svar.UAVKLART, evaluer(personleser.enkelNorskPilot()))
    }

    @Test
    fun `person med norsk arbeidsgiver på utenlandsk skip, får uavklart`() {
        assertEquals(Svar.UAVKLART, evaluer(personleser.enkelNorskUtenlandskSkip()))
    }

    @Test
    fun `person med flere arbeidsgivere, hvor en er norsk får nei i periode`() {
        assertEquals(Svar.UAVKLART, evaluer(personleser.norskArbeidsgiverMedFlereIPeriode()))
    }

    @Test
    fun `person med flere arbeidsforhold, hvor en maritimt med nis som skipsregister`() {
        val data = personleser.norskMedFlereArbeidsforholdstyperIPerioder()
        val evaluering = evaluer(data)
        assertEquals(Svar.UAVKLART, evaluering)
    }

    @Test
    fun `person med flere arbeidsforhold, hvor en har yrkeskode pilot`() {
        assertEquals(Svar.UAVKLART, evaluer(personleser.norskMedFlereYrkeskoderIPeriode()))
    }

    private fun evaluer(datagrunnlag: Datagrunnlag): Svar {
        val regelsett = ReglerForArbeidsforhold(initialiserFakta(datagrunnlag))
        return regelsett.hentHovedRegel().utfør(mutableListOf()).svar
    }

}
