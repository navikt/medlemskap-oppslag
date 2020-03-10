package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Personfakta.Companion.initialiserFakta
import no.nav.medlemskap.regler.common.Resultattype
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RegelsettForNorskLovvalgTest {

    private val personleser = Personleser()

    @Test
    fun `person med en norsk arbeidsgiver og kun arbeid i Norge, ikke maritim eller pilot, får ja`() {
        assertEquals(Resultattype.JA, evaluer(personleser.enkelNorskArbeid()))
    }

    @Test
    fun `person med en norsk arbeidsgiver og kun arbeid i Norge, maritimt på norsk skip, får ja`() {
        assertEquals(Resultattype.JA, evaluer(personleser.enkelNorskMaritim()))
    }

    @Test
    fun `person med en norsk arbeidsgiver, pilot, får uavklart`() {
        assertEquals(Resultattype.UAVKLART, evaluer(personleser.enkelNorskPilot()))
    }

    @Test
    fun `person med norsk arbeidsgiver på utenlandsk skip, får uavklart`() {
        assertEquals(Resultattype.UAVKLART, evaluer(personleser.enkelNorskUtenlandskSkip()))
    }

    @Test
    fun `person med flere arbeidsgivere, hvor en er norsk får uavklart i periode`() {
        assertEquals(Resultattype.UAVKLART, evaluer(personleser.norskArbeidsgiverMedFlereIPeriode()))
    }

    @Test
    fun `person med for liten stillingsprosent, får uavklart`() {
        assertEquals(Resultattype.UAVKLART, evaluer(personleser.enkelNorskMedForLiteStillingsporsent()))
    }

    @Test
    fun `person med flere arbeidsforhold, hvor en maritimt med nis som skipsregister`() {
        val data = personleser.norskMedFlereArbeidsforholdstyperIPerioder()
        val evaluering = evaluer(data)
        assertEquals(Resultattype.UAVKLART, evaluering)
    }

    @Test
    fun `person med flere arbeidsforhold, hvor en har yrkeskode pilot`() {
        assertEquals(Resultattype.UAVKLART, evaluer(personleser.norskMedFlereYrkeskoderIPeriode()))
    }
    private fun evaluer(datagrunnlag: Datagrunnlag): Resultattype {
        val regelsett = RegelsettForNorskLovvalg()
        return regelsett.evaluer(initialiserFakta(datagrunnlag)).resultat
    }

}
