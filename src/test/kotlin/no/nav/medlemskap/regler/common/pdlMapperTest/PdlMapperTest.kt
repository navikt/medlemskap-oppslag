package no.nav.medlemskap.regler.common.pdlMapperTest

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.services.pdl.mapper.PdlMapper.mapTilPersonHistorikkTilBruker
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PdlMapperTest {

    private val pdlData = PdlTestData()
    private val mappetHistorikkTilBruker = mapTilPersonHistorikkTilBruker(pdlData.pdlPersonBruker())
    private val medlemskapDomeneData = MedlemskapTestData()

    @Test
    fun `PDLdata mappes om til riktige statsborgerskap `() {

        val statsborgerskapUtlandMedlemskap = medlemskapPersonhistorikk().statsborgerskap[0]
        val statsborgerskapNorMedlemskap = medlemskapPersonhistorikk().statsborgerskap[1]

        val mappetStatsborgerskapUtland = mappetHistorikkTilBruker.statsborgerskap[0]
        val mappetStatsborgerskapNOR = mappetHistorikkTilBruker.statsborgerskap[1]

        Assertions.assertEquals(mappetHistorikkTilBruker.statsborgerskap.size, medlemskapPersonhistorikk().statsborgerskap.size)

        Assertions.assertEquals(statsborgerskapUtlandMedlemskap.landkode, mappetStatsborgerskapUtland.landkode)
        Assertions.assertEquals(statsborgerskapUtlandMedlemskap.fom, mappetStatsborgerskapUtland.fom)
        Assertions.assertEquals(statsborgerskapUtlandMedlemskap.tom, mappetStatsborgerskapUtland.tom)

        Assertions.assertEquals(statsborgerskapNorMedlemskap.landkode, mappetStatsborgerskapNOR.landkode)
        Assertions.assertEquals(statsborgerskapNorMedlemskap.fom, mappetStatsborgerskapNOR.fom)
        Assertions.assertEquals(statsborgerskapNorMedlemskap.tom, mappetStatsborgerskapNOR.tom)
    }

    @Test
    fun `PDLdata mappes om til riktige bostedsadresser`() {

        val bostedsadresseMedlemskap = medlemskapPersonhistorikk().bostedsadresser[0]
        val bostedsadresseMedlemskap2 = medlemskapPersonhistorikk().bostedsadresser[1]

        val mappetBostedsadresse = mappetHistorikkTilBruker.bostedsadresser[0]
        val mappetBostedsadresse2 = mappetHistorikkTilBruker.bostedsadresser[1]

        Assertions.assertEquals(mappetHistorikkTilBruker.bostedsadresser.size, medlemskapPersonhistorikk().bostedsadresser.size)

        Assertions.assertEquals(bostedsadresseMedlemskap.landkode, mappetBostedsadresse.landkode)
        Assertions.assertEquals(bostedsadresseMedlemskap.fom, mappetBostedsadresse.fom)
        Assertions.assertEquals(bostedsadresseMedlemskap.tom, mappetBostedsadresse.tom)

        Assertions.assertEquals(bostedsadresseMedlemskap2.landkode, mappetBostedsadresse.landkode)
        Assertions.assertEquals(bostedsadresseMedlemskap2.fom, mappetBostedsadresse2.fom)
        Assertions.assertEquals(bostedsadresseMedlemskap2.tom, mappetBostedsadresse2.tom)
    }

    @Test
    fun `PDLdata mappes om til riktige oppholdsadresser`() {

        Assertions.assertEquals(mappetHistorikkTilBruker.oppholdsadresser.size, medlemskapPersonhistorikk().oppholdsadresser.size)

        val oppholdsadresseMedlemskap = medlemskapPersonhistorikk().oppholdsadresser[0]
        val oppholdsadresseMedlemskap2 = medlemskapPersonhistorikk().oppholdsadresser[1]

        val mappetOppholdssadresse = mappetHistorikkTilBruker.oppholdsadresser[0]
        val mappetOppholdsadresse2 = mappetHistorikkTilBruker.oppholdsadresser[1]

        Assertions.assertEquals(oppholdsadresseMedlemskap.landkode, mappetOppholdssadresse.landkode)
        Assertions.assertEquals(oppholdsadresseMedlemskap.fom, mappetOppholdssadresse.fom)
        Assertions.assertEquals(oppholdsadresseMedlemskap.tom, mappetOppholdssadresse.tom)

        Assertions.assertEquals(oppholdsadresseMedlemskap2.landkode, mappetOppholdsadresse2.landkode)
        Assertions.assertEquals(oppholdsadresseMedlemskap2.fom, mappetOppholdsadresse2.fom)
        Assertions.assertEquals(oppholdsadresseMedlemskap2.tom, mappetOppholdsadresse2.tom)
    }

    @Test
    fun `PDLdata mappes om til riktige kontaktadresser`() {
        Assertions.assertEquals(mappetHistorikkTilBruker.kontaktadresser.size, medlemskapPersonhistorikk().kontaktadresser.size)

        val oppholdsadresseMedlemskap = medlemskapPersonhistorikk().kontaktadresser[0]
        val mappetKontaktadresse = mappetHistorikkTilBruker.kontaktadresser[0]

        Assertions.assertEquals(oppholdsadresseMedlemskap.landkode, mappetKontaktadresse.landkode)
        Assertions.assertEquals(oppholdsadresseMedlemskap.fom, mappetKontaktadresse.fom)
        Assertions.assertEquals(oppholdsadresseMedlemskap.tom, mappetKontaktadresse.tom)
    }

    @Test
    fun `PDLdata mappes om til riktig sivilstand`() {
        Assertions.assertEquals(mappetHistorikkTilBruker.kontaktadresser.size, medlemskapPersonhistorikk().kontaktadresser.size)

        val sivilstandMedlemskap = medlemskapPersonhistorikk().sivilstand[0]
        val sivilstandMappet = mappetHistorikkTilBruker.sivilstand[0]

        Assertions.assertEquals(sivilstandMedlemskap.type, sivilstandMappet.type)
        Assertions.assertEquals(sivilstandMedlemskap.relatertVedSivilstand, sivilstandMappet.relatertVedSivilstand)
        Assertions.assertEquals(sivilstandMedlemskap.gyldigFraOgMed, sivilstandMappet.gyldigFraOgMed)
        Assertions.assertEquals(sivilstandMedlemskap.gyldigTilOgMed, sivilstandMappet.gyldigTilOgMed)
        Assertions.assertEquals(sivilstandMedlemskap.folkeregistermetadata?.ajourholdstidspunkt, sivilstandMappet.folkeregistermetadata?.ajourholdstidspunkt)
        Assertions.assertEquals(sivilstandMedlemskap.folkeregistermetadata?.gyldighetstidspunkt, sivilstandMappet.folkeregistermetadata?.gyldighetstidspunkt)
        Assertions.assertEquals(sivilstandMedlemskap.folkeregistermetadata?.opphoerstidspunkt, sivilstandMappet.folkeregistermetadata?.opphoerstidspunkt)
    }

    @Test
    fun `PDLdata mappes om til riktig familierelasjoner`() {
        Assertions.assertEquals(mappetHistorikkTilBruker.familierelasjoner.size, medlemskapPersonhistorikk().familierelasjoner.size)

        val familierelasjonerMedlemskap = medlemskapPersonhistorikk().familierelasjoner[0]
        val familierelasjonerMappet = mappetHistorikkTilBruker.familierelasjoner[0]

        Assertions.assertEquals(familierelasjonerMedlemskap.relatertPersonsRolle, familierelasjonerMappet.relatertPersonsRolle)
        Assertions.assertEquals(familierelasjonerMedlemskap.minRolleForPerson, familierelasjonerMappet.minRolleForPerson)
        Assertions.assertEquals(familierelasjonerMappet.relatertPersonsIdent, familierelasjonerMappet.relatertPersonsIdent)

        Assertions.assertEquals(familierelasjonerMedlemskap.folkeregistermetadata?.ajourholdstidspunkt, familierelasjonerMappet.folkeregistermetadata?.ajourholdstidspunkt)
        Assertions.assertEquals(familierelasjonerMedlemskap.folkeregistermetadata?.gyldighetstidspunkt, familierelasjonerMappet.folkeregistermetadata?.gyldighetstidspunkt)
        Assertions.assertEquals(familierelasjonerMedlemskap.folkeregistermetadata?.opphoerstidspunkt, familierelasjonerMappet.folkeregistermetadata?.opphoerstidspunkt)
    }

    fun medlemskapPersonhistorikk() = Personhistorikk(
        statsborgerskap = listOf(medlemskapDomeneData.medlemskapStatsborgerskapUtland(), medlemskapDomeneData.medlemskapStatsborgerskap()),
        familierelasjoner = listOf(medlemskapDomeneData.medlemskapFamilierelasjoner()),
        bostedsadresser = listOf(medlemskapDomeneData.medlemskapBostedAdresse(), medlemskapDomeneData.medlemskapBostedsAdresse2()),
        kontaktadresser = listOf(medlemskapDomeneData.medlemskapKontaktAdresse()),
        oppholdsadresser = listOf(medlemskapDomeneData.medlemskapOppholdsadresse(), medlemskapDomeneData.medlemskapOppholdsadresseNor()),
        sivilstand = listOf(medlemskapDomeneData.medlemskapSivilstand()),
        personstatuser = emptyList()
    )
}
