package no.nav.medlemskap.regler.funksjoner

import assertk.assertThat
import assertk.assertions.isEqualTo
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.arbeidsforhold.*
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.antallAnsatteHosArbeidsgiversJuridiskeEnheter
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.beregnGjennomsnittligStillingsprosentForGrafana
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.erArbeidsforholdetOffentligSektor
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

        val arbeidsforhold = listOf(createArbeidsforhold(arbeidsforholdPeriode))
        assertEquals(50.1, arbeidsforhold.beregnGjennomsnittligStillingsprosentForGrafana(kontrollperiodeFra2019Til2020))
    }

    @Test
    fun `Beregn vektet stillingsprosent for arbeidsforhold med arbeidsavtale med lik periode`() {

        val arbeidsforholdPeriode = kontrollperiodeFra2019Til2020.periode
        val arbeidsforhold = createArbeidsforhold(arbeidsforholdPeriode, 25.0)

        assertEquals(25.0, listOf(arbeidsforhold).first().vektetStillingsprosentForArbeidsforhold(kontrollperiodeFra2019Til2020))
    }

    @Test
    fun `Beregn vektet stillingsprosent for arbeidsforhold med arbeidsavtale i deler av periode`() {

        val arbeidsforholdPeriode = kontrollperiodeFra2019Til2020.periode

        val arbeidsavtalePeriode = Periode(
            fom = LocalDate.of(2019, 7, 1),
            tom = LocalDate.of(2019, 12, 31)
        )
        val arbeidsforhold = createArbeidsforhold(arbeidsforholdPeriode, 25.0, arbeidsavtalePeriode)

        assertEquals(12.5, listOf(arbeidsforhold).first().vektetStillingsprosentForArbeidsforhold(kontrollperiodeFra2019Til2020))
    }

    @Test
    fun `Beregn vektet stillingsprosent for arbeidsforhold med to arbeidsavtaler i deler av arbeidsforholdperiode`() {

        val arbeidsforholdPeriode = kontrollperiodeFra2019Til2020.periode

        val arbeidsavtalePeriode = Periode(
            fom = LocalDate.of(2019, 7, 1),
            tom = LocalDate.of(2020, 1, 1)
        )

        val arbeidsavtale = Arbeidsavtale(arbeidsavtalePeriode, arbeidsavtalePeriode, "1234", Skipsregister.NOR, Fartsomraade.INNENRIKS, 25.0, 9.0)
        val arbeidsavtale2 = Arbeidsavtale(arbeidsavtalePeriode, arbeidsavtalePeriode, "4321", null, null, 35.0, 9.0)

        val arbeidsforhold = createArbeidsforhold(arbeidsforholdPeriode)
        arbeidsforhold.arbeidsavtaler = listOf(arbeidsavtale, arbeidsavtale2)

        val arbeidsforholdList = listOf(arbeidsforhold)

        assertEquals(30.2, arbeidsforholdList.first().vektetStillingsprosentForArbeidsforhold(kontrollperiodeFra2019Til2020))
        assertEquals(30.2, arbeidsforholdList.beregnGjennomsnittligStillingsprosentForGrafana(kontrollperiodeFra2019Til2020))
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

        val arbeidsforholdMock = createArbeidsforhold(arbeidsforholdPeriode, 25.0)
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

        val arbeidsforholdMock = createArbeidsforhold(arbeidsforholdPeriode, 25.0)
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

        val arbeidsavtalePeriode = Periode(
            fom = LocalDate.of(2018, 7, 1),
            tom = LocalDate.of(2021, 7, 1)
        )

        val arbeidsforhold = createArbeidsforhold(arbeidsforholdPeriode, 20.0, arbeidsforholdPeriode)
        val arbeidsforhold2 = createArbeidsforhold(arbeidsforholdPeriode, 20.0, arbeidsavtalePeriode)
        val arbeidsforholdList = listOf(arbeidsforhold, arbeidsforhold2)

        assertEquals(40.0, arbeidsforholdList.beregnGjennomsnittligStillingsprosentForGrafana(kontrollperiode))
        assertEquals(20.0, arbeidsforholdList[0].vektetStillingsprosentForArbeidsforhold(kontrollperiode))
        assertEquals(20.0, arbeidsforholdList[1].vektetStillingsprosentForArbeidsforhold(kontrollperiode))
        assertTrue(arbeidsforholdList.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(25.0, kontrollperiode, Ytelse.SYKEPENGER))
    }

    @Test
    fun `Beregn vektet stillingsprosent med parallelt arbeid med 0% stilling`() {

        val kontrollperiode = Kontrollperiode(
            fom = LocalDate.of(2018, 12, 31),
            tom = LocalDate.of(2019, 12, 31)
        )
        val arbeidsforholdPeriode = Periode(
            fom = LocalDate.of(2018, 7, 1),
            tom = null
        )

        val arbeidsavtalePeriode = Periode(
            fom = LocalDate.of(2018, 7, 1),
            tom = LocalDate.of(2021, 7, 1)
        )

        val arbeidsforhold = createArbeidsforhold(arbeidsforholdPeriode, 100.0, arbeidsforholdPeriode)
        val arbeidsforhold2 = createArbeidsforhold(arbeidsforholdPeriode, 0.0, arbeidsavtalePeriode)
        val arbeidsforholdList = listOf(arbeidsforhold, arbeidsforhold2)

        assertEquals(100.0, arbeidsforholdList.beregnGjennomsnittligStillingsprosentForGrafana(kontrollperiode))
        assertEquals(100.0, arbeidsforholdList[0].vektetStillingsprosentForArbeidsforhold(kontrollperiode))
        assertEquals(0.0, arbeidsforholdList[1].vektetStillingsprosentForArbeidsforhold(kontrollperiode))
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

        val arbeidsforholdMock = createArbeidsforhold(arbeidsforholdPeriode, 20.0)
        val arbeidsforhold2Mock = createArbeidsforhold(arbeidsforhold2Periode, 20.0)
        val arbeidsforhold = listOf(arbeidsforholdMock, arbeidsforhold2Mock)

        assertFalse(arbeidsforhold.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(25.0, kontrollperiode, Ytelse.SYKEPENGER))
    }

    @Test
    fun `Arbeidsforhold med både statlig og privat enhetstype får true`() {
        val arbeidsforhold =
            listOf(arbeidsforholdMedStatligJuridiskEnhetstype, arbeidsforholdMedIkkeStatligEllerKommunalJuridiskEnhetstype)
        val sjekkJuridiskEnhet = erArbeidsforholdetOffentligSektor(arbeidsforhold, kontrollperiodeFra2019Til2020, Ytelse.SYKEPENGER)
        assertTrue(sjekkJuridiskEnhet)
    }

    @Test
    fun `Arbeidsforhold med juridisk enhetstype som null får false`() {
        val arbeidsforhold = listOf(arbeidsforholdMedFlerEnn6Ansatte)
        val sjekkJuridiskEnhet = erArbeidsforholdetOffentligSektor(arbeidsforhold, kontrollperiodeFra2019Til2020, Ytelse.SYKEPENGER)
        assertFalse(sjekkJuridiskEnhet)
    }

    @Test
    fun `Ikke statlig eller kommunalt arbeidsforhold får false`() {
        val arbeidsforhold = listOf(arbeidsforholdMedIkkeStatligEllerKommunalJuridiskEnhetstype)
        val sjekkJuridiskEnhet = erArbeidsforholdetOffentligSektor(arbeidsforhold, kontrollperiodeFra2019Til2020, Ytelse.SYKEPENGER)
        assertFalse(sjekkJuridiskEnhet)
    }

    @Test
    fun `Statlig arbeidsforhold får true`() {
        val arbeidsforhold = listOf(arbeidsforholdMedStatligJuridiskEnhetstype)
        val sjekkJuridiskEnhet = erArbeidsforholdetOffentligSektor(arbeidsforhold, kontrollperiodeFra2019Til2020, Ytelse.SYKEPENGER)
        assertTrue(sjekkJuridiskEnhet)
    }

    @Test
    fun `Statlig arbeidsforhold med mindre enn 25% stilling får false`() {

        val statligArbeidsforholdMedMindreEnn25Stillingsprosent = listOf(
            lagArbeidsforhold(
                arbeidsavtaleList = listOf(createArbeidsavtale(kontrollperiodeFra2019Til2020.periode, 10.0)),
                juridiskeEnheter = listOf(JuridiskEnhet("1", "STAT", 20))
            )
        )

        assertFalse(erArbeidsforholdetOffentligSektor(statligArbeidsforholdMedMindreEnn25Stillingsprosent, kontrollperiodeFra2019Til2020, Ytelse.SYKEPENGER))
    }

    @Test
    fun `Statlig arbeidsforhold med 25% stilling får true`() {

        val statligArbeidsforholdMed25Stillingsprosent = listOf(
            lagArbeidsforhold(
                arbeidsavtaleList = listOf(
                    Arbeidsavtale(
                        periode = kontrollperiodeFra2019Til2020.periode,
                        gyldighetsperiode = kontrollperiodeFra2019Til2020.periode,
                        yrkeskode = "Yrkeskode",
                        fartsomraade = null,
                        skipsregister = null,
                        stillingsprosent = 25.0,
                        beregnetAntallTimerPrUke = null
                    )
                ),
                juridiskeEnheter = listOf(JuridiskEnhet("1", "STAT", 20))
            )
        )

        val sjekkStatligArbeidsforhold = erArbeidsforholdetOffentligSektor(
            statligArbeidsforholdMed25Stillingsprosent, kontrollperiodeFra2019Til2020, Ytelse.SYKEPENGER
        )
        assertTrue(sjekkStatligArbeidsforhold)
    }

    @Test
    fun `antallAnsatteHosArbeidsgiversJuridiskeEnheter returnerer 5`() {

        val statligArbeidsforholdMed25Stillingsprosent = listOf(
            lagArbeidsforhold(
                arbeidsavtaleList = listOf(createArbeidsavtale(kontrollperiodeFra2019Til2020.periode)),
                juridiskeEnheter = listOf(JuridiskEnhet("1", "STAT", 20))
            )
        )

        val antallAnsatteHosArbeidsgiversJuridiskeEnheter = statligArbeidsforholdMed25Stillingsprosent.antallAnsatteHosArbeidsgiversJuridiskeEnheter(kontrollperiodeFra2019Til2020)
        assertEquals(20, antallAnsatteHosArbeidsgiversJuridiskeEnheter.first())
    }

    @Test
    fun `To statlige arbeidsforhold med 25% stilling etter hverandre får true`() {
        val periode1 = Periode(
            fom = LocalDate.of(2019, 6, 1),
            tom = LocalDate.of(2020, 1, 1)
        )

        val periode2 = Periode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 6, 1)
        )

        val statligArbeidsforholdMed25Stillingsprosent1 = lagArbeidsforhold(
            arbeidsavtaleList = listOf(createArbeidsavtale(periode1, 25.0)),
            juridiskeEnheter = listOf(JuridiskEnhet("1", "STAT", 20))
        )

        val statligArbeidsforholdMed25Stillingsprosent2 = lagArbeidsforhold(
            arbeidsavtaleList = listOf(createArbeidsavtale(periode2, 25.0)),
            juridiskeEnheter = listOf(JuridiskEnhet("1", "STAT", 20))
        )

        val arbeidsforhold = listOf(statligArbeidsforholdMed25Stillingsprosent1, statligArbeidsforholdMed25Stillingsprosent2)
        val sjekkStatligArbeidsforhold = erArbeidsforholdetOffentligSektor(arbeidsforhold, kontrollperiodeFra2019Til2020, Ytelse.SYKEPENGER)
        assertTrue(sjekkStatligArbeidsforhold)
    }

    @Test
    fun `To arbeidsforhold med ett med 25% stilling som går gjennom hele perioden får true`() {
        val periode2 = Periode(
            fom = LocalDate.of(2019, 1, 1),
            tom = LocalDate.of(2019, 6, 1)
        )

        val statligArbeidsforholdMed25Stillingsprosent1 = lagArbeidsforhold(
            arbeidsavtaleList = listOf(createArbeidsavtale(kontrollperiodeFra2019Til2020.periode, 25.0)),
            juridiskeEnheter = listOf(JuridiskEnhet("1", "STAT", 20))
        )

        val statligArbeidsforholdMed10Stillingsprosent2 = lagArbeidsforhold(
            arbeidsavtaleList = listOf(createArbeidsavtale(periode2, 10.0)),
            juridiskeEnheter = listOf(JuridiskEnhet("1", "AS", 20))
        )

        val arbeidsforhold = listOf(statligArbeidsforholdMed25Stillingsprosent1, statligArbeidsforholdMed10Stillingsprosent2)
        val sjekkStatligArbeidsforhold = erArbeidsforholdetOffentligSektor(arbeidsforhold, kontrollperiodeFra2019Til2020, Ytelse.SYKEPENGER)
        assertTrue(sjekkStatligArbeidsforhold)
    }

    @Test
    fun fraOgMedDatoForArbeidsforhold() {
        val førsteDatoForYtelse = LocalDate.of(2019, 1, 1)

        val fomArbeidsforhold = ArbeidsforholdFunksjoner.fraOgMedDatoForArbeidsforhold(førsteDatoForYtelse)
        assertThat(fomArbeidsforhold).isEqualTo(LocalDate.of(2017, 12, 31))
    }

    private fun createArbeidsforhold(arbeidsforholdPeriode: Periode, stillingsprosent: Double = 100.0, arbeidsavtalePeriode: Periode = arbeidsforholdPeriode): Arbeidsforhold {
        val arbeidsavtale = Arbeidsavtale(arbeidsavtalePeriode, arbeidsavtalePeriode, "11111", Skipsregister.NOR, Fartsomraade.INNENRIKS, stillingsprosent, null)

        val arbeidsforhold = Arbeidsforhold(
            arbeidsforholdPeriode, null, OpplysningspliktigArbeidsgiverType.Organisasjon,
            Arbeidsgiver(null, listOf(Ansatte(antall = 10, bruksperiode = null, gyldighetsperiode = null)), null, null),
            Arbeidsforholdstype.NORMALT,
            listOf(arbeidsavtale),
            emptyList()
        )

        return arbeidsforhold
    }

    private fun createArbeidsavtale(periode: Periode, stillingsprosent: Double = 100.0): Arbeidsavtale {
        return Arbeidsavtale(periode, periode, "11111", null, null, stillingsprosent, 37.5)
    }

    private val arbeidsforholdMedMindreEnn6Ansatte = lagArbeidsforhold(2)
    private val arbeidsforholdMedFlerEnn6Ansatte = lagArbeidsforhold(10)
    private val arbeidsforholdMedAkkurat6Ansatte = lagArbeidsforhold(6)
    private val arbeidsforholdMed5Ansatte = lagArbeidsforhold(5)

    val arbeidsforholdMedStatligJuridiskEnhetstype = lagArbeidsforhold(juridiskeEnheter = listOf(JuridiskEnhet("1", "STAT", 20)))
    val arbeidsforholdMedIkkeStatligEllerKommunalJuridiskEnhetstype = lagArbeidsforhold(
        juridiskeEnheter = listOf(
            JuridiskEnhet("1", "AS", 20)
        )
    )

    private fun lagArbeidsforhold(
        antall: Int = 10,
        periode: Periode = Periode(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 31)),
        arbeidsavtaleList: List<Arbeidsavtale> = listOf(createArbeidsavtale(Periode(null, null), 100.0)),
        juridiskeEnheter: List<JuridiskEnhet?>? = listOf(JuridiskEnhet("1", "AS", 20))
    ): Arbeidsforhold {
        return Arbeidsforhold(
            periode = periode,
            utenlandsopphold = null,
            arbeidsgivertype = OpplysningspliktigArbeidsgiverType.Organisasjon,
            arbeidsgiver = Arbeidsgiver(
                organisasjonsnummer = null,
                ansatte = listOf(Ansatte(antall = antall, bruksperiode = null, gyldighetsperiode = null)),
                konkursStatus = null,
                juridiskeEnheter = juridiskeEnheter
            ),
            arbeidsforholdstype = Arbeidsforholdstype.NORMALT,
            arbeidsavtaler = arbeidsavtaleList,
            permisjonPermittering = emptyList()
        )
    }
}
