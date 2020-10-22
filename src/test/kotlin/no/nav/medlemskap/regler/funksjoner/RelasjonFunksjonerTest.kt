package no.nav.medlemskap.clients.pdl.mapper

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class RelasjonFunksjonerTest {

    @Test
    fun `hent fnr til ektefelle`() {
        val personhistorikk = lagPersonhistorikk(
            sivilstand = listOf(
                lagSivilstand(relatertVedSivilstand = "13128619857")
            )
        )
        val fnrTilEktefelle = RelasjonFunksjoner.hentFnrTilEktefelle(personhistorikk)
        assertEquals("13128619857", fnrTilEktefelle)
    }

    @Test
    fun `hent fnr til barn`() {
        val familierelasjoner = listOf(
            lagFamilierelasjon(relatertPersonIdent = "25079528660", minRolleForPerson = Familierelasjonsrolle.FAR),
            lagFamilierelasjon(relatertPersonIdent = "26079447659", relatertPersonsRolle = Familierelasjonsrolle.MOR),
            lagFamilierelasjon(relatertPersonIdent = "09069534888", minRolleForPerson = Familierelasjonsrolle.FAR)
        )

        val fnrTilBarn = RelasjonFunksjoner.hentFnrTilBarn(familierelasjoner)
        val forventetSvar = listOf("25079528660", "09069534888")
        assertEquals(2, fnrTilBarn.size)
        assertEquals(forventetSvar, fnrTilBarn)
    }

    private fun lagPersonhistorikk(
        statsborgerskap: List<Statsborgerskap> = listOf(lagStatsborgerskap()),
        bostedsadresser: List<Adresse> = listOf(lagAdresse()),
        kontaktadresser: List<Adresse> = listOf(lagAdresse()),
        oppholdsadresser: List<Adresse> = listOf(lagAdresse()),
        sivilstand: List<Sivilstand> = listOf(lagSivilstand()),
        familierelasjoner: List<Familierelasjon> = listOf(lagFamilierelasjon()),
        doedsfall: List<LocalDate?> = emptyList()

    ): Personhistorikk =
        Personhistorikk(
            statsborgerskap = statsborgerskap,
            bostedsadresser = bostedsadresser,
            kontaktadresser = kontaktadresser,
            oppholdsadresser = oppholdsadresser,
            sivilstand = sivilstand,
            familierelasjoner = familierelasjoner,
            doedsfall = doedsfall
        )

    private fun lagStatsborgerskap(landkode: String = "NOR", fom: LocalDate? = null, tom: LocalDate? = null): Statsborgerskap =
        Statsborgerskap(landkode = landkode, fom = fom, tom = tom)

    private fun lagAdresse(landkode: String = "NOR", fom: LocalDate? = null, tom: LocalDate? = null): Adresse =
        Adresse(landkode = landkode, fom = fom, tom = tom)

    private fun lagSivilstand(
        type: Sivilstandstype = Sivilstandstype.GIFT,
        gyldigFraOgMed: LocalDate? = null,
        gyldigTilOgMed: LocalDate? = null,
        relatertVedSivilstand: String? = null,
        folkeregistermetadata: Folkeregistermetadata? = null
    ): Sivilstand =
        Sivilstand(type, gyldigFraOgMed, gyldigTilOgMed, relatertVedSivilstand, folkeregistermetadata)

    private fun lagFamilierelasjon(
        relatertPersonIdent: String = "12345678910",
        relatertPersonsRolle: Familierelasjonsrolle = Familierelasjonsrolle.BARN,
        minRolleForPerson: Familierelasjonsrolle? = null,
        folkeregistermetadata: Folkeregistermetadata? = null
    ): Familierelasjon =
        Familierelasjon(
            relatertPersonsIdent = relatertPersonIdent,
            relatertPersonsRolle = relatertPersonsRolle,
            minRolleForPerson = minRolleForPerson,
            folkeregistermetadata = folkeregistermetadata
        )
}
