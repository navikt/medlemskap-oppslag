package no.nav.medlemskap.regler.funksjoner

import junit.framework.Assert.*
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.filtrerUtArbeidsgivereMedFærreEnn6Ansatte
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.harMinst25stillingsprosent
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ArbeidsforholdFunksjonerTest {

    @Test
    fun `Filtrerer ut arbeidsgiver med flere enn 6 ansatte der arbeidsgiver med flest ansatte ligger sist`() {
        val arbeidsforhold = listOf(arbeidsforholdMedMindreEnn6Ansatte, arbeidsforholdMedFlerEnn6Ansatte)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        val filtrertArbeidsforhold = arbeidsforhold.filtrerUtArbeidsgivereMedFærreEnn6Ansatte(kontrollperiode)
        assertEquals(2, arbeidsforhold.size)
        assertEquals(1, filtrertArbeidsforhold.size)
    }

    @Test
    fun `Filtrerer ut arbeidsgiver med flere enn 6 ansatte der arbeidsgiver med flest ansatte ligger først`() {
        val arbeidsforhold = listOf(arbeidsforholdMedFlerEnn6Ansatte, arbeidsforholdMedMindreEnn6Ansatte)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        val filtrertArbeidsforhold = arbeidsforhold.filtrerUtArbeidsgivereMedFærreEnn6Ansatte(kontrollperiode)
        assertEquals(2, arbeidsforhold.size)
        assertEquals(1, filtrertArbeidsforhold.size)
    }

    @Test
    fun `Sjekker grenseverdien`() {
        val arbeidsforhold = listOf(arbeidsforholdMedAkkurat6Ansatte, arbeidsforholdMed5Ansatte)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        val filtrertArbeidsforhold = arbeidsforhold.filtrerUtArbeidsgivereMedFærreEnn6Ansatte(kontrollperiode)
        assertEquals(2, arbeidsforhold.size)
        assertEquals(1, filtrertArbeidsforhold.size)
    }

    @Test
    fun `Bruker med 100 stillingsprosent blir true`() {
        val arbeidsforhold = listOf(arbeidsforholdMed100Stillingsprosent)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2018, 12, 31),
            tom = LocalDate.of(2020, 1, 1)
        )
        val sjekkerStillingsprosent = arbeidsforhold.harMinst25stillingsprosent(kontrollperiode, Ytelse.SYKEPENGER)
        assertTrue(sjekkerStillingsprosent)
    }

    @Test
    fun `Flere arbeidsavtaler med 25% stillingsprosent eller mer blir true`() {
        val arbeidsforholdMedFlereArbeidsavtaler =
            listOf(arbeidsforholdMed100Stillingsprosent, arbeidsforholdMed25stillingsprosent)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2020, 1, 1)
        )
        val sjekkerStillingsprosent = arbeidsforholdMedFlereArbeidsavtaler.harMinst25stillingsprosent(kontrollperiode, Ytelse.SYKEPENGER)
        assertTrue(sjekkerStillingsprosent)
    }

    @Test
    fun `Arbeidsavtale under 25% blir false`() {
        val arbeidsforhold = listOf(arbeidsforholdMed25stillingsprosent)
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2020, 1, 1)
        )
        val sjekkStillingsprosent = arbeidsforhold.harMinst25stillingsprosent(kontrollperiode, Ytelse.SYKEPENGER)
        assertFalse(sjekkStillingsprosent)
    }

    val arbeidsforholdMedMindreEnn6Ansatte = lagArbeidsforhold(2)
    val arbeidsforholdMedFlerEnn6Ansatte = lagArbeidsforhold(10)
    val arbeidsforholdMedAkkurat6Ansatte = lagArbeidsforhold(6)
    val arbeidsforholdMed5Ansatte = lagArbeidsforhold(5)

    val arbeidsavtaleMed100Stillingsprosent = lagArbeidsavtale(
        Periode(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 6, 1)),
        "Yrkeskode", null, 100.0
    )

    val arbeidsavtaleMed25Stillingsprosent = lagArbeidsavtale(
        Periode(LocalDate.of(2019, 6, 2), LocalDate.of(2019, 12, 31)),
        "Yrkeskode", null, 25.0
    )

    val arbeidsavtaleMed10Stillingsprosent = lagArbeidsavtale(
        Periode(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 31)),
        "Yrkeskode", null, 10.0
    )

    val arbeidsforholdMed100Stillingsprosent = lagArbeidsforhold(
        10,
        Periode(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 6, 1)),
        listOf(arbeidsavtaleMed100Stillingsprosent)
    )

    val arbeidsforholdMed25stillingsprosent = lagArbeidsforhold(
        10,
        Periode(LocalDate.of(2019, 6, 2), LocalDate.of(2019, 12, 31)),
        listOf(arbeidsavtaleMed25Stillingsprosent)
    )

    fun lagArbeidsforhold(
        antall: Int = 10,
        periode: Periode =
            Periode(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 31)),
        arbeidsavtale: List<Arbeidsavtale> =
            listOf(lagArbeidsavtale(Periode(null, null), "Yrkeskode", null, null))
    ): Arbeidsforhold {
        return Arbeidsforhold(
            periode = periode,
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
            arbeidsavtaler = arbeidsavtale
        )
    }

    fun lagArbeidsavtale(periode: Periode, yrkesKode: String, skipsregister: Skipsregister?, stillingsprosent: Double?): Arbeidsavtale {
        return Arbeidsavtale(
            periode = periode,
            yrkeskode = yrkesKode,
            skipsregister = skipsregister,
            stillingsprosent = stillingsprosent
        )
    }
}
