package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Personfakta.Companion.initialiserFakta
import no.nav.medlemskap.regler.personer.Personleser
import no.nav.medlemskap.regler.v2.common.Svar
import no.nav.medlemskap.regler.v2.regler.ReglerForArbeidsforhold
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RegelsettForNorskLovvalgTest {

    private val personleser = Personleser()

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
