package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.assertSvar
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.evaluer
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Test

class RegelsettForArbeidsforholdTest {

        private val personleser = Personleser()


        @Test
        fun `person med overlappende arbeidsforhold innenfor kontrollperiode, får ja`() {
            assertSvar("3", Svar.JA, evaluer(personleser.norskMedOverlappendeArbeidsforholdIPeriode()), Svar.JA)
        }

        @Test
        fun `person med ett arbeidsforhold under 12 mnd innenfor kontrollperiode, får uavklart`() {
            assertSvar("3", Svar.NEI, evaluer(personleser.norskMedEttArbeidsforholdUnder12MndIPeriode()), Svar.UAVKLART)
        }

        @Test
        fun `person med to sammenhengende arbeidsforhold under 12 mnd innenfor kontrollperiode, får uavklart`() {
            assertSvar("3", Svar.NEI, evaluer(personleser.norskMedToSammenhengendeArbeidsforholdUnder12MndIPeriode()), Svar.UAVKLART)
        }

        @Test
        fun `person med to usammenhengende arbeidsforhold innenfor kontrollperiode, får uavklart`() {
            assertSvar("3", Svar.NEI, evaluer(personleser.norskMedToUsammenhengendeArbeidsforholdIPeriode()), Svar.UAVKLART)
        }

        @Test
        fun `person med over 10 arbeidsforhold innenfor kontrollperiode, får uavklart`() {
            assertSvar("3", Svar.NEI, evaluer(personleser.norskMedOverTiArbeidsforhold()), Svar.UAVKLART)
        }

        @Test
        fun `person med ett arbeidsforhold med arbeidsgiver som privatperson innenfor kontrollperiode, får uavklart`() {
            assertSvar("4", Svar.NEI, evaluer(personleser.norskMedEttArbeidsforholdMedPrivatpersonSomArbeidsgiverIPeriode()), Svar.UAVKLART)
        }

        @Test
        fun `person med flere arbeidsforhold hvorav en arbeidsgiver har færre enn 6 ansatte innenfor kontrollperiode, får uavklart`() {
            assertSvar("5", Svar.NEI, evaluer(personleser.norskMedFlereArbeidsforholdHvoravEnArbeidsgiverMedKun4AnsatteIPeriode()), Svar.UAVKLART)
        }

        @Test
        fun `person med ett arbeidsforhold med arbeidsgiver har konkurs status får uavklart`() {
            assertSvar("6", Svar.NEI, evaluer(personleser.enkelNorskMedKonkursArbeidsgiver()), Svar.UAVKLART)
        }

        @Test
        fun `person med flere arbeidsforhold hvorav ett er maritimt, får uavklart`() {
            assertSvar("7", Svar.NEI, evaluer(personleser.norskMedFlereArbeidsforholdstyperIPerioder()), Svar.UAVKLART)
        }

        @Test
        fun `person med en norsk arbeidsgiver og kun arbeid i Norge, maritimt på norsk skip, får ja`() {
            assertSvar("7", Svar.JA, evaluer(personleser.enkelNorskMaritim()), Svar.JA)
        }

        @Test
        fun `person med norsk arbeidsgiver på utenlandsk skip, får uavklart`() {
            assertSvar("7.1", Svar.NEI, evaluer(personleser.brukerArbeiderPaaNISSkip()), Svar.UAVKLART)
        }

         @Test
         fun `person med norsk arbeidsgiver på NOR skip, får JA`() {
        assertSvar("7.1", Svar.JA, evaluer(personleser.brukerArbeiderPaaNORSkip()), Svar.JA)
         }

        @Test
        fun `person med en norsk arbeidsgiver og kun arbeid i Norge, ikke maritim eller pilot, får ja`() {
            assertSvar("8", Svar.NEI, evaluer(personleser.brukerErIkkePilotEllerKabinansatt()), Svar.JA)
        }

        @Test
        fun `person med flere arbeidsforhold, hvor en har yrkeskode pilot`() {
            assertSvar("8", Svar.JA, evaluer(personleser.norskMedFlereYrkeskoderIPeriode()), Svar.UAVKLART)
        }

        @Test
        fun `person med en norsk arbeidsgiver, pilot, får uavklart`() {
            assertSvar("8", Svar.JA, evaluer(personleser.brukerErPilot()), Svar.UAVKLART)
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