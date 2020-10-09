package no.nav.medlemskap.regler.funksjoner

import io.mockk.every
import io.mockk.mockk
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.beregnGjennomsnittligStillingsprosentForGrafana
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.filtrerUtArbeidsgivereMedFærreEnn6Ansatte
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.vektetStillingsprosentForArbeidsforhold
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ArbeidsforholdFunksjonerTest {

    private val kontrollperiodeFra2019Til2020 = Kontrollperiode(
        fom = LocalDate.of(2019, 1, 1),
        tom = LocalDate.of(2020, 1, 1)
    )

    @Test
    fun `Filtrerer ut arbeidsgiver med flere enn 6 ansatte der arbeidsgiver med flest ansatte ligger sist`() {
        val arbeidsforhold = listOf(arbeidsforholdMedMindreEnn6Ansatte, arbeidsforholdMedFlerEnn6Ansatte)
        val filtrertArbeidsforhold = arbeidsforhold.filtrerUtArbeidsgivereMedFærreEnn6Ansatte(kontrollperiodeFra2019Til2020)
        assertEquals(2, arbeidsforhold.size)
        assertEquals(1, filtrertArbeidsforhold.size)
    }

    @Test
    fun `Filtrerer ut arbeidsgiver med flere enn 6 ansatte der arbeidsgiver med flest ansatte ligger først`() {
        val arbeidsforhold = listOf(arbeidsforholdMedFlerEnn6Ansatte, arbeidsforholdMedMindreEnn6Ansatte)
        val filtrertArbeidsforhold = arbeidsforhold.filtrerUtArbeidsgivereMedFærreEnn6Ansatte(kontrollperiodeFra2019Til2020)
        assertEquals(2, arbeidsforhold.size)
        assertEquals(1, filtrertArbeidsforhold.size)
    }

    @Test
    fun `Sjekker grenseverdien`() {
        val arbeidsforhold = listOf(arbeidsforholdMedAkkurat6Ansatte, arbeidsforholdMed5Ansatte)
        val filtrertArbeidsforhold = arbeidsforhold.filtrerUtArbeidsgivereMedFærreEnn6Ansatte(kontrollperiodeFra2019Til2020)
        assertEquals(2, arbeidsforhold.size)
        assertEquals(1, filtrertArbeidsforhold.size)
    }

    @Test
    fun `Bruker med 100 i stillingsprosent gjennom halve kontrollperioden blir vektet til 50 prosent`() {

        val arbeidsforholdPeriode = Periode(
            fom = LocalDate.of(2019, 7, 2),
            tom = kontrollperiodeFra2019Til2020.tom
        )

        val arbeidsforhold = listOf(createArbeidsforholdMock(arbeidsforholdPeriode))
        assertEquals(50.1, arbeidsforhold.beregnGjennomsnittligStillingsprosentForGrafana(kontrollperiodeFra2019Til2020))
    }

    @Test
    fun `Beregn vektet stillingsprosent for arbeidsforhold med arbeidsavtale med lik periode`() {

        val arbeidsforholdPeriode = kontrollperiodeFra2019Til2020.periode
        val arbeidsforholdMock = createArbeidsforholdMock(arbeidsforholdPeriode, 25.0)
        val arbeidsforhold = listOf(arbeidsforholdMock)

        assertEquals(25.0, arbeidsforhold.first().vektetStillingsprosentForArbeidsforhold(kontrollperiodeFra2019Til2020))
    }

    @Test
    fun `Beregn vektet stillingsprosent for arbeidsforhold med arbeidsavtale i deler av periode`() {

        val arbeidsforholdPeriode = kontrollperiodeFra2019Til2020.periode

        val arbeidsavtalePeriode = Periode(
            fom = LocalDate.of(2019, 7, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        val arbeidsforholdMock = createArbeidsforholdMock(arbeidsforholdPeriode, arbeidsavtalePeriode, 25.0)
        val arbeidsforholdList = listOf(arbeidsforholdMock)

        assertEquals(12.5, arbeidsforholdList.first().vektetStillingsprosentForArbeidsforhold(kontrollperiodeFra2019Til2020))
    }

    @Test
    fun `Beregn vektet stillingsprosent for arbeidsforhold med to arbeidsavtaler i deler av arbeidsforholdperiode`() {

        val arbeidsforholdPeriode = kontrollperiodeFra2019Til2020.periode

        val arbeidsavtalePeriode = Periode(
            fom = LocalDate.of(2019, 7, 1),
            tom = LocalDate.of(2020, 1, 1)
        )

        val arbeidsforholdMock = mockk<Arbeidsforhold>()
        val arbeidsavtaleMock = mockk<Arbeidsavtale>()
        val arbeidsavtale2Mock = mockk<Arbeidsavtale>()

        every { arbeidsavtale2Mock.periode } returns arbeidsavtalePeriode
        every { arbeidsavtale2Mock.stillingsprosent } returns 25.0
        every { arbeidsavtaleMock.periode } returns arbeidsavtalePeriode
        every { arbeidsavtaleMock.stillingsprosent } returns 35.0
        every { arbeidsforholdMock.periode } returns arbeidsforholdPeriode
        every { arbeidsforholdMock.arbeidsavtaler } returns listOf(arbeidsavtaleMock, arbeidsavtale2Mock)

        val arbeidsforhold = listOf(arbeidsforholdMock)

        assertEquals(30.2, arbeidsforhold.first().vektetStillingsprosentForArbeidsforhold(kontrollperiodeFra2019Til2020))
        assertEquals(30.2, arbeidsforhold.beregnGjennomsnittligStillingsprosentForGrafana(kontrollperiodeFra2019Til2020))
    }

    @Test
    fun `Beregn vektet stillingsprosent for et arbeidsforhold som kun varer i først halvdel av kontrollperioden`() {
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2019, 7, 1),
            tom = LocalDate.of(2020, 7, 1)
        )
        val arbeidsforholdPeriode = Periode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 12, 31)
        )

        val arbeidsforholdMock = createArbeidsforholdMock(arbeidsforholdPeriode, 25.0)
        val arbeidsforhold = listOf(arbeidsforholdMock)

        assertEquals(25.0, arbeidsforhold.first().vektetStillingsprosentForArbeidsforhold(kontrollperiode))
        assertEquals(12.5, arbeidsforhold.beregnGjennomsnittligStillingsprosentForGrafana(kontrollperiode))
    }

    @Test
    fun `Beregn vektet stillingsprosent for et arbeidsforhold som kun varer i siste halvdel av kontrollperioden`() {
        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2018, 12, 31),
            tom = LocalDate.of(2019, 12, 31)
        )
        val arbeidsforholdPeriode = Periode(
            fom = LocalDate.of(2019, 7, 1),
            tom = LocalDate.of(2020, 7, 1)
        )

        val arbeidsforholdMock = createArbeidsforholdMock(arbeidsforholdPeriode, 25.0)
        val arbeidsforholdList = listOf(arbeidsforholdMock)

        assertEquals(25.0, arbeidsforholdList.first().vektetStillingsprosentForArbeidsforhold(kontrollperiode))
        assertEquals(12.5, arbeidsforholdList.beregnGjennomsnittligStillingsprosentForGrafana(kontrollperiode))
    }

    @Test
    fun `Beregn vektet stillingsprosent for to parallelle arbeidsforhold`() {

        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2018, 12, 31),
            tom = LocalDate.of(2019, 12, 31)
        )
        val arbeidsforholdPeriode = Periode(
            fom = LocalDate.of(2018, 7, 1),
            tom = null
        )

        val arbeidsforholdMock = createArbeidsforholdMock(arbeidsforholdPeriode, 20.0)
        val arbeidsforhold2Mock = createArbeidsforholdMock(arbeidsforholdPeriode, 20.0)
        val arbeidsforholdList = listOf(arbeidsforholdMock, arbeidsforhold2Mock)

        assertEquals(40.0, arbeidsforholdList.beregnGjennomsnittligStillingsprosentForGrafana(kontrollperiode))
        assertEquals(20.0, arbeidsforholdList[0].vektetStillingsprosentForArbeidsforhold(kontrollperiode))
        assertEquals(20.0, arbeidsforholdList[1].vektetStillingsprosentForArbeidsforhold(kontrollperiode))
        assertTrue(arbeidsforholdList.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(25.0, kontrollperiode, Ytelse.SYKEPENGER))
    }

    @Test
    fun `Beregn vektet stillingsprosent for to delvis parallelle arbeidsforhold`() {

        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2018, 12, 31),
            tom = LocalDate.of(2019, 12, 31)
        )
        val arbeidsforholdPeriode = Periode(
            fom = LocalDate.of(2018, 7, 1),
            tom = null
        )
        val arbeidsforhold2Periode = Periode(
            fom = LocalDate.of(2019, 7, 1),
            tom = null
        )

        val arbeidsforholdMock = createArbeidsforholdMock(arbeidsforholdPeriode, 20.0)
        val arbeidsforhold2Mock = createArbeidsforholdMock(arbeidsforhold2Periode, 20.0)
        val arbeidsforhold = listOf(arbeidsforholdMock, arbeidsforhold2Mock)

        assertFalse(arbeidsforhold.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(25.0, kontrollperiode, Ytelse.SYKEPENGER))
    }

    private fun createArbeidsforholdMock(arbeidsforholdPeriode: Periode, stillingsprosent: Double? = 100.0): Arbeidsforhold {

        val arbeidsavtaleMock = mockk<Arbeidsavtale>()
        every { arbeidsavtaleMock.periode } returns arbeidsforholdPeriode
        every { arbeidsavtaleMock.stillingsprosent } returns stillingsprosent

        val arbeidsforholdMock = mockk<Arbeidsforhold>()
        every { arbeidsforholdMock.periode } returns arbeidsforholdPeriode
        every { arbeidsforholdMock.arbeidsavtaler } returns listOf(arbeidsavtaleMock)

        return arbeidsforholdMock
    }

    private fun createArbeidsforholdMock(arbeidsforholdPeriode: Periode, arbeidsavtalePeriode: Periode, stillingsprosent: Double? = 100.0): Arbeidsforhold {
        val arbeidsavtaleMock = mockk<Arbeidsavtale>()
        every { arbeidsavtaleMock.periode } returns arbeidsavtalePeriode
        every { arbeidsavtaleMock.stillingsprosent } returns stillingsprosent

        val arbeidsforholdMock = mockk<Arbeidsforhold>()
        every { arbeidsforholdMock.periode } returns arbeidsforholdPeriode
        every { arbeidsforholdMock.arbeidsavtaler } returns listOf(arbeidsavtaleMock)

        return arbeidsforholdMock
    }

    private val arbeidsforholdMedMindreEnn6Ansatte = lagArbeidsforhold(2)
    private val arbeidsforholdMedFlerEnn6Ansatte = lagArbeidsforhold(10)
    private val arbeidsforholdMedAkkurat6Ansatte = lagArbeidsforhold(6)
    private val arbeidsforholdMed5Ansatte = lagArbeidsforhold(5)

    private fun lagArbeidsforhold(
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
                organisasjonsnummer = null,
                ansatte = listOf(Ansatte(antall = antall, bruksperiode = null, gyldighetsperiode = null)),
                konkursStatus = null,
                juridiskEnhetEnhetstypeMap = null
            ),
            arbeidsforholdstype = Arbeidsforholdstype.NORMALT,
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
