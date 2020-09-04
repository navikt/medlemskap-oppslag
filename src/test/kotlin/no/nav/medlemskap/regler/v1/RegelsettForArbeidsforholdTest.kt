package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.assertSvar
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.evaluer
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Test

class RegelsettForArbeidsforholdTest {

    private val personleser = Personleser()

    @Test
    fun `person med ett arbeidsforhold med arbeidsgiver som privatperson innenfor kontrollperiode, får uavklart`() {
        assertSvar(REGEL_4, Svar.NEI, evaluer(personleser.norskMedEttArbeidsforholdMedPrivatpersonSomArbeidsgiverIPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `person med flere arbeidsforhold hvorav en arbeidsgiver har færre enn 6 ansatte innenfor kontrollperiode, får uavklart`() {
        assertSvar(REGEL_5, Svar.NEI, evaluer(personleser.norskMedFlereArbeidsforholdHvoravEnArbeidsgiverMedKun4AnsatteIPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `person med ett arbeidsforhold med arbeidsgiver har konkurs status får uavklart`() {
        assertSvar(REGEL_6, Svar.NEI, evaluer(personleser.enkelNorskMedKonkursArbeidsgiver()), Svar.UAVKLART)
    }

    @Test
    fun `person med flere arbeidsforhold hvorav ett er maritimt, får uavklart`() {
        assertSvar(REGEL_7, Svar.NEI, evaluer(personleser.norskMedFlereArbeidsforholdstyperIPerioder()), Svar.UAVKLART)
    }

    @Test
    fun `person med en norsk arbeidsgiver og kun arbeid i Norge, maritimt på norsk skip, får ja`() {
        assertSvar(REGEL_7, Svar.JA, evaluer(personleser.enkelNorskMaritim()), Svar.JA)
    }

    @Test
    fun `person med norsk arbeidsgiver på utenlandsk skip, får uavklart`() {
        assertSvar(REGEL_7_1, Svar.NEI, evaluer(personleser.brukerArbeiderPaaNISSkip()), Svar.UAVKLART)
    }

    @Test
    fun `person med norsk arbeidsgiver på NOR skip, får JA`() {
        assertSvar(REGEL_7_1, Svar.JA, evaluer(personleser.brukerArbeiderPaaNORSkip()), Svar.JA)
    }

    @Test
    fun `person med en norsk arbeidsgiver og kun arbeid i Norge, ikke maritim eller pilot, får ja`() {
        assertSvar(REGEL_8, Svar.NEI, evaluer(personleser.brukerErIkkePilotEllerKabinansatt()), Svar.JA)
    }

    @Test
    fun `person med flere arbeidsforhold, hvor en har yrkeskode pilot`() {
        assertSvar(REGEL_8, Svar.JA, evaluer(personleser.norskMedFlereYrkeskoderIPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `person med en norsk arbeidsgiver, pilot, får uavklart`() {
        assertSvar(REGEL_8, Svar.JA, evaluer(personleser.brukerErPilot()), Svar.UAVKLART)
    }

        /* Tester som blir aktuelle igjen når det skal sjekkes på arbeidsforhold i opptjeningsperiode. Regelen er for tiden kommentert ut.
        @Test
        fun `person med ett arbeidsforhold innenfor kontrollperiode, får ja`() {
            assertEquals(Svar.JA, evaluer(personleser.norskMedEttArbeidsforholdIPeriode()))
        }
        @Test
        fun `person med flere arbeidsforhold innenfor kontrollperiode, får uavklart`() {
            assertEquals(Svar.UAVKLART, evaluer(personleser.enkelNorskFlereArbeidsforholdIPeriode()))
        }
        */
}
