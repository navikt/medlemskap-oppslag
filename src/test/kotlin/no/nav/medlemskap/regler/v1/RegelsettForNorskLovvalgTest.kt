package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.assertSvar
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.evaluer
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Test

class RegelsettForNorskLovvalgTest {

    private val personleser = Personleser()

    @Test
    fun `person har svart ja på jobb utenfor Norge, får NEI`() {
        assertSvar("9", Svar.JA, evaluer(personleser.brukerHarJobbetUtenforNorge()), Svar.NEI)
    }

    @Test
    fun `person har svart nei på jobb utenfor Norge, får JA`() {
        assertSvar("9", Svar.NEI, evaluer(personleser.brukerHarIkkeJobbetUtenforNorge()), Svar.JA)
    }

    @Test
    fun `norsk person kun bostedsadresse siste 12 mnd får JA`() {
        assertSvar("10", Svar.JA, evaluer(personleser.brukerHarKunBostedsadresse()), Svar.JA)
    }

    @Test
    fun `person uten bostedsadresse, får uavklart`() {
        assertSvar("10", Svar.NEI, evaluer(personleser.brukerHarIngenBostedsadresse()), Svar.UAVKLART)
    }

    @Test
    fun `person med alle adresser med norsk landkode, får ja`() {
        assertSvar("10", Svar.JA, evaluer(personleser.brukerHarAlleAdresserNorsk()), Svar.JA)
    }


    @Test
    fun `person med utenlandsk postadresse, får uavklart`() {
        assertSvar("10", Svar.NEI, evaluer(personleser.brukerHarPostadresseIUtland()), Svar.UAVKLART)
    }

    @Test
    fun `person med utenlandsk midlertidig adresse, får uavklart`() {
        assertSvar("10", Svar.NEI, evaluer(personleser.brukerHarMidlertidigAdresseIUtland()), Svar.UAVKLART)
    }

    @Test
    fun `person med norsk statsborgerskap, får ja`() {
        assertSvar("11", Svar.JA, evaluer(personleser.brukerErNorskStatsborger()), Svar.JA)
    }

    @Test
    fun `person med utenlandsk statsborgerskap, får nei`() {
        assertSvar("11", Svar.NEI, evaluer(personleser.brukerErIkkeNorskStatsborger()), Svar.JA)
    }

    @Test
    fun `person med ektefelle får ja`() {
        assertSvar("11.2", Svar.JA, evaluer(personleser.brukerHarEktefelle()), Svar.JA)
    }

    @Test
    fun `person uten ektefelle får nei`() {
        assertSvar("11.2", Svar.NEI, evaluer(personleser.brukerHarIkkeEktefelle()), Svar.JA)
    }

    @Test
    fun `person har ikke ektefelle men barn får ja`() {
        assertSvar("11.2.1", Svar.JA, evaluer(personleser.brukerHarIkkeEktefelleMenBarn()), Svar.JA)
    }

    @Test
    fun `person har ikke ektefelle og ikke barn får nei`() {
        assertSvar("11.2.1", Svar.NEI, evaluer(personleser.brukerHarIkkeEktefelleOgIkkeBarn()), Svar.JA)
    }

    @Test
    fun `person med ektefelle og barn får ja`() {
        assertSvar("11.3", Svar.JA, evaluer(personleser.brukerHarEktefelleOgBarn()), Svar.JA)
    }

    @Test
    fun `person med ektefelle og ikke barn får nei`() {
        assertSvar("11.3", Svar.NEI, evaluer(personleser.brukerHarEktefelleMenIkkeBarn()), Svar.JA)
    }

    @Test
    fun `person med ett arbeidsforhold med to overlappende arbeidsavtaler som til sammen utgjør mer enn 25% i stillingsprosent, får ja`() {
        assertSvar("12", Svar.JA, evaluer(personleser.norskMedToOverlappendeArbeidsavtalerSomTilSammenErOver25ProsentIPeriode()), Svar.JA)
    }

    @Test
    fun `person med ett arbeidsforhold som har en arbeidsavtale med mindre enn 25% stillingsprosent, får uavklart`() {
        assertSvar("12", Svar.NEI, evaluer(personleser.norskMedEttArbeidsforholdMedArbeidsavtaleUnder25ProsentStillingIPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `person med ett arbeidsforhold med to arbeidsavtaler som utgjør mindre enn 25% i stillingsprosent, får uavklart`() {
        assertSvar("12", Svar.NEI, evaluer(personleser.norskMedToArbeidsavtalerISammeArbeidsforholdMedForLavTotalStillingProsentIPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `person med to parallelle arbeidsforhold, hvor ene er heltid og andre er deltid`() {
        assertSvar("12", Svar.JA, evaluer(personleser.norskMedToParallelleArbeidsforholdHvoravEnLavStillingsprosent()), Svar.JA)
    }

    @Test
    fun `person med to serielle arbeidsforhold, hvor ene er heltid og andre er deltid`() {
        assertSvar("12", Svar.NEI, evaluer(personleser.norskMedToSerielleArbeidsforholdHvoravEnLavStillingsprosent()), Svar.UAVKLART)
    }

    @Test
    fun `person med to delvis overlappende arbeidsforhold, hvor ene har for lav stillingsprosent`() {
        assertSvar("12", Svar.NEI, evaluer(personleser.norskMedToDelvisOverlappendeArbeidsforholdHvoravEnLavStillingsprosent()), Svar.UAVKLART)
    }

    @Test
    fun `person med to sammenhengende arbeidsforhold innenfor kontrollperiode, får ja`() {
        assertSvar("12", Svar.JA, evaluer(personleser.norskMedToSammenhengendeArbeidsforholdIPeriode()), Svar.JA)
    }

}