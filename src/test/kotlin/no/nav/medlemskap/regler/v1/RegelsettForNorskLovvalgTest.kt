package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.assertSvar
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.evaluer
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Test

class RegelsettForNorskLovvalgTest {

    private val personleser = Personleser()

    @Test
    fun `person med norsk statsborgerskap, får ja`() {
        assertSvar(REGEL_11, Svar.JA, evaluer(personleser.brukerErNorskStatsborger()), Svar.JA)
    }

    @Test
    fun `person med utenlandsk statsborgerskap, får nei`() {
        assertSvar(REGEL_11, Svar.NEI, evaluer(personleser.brukerErIkkeNorskStatsborger()), Svar.JA)
    }

    @Test
    fun `person med ektefelle får ja`() {
        assertSvar(REGEL_11_2, Svar.JA, evaluer(personleser.brukerHarEktefelle()), Svar.JA)
    }

    @Test
    fun `person uten ektefelle får nei`() {
        assertSvar(REGEL_11_2, Svar.NEI, evaluer(personleser.brukerHarIkkeEktefelle()), Svar.JA)
    }

    @Test
    fun `person har ikke ektefelle men barn får ja`() {
        assertSvar(REGEL_11_2_1, Svar.JA, evaluer(personleser.brukerHarIkkeEktefelleMenBarn()), Svar.JA)
    }

    @Test
    fun `person har ikke ektefelle og ikke barn får nei`() {
        assertSvar(REGEL_11_2_1, Svar.NEI, evaluer(personleser.brukerHarIkkeEktefelleOgIkkeBarn()), Svar.JA)
    }

   /* fun `Bruker over 25 regnes ikke som barn`() {
        assertSvar("11.2.1", Svar.NEI, evaluer(personleser.brukerHarBarnOver25RegnesIkkeSomBarn()), Svar.JA)
    }*/

    fun `person har ikke ektefelle men har folkeregistrerte barn får ja`() {
        assertSvar(REGEL_11_2_2, Svar.JA, evaluer(personleser.brukerHarIkkeEktefelleMenFolkeregistrerteBarn()), Svar.JA)
    }

    @Test
    fun `person har ikke ektefelle og ikke folkeregistrerte barn får nei`() {
        assertSvar(REGEL_11_2_2, Svar.NEI, evaluer(personleser.brukerHarIkkeEktefelleOgIkkeFolkeregistrerteBarn()), Svar.JA)
    }

    @Test
    fun `person har ikke ektefelle men har folkeregistrerte barn og har jobbet mer enn 80 prosent får ja`() {
        assertSvar(REGEL_11_2_3, Svar.JA, evaluer(personleser.brukerHarIkkeEktefelleMenMedFolkeregistrerteBarnHarJobbetMerEnn80rosent()), Svar.JA)
    }

    @Test
    fun  `person har ikke ektefelle men har folkeregistrerte barn og har ikke jobbet mer enn 80 prosent får nei`() {
        assertSvar(REGEL_11_2_3, Svar.NEI, evaluer(personleser.brukerHarIkkeEktefelleMenMedFolkeregistrerteBarnHarIkkeJobbetMerEnn80Prosent()), Svar.UAVKLART)
    }

    @Test
    fun `person har ikke ektefelle men og ikke folkeregistrerte barn har jobbet mer enn 100% får ja`() {
        assertSvar(REGEL_11_2_2_1, Svar.JA, evaluer(personleser.brukerHarIkkeEktefelleOgIkkeFolkeregistrerteBarnHarJobbetMerEnn100rosent()), Svar.JA)
    }

    @Test
    fun `person har ikke ektefelle men og ikke folkeregistrerte barn har ikke jobbet mer enn 100% får nei`() {
        assertSvar(REGEL_11_2_2_1, Svar.NEI, evaluer(personleser.brukerHarIkkeEktefelleOgIkkeFolkeregistrerteBarnHarIkkeJobbetMerEnn100Prosent()), Svar.UAVKLART)
    }

    @Test
    fun `person med ektefelle og barn får ja`() {
        assertSvar(REGEL_11_3, Svar.JA, evaluer(personleser.brukerHarEktefelleOgBarn()), Svar.JA)
    }

    @Test
    fun `person med ektefelle og ikke barn får nei`() {
        assertSvar(REGEL_11_3, Svar.NEI, evaluer(personleser.brukerHarEktefelleMenIkkeBarn()), Svar.JA)
    }

    @Test
    fun `person med folkeregistrert ektefelle uten barn får ja`() {
        assertSvar(REGEL_11_3_1, Svar.JA, evaluer(personleser.brukerUtenBarnHarFolkeregistrertEktefelle()), Svar.JA)
    }

    @Test
    fun `person uten barn med ektefelle som ikke er folkeregistrert får nei`() {
        assertSvar(REGEL_11_3_1, Svar.NEI, evaluer(personleser.brukerUtenBarnHarIkkeFolkeregistrertEktefelle()), Svar.JA)
    }

    @Test
    fun `barnløs bruker barn uten folkeregistrert ektefelle, men har jobbet 100% får ja`() {
        assertSvar(REGEL_11_3_1_1, Svar.JA, evaluer(personleser.brukerUtenBarnOgUtenFolkeregistrertEktefelleHarVeartI100prosent()), Svar.JA)
    }

    @Test
    fun `barnløs bruker barn uten folkeregistrert ektefelle, men har ikke jobbet 100% får nei`() {
        assertSvar(REGEL_11_3_1_1, Svar.NEI, evaluer(personleser.brukerUtenBarnOgUtenFolkeregistrertEktefelleHarIkkeVeartI100prosent()), Svar.UAVKLART)
    }

    @Test
    fun `person med barn har ektefelle registrert i Norge får ja`() {
        assertSvar(REGEL_11_4, Svar.JA, evaluer(personleser.brukerHarBarnOgEktefelleErRegistrertINorge()), Svar.JA)
    }

    @Test
    fun `person med barn har ektefelle registrert i Norge får nei`() {
        assertSvar(REGEL_11_4, Svar.NEI, evaluer(personleser.brukerHarBarnOgEktefelleErIkkeRegistrertINorge()), Svar.JA)
    }

    @Test
    fun `person uten folkeregistrert ektefelle som har folkeregistrert barn får ja`() {
        assertSvar(REGEL_11_4_1, Svar.JA, evaluer(personleser.brukerHarIkkeFolkeregistrertEktefelleMenFolkeregistrertBarn()), Svar.UAVKLART)
    }

    @Test
    fun `person uten folkeregistrert ektefelle som ikke folkeregistrert barn får nei`() {
        assertSvar(REGEL_11_4_1, Svar.NEI, evaluer(personleser.brukerHarIkkeFolkeregistrertEktefelleOgIkkeFolkeregisrertBarn()), Svar.JA)
    }
    @Test
    fun `person med barn og ektefelle registrert i Norge får ja`() {
        assertSvar(REGEL_11_5, Svar.JA, evaluer(personleser.brukerHarFolkeregistrertEktefelleOgBarn()), Svar.JA)
    }

    @Test
    fun `person med ektefelle registrert, men barn er ikke registrert i Norge får nei`() {
        assertSvar(REGEL_11_5, Svar.NEI, evaluer(personleser.brukerHarFolkeregistrertEktefelleMenBarnErIkkeFolkeregistrert()), Svar.UAVKLART)
    }

/** TODO: Hvorfor feiler disse?
    @Test
    fun `person med folkeregistrerte relasjoner har jobbet mer enn 80 prosent`() {
        assertSvar(REGEL_11_6, Svar.JA, evaluer(personleser.brukermedFolkeregistrertRelasjonerHarJobbetMerEnn80Prosent()), Svar.JA)
    }

    @Test
    fun `person med folkeregistrerte relasjoner har ikke jobbet mer enn 80 prosent`() {
        assertSvar(REGEL_11_6, Svar.NEI, evaluer(personleser.brukermedFolkeregistrertRelasjonerHarIkkeJobbetMerEnn80Prosent()), Svar.UAVKLART)
    }
*/

}