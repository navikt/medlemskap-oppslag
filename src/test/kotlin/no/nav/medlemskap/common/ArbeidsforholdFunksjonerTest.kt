package no.nav.medlemskap.common

import junit.framework.Assert.assertEquals
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.filtrerUtArbeidsgivereMedFærreEnn6Ansatte
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ArbeidsforholdFunksjonerTest {

    @Test
    fun `Filtrerer ut arbeidsgiver med flere enn 6 ansatte der arbeidsgiver med flest ansatte ligger sist`() {
        val arbeidsforhold = listOf(arbeidsforholdMedMindreEnn6Ansatte, arbeidsforholdMedFlerEnn6Ansatte)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2016, 1, 1),
            tom = LocalDate.of(2016, 12, 31)
        )
        val filtrertArbeidsforhold = arbeidsforhold.filtrerUtArbeidsgivereMedFærreEnn6Ansatte(kontrollperiode)
        assertEquals(2, arbeidsforhold.size)
        assertEquals(1, filtrertArbeidsforhold.size)
    }

    @Test
    fun `Filtrerer ut arbeidsgiver med flere enn 6 ansatte der arbeidsgiver med flest ansatte ligger først`() {
        val arbeidsforhold = listOf(arbeidsforholdMedFlerEnn6Ansatte, arbeidsforholdMedMindreEnn6Ansatte)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2016, 1, 1),
            tom = LocalDate.of(2016, 12, 31)
        )
        val filtrertArbeidsforhold = arbeidsforhold.filtrerUtArbeidsgivereMedFærreEnn6Ansatte(kontrollperiode)
        assertEquals(2, arbeidsforhold.size)
        assertEquals(1, filtrertArbeidsforhold.size)
    }

    @Test
    fun `Sjekker grenseverdien`() {
        val arbeidsforhold = listOf(arbeidsforholdMedAkkurat6Ansatte, arbeidsforholdMed5Ansatte)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2016, 1, 1),
            tom = LocalDate.of(2016, 12, 31)
        )
        val filtrertArbeidsforhold = arbeidsforhold.filtrerUtArbeidsgivereMedFærreEnn6Ansatte(kontrollperiode)
        assertEquals(2, arbeidsforhold.size)
        assertEquals(1, filtrertArbeidsforhold.size)
    }

    val arbeidsforholdMedMindreEnn6Ansatte = lagArbeidsforhold(2)
    val arbeidsforholdMedFlerEnn6Ansatte = lagArbeidsforhold(10)
    val arbeidsforholdMedAkkurat6Ansatte = lagArbeidsforhold(6)
    val arbeidsforholdMed5Ansatte = lagArbeidsforhold(5)

    fun lagArbeidsforhold(antall: Int): Arbeidsforhold {
        return Arbeidsforhold(
            periode = Periode(
                fom = LocalDate.of(2016, 1, 1),
                tom = LocalDate.of(2017, 12, 31)
            ),
            utenlandsopphold = null,
            arbeidsgivertype = OpplysningspliktigArbeidsgiverType.Organisasjon,
            arbeidsgiver = Arbeidsgiver(
                type = null,
                identifikator = null,
                ansatte = listOf(Ansatte(antall = antall, bruksperiode = null, gyldighetsperiode = null)),
                konkursStatus = null,
                juridiskEnhetEnhetstypeMap = null
            ),
            arbeidsfolholdstype = Arbeidsforholdstype.NORMALT,
            arbeidsavtaler = listOf(
                Arbeidsavtale(
                    periode = Periode(fom = null, tom = null),
                    yrkeskode = "Yrkeskode",
                    skipsregister = null,
                    stillingsprosent = null
                )
            )
        )
    }
}
