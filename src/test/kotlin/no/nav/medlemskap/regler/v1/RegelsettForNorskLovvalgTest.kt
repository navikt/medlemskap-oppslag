package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.assertSvar
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.evaluer
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Test

class RegelsettForNorskLovvalgTest {

    private val personleser = Personleser()

    @Test
    fun `person bosatt i Norge siste 12 mnd, får ja`() {
        assertSvar("LOV-2", Svar.JA, evaluer(personleser.enkelNorskArbeid()), Svar.JA)
    }

    @Test
    fun `person ikke bosatt i Norge siste 12 mnd, får uavklart`() {
        assertSvar("LOV-2", Svar.NEI, evaluer(personleser.norskBosattIUtland()), Svar.UAVKLART)
    }

    @Test
    fun `person med norsk bostedsadresse og postadresse, får ja`() {
        assertSvar("LOV-2", Svar.JA, evaluer(personleser.enkelNorskMedNorskBostedsadresseOgPostadresse()), Svar.JA)
    }

    @Test
    fun `norsk person med utenlandsk bostedsadresse, får uavklart`() {
        assertSvar("LOV-2", Svar.NEI, evaluer(personleser.norskBosattIUtland()), Svar.UAVKLART)
    }

    @Test
    fun `norsk person med utenlandsk postadresse, får uavklart`() {
        assertSvar("LOV-2", Svar.NEI, evaluer(personleser.norskPostadresseIUtland()), Svar.UAVKLART)
    }

    @Test
    fun `person med norsk statsborgerskap, kun arbeid i Norge, får ja`() {
        assertSvar("LOV-3", Svar.JA, evaluer(personleser.enkelNorskArbeid()), Svar.JA)
    }

    @Test
    fun `person med ett arbeidsforhold med to overlappende arbeidsavtaler som til sammen utgjør mer enn 25% i stillingsprosent, får ja`() {
        assertSvar("LOV-4", Svar.JA, evaluer(personleser.norskMedToOverlappendeArbeidsavtalerSomTilSammenErOver25ProsentIPeriode()), Svar.JA)
    }

    @Test
    fun `person med ett arbeidsforhold som har en arbeidsavtale med mindre enn 25% stillingsprosent, får uavklart`() {
        assertSvar("LOV-4", Svar.NEI, evaluer(personleser.norskMedEttArbeidsforholdMedArbeidsavtaleUnder25ProsentStillingIPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `person med ett arbeidsforhold med to arbeidsavtaler som utgjør mindre enn 25% i stillingsprosent, får uavklart`() {
        assertSvar("LOV-4", Svar.NEI, evaluer(personleser.norskMedToArbeidsavtalerISammeArbeidsforholdMedForLavTotalStillingProsentIPeriode()), Svar.UAVKLART)
    }

    @Test
    fun `person med to parallelle arbeidsforhold, hvor ene er heltid og andre er deltid`() {
        assertSvar("LOV-4", Svar.JA, evaluer(personleser.norskMedToParallelleArbeidsforholdHvoravEnLavStillingsprosent()), Svar.JA)
    }

    @Test
    fun `person med to serielle arbeidsforhold, hvor ene er heltid og andre er deltid`() {
        assertSvar("LOV-4", Svar.NEI, evaluer(personleser.norskMedToSerielleArbeidsforholdHvoravEnLavStillingsprosent()), Svar.UAVKLART)
    }

    @Test
    fun `person med to delvis overlappende arbeidsforhold, hvor ene har for lav stillingsprosent`() {
        assertSvar("LOV-4", Svar.NEI, evaluer(personleser.norskMedToDelvisOverlappendeArbeidsforholdHvoravEnLavStillingsprosent()), Svar.UAVKLART)
    }

}