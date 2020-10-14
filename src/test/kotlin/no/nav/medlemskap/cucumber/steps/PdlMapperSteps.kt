package no.nav.medlemskap.cucumber.steps

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import io.kotest.matchers.collections.shouldContainExactly
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.cucumber.*
import no.nav.medlemskap.cucumber.mapping.pdl.PdlDomenespråkParser
import no.nav.medlemskap.domene.Personhistorikk
import no.nav.medlemskap.services.pdl.mapper.PdlMapper

class PdlMapperSteps : No {
    private val pdlDomenespråkParser = PdlDomenespråkParser()
    private val domenespråkParser = DomenespråkParser()

    private var pdlPersonBuilder = PdlPersonBuilder()

    private var personhistorikk: Personhistorikk? = null

    init {
        Gitt<DataTable>("følgende statsborgerskap fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.statsborgerskap = pdlDomenespråkParser.mapDataTable(dataTable, PdlDomenespråkParser.StatsborgerskapMapper())
        }

        Gitt<DataTable>("følgende bostedsadresser fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.bostedsadresser = pdlDomenespråkParser.mapDataTable(dataTable, PdlDomenespråkParser.BostedsadresseMapper())
        }

        Gitt<DataTable>("følgende kontaktadresser fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.kontaktadresser = pdlDomenespråkParser.mapDataTable(dataTable, PdlDomenespråkParser.KontaktadresseMapper())
        }

        Gitt<DataTable>("følgende oppholdsadresser fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.oppholdsadresser = pdlDomenespråkParser.mapDataTable(dataTable, PdlDomenespråkParser.OppholdsadresseMapper())
        }

        Gitt<DataTable>("følgende sivilstander fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.sivilstander = pdlDomenespråkParser.mapDataTable(dataTable, PdlDomenespråkParser.SivilstandMapper())
        }

        Gitt<DataTable>("følgende familierelasjoner fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.familierelasjoner = pdlDomenespråkParser.mapDataTable(dataTable, PdlDomenespråkParser.FamilerelasjonMapper())
        }

        Gitt<DataTable>("følgende opplysninger om doedsfall fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.doedsfall = pdlDomenespråkParser.mapDataTable(dataTable, PdlDomenespråkParser.DoedsfallMapper())
        }

        Når("statsborgerskap mappes") {
            personhistorikk = mapTilPersonhistorikk()
        }

        Når("bostedsadresser mappes") {
            personhistorikk = mapTilPersonhistorikk()
        }

        Når("kontaktadresser mappes") {
            personhistorikk = mapTilPersonhistorikk()
        }

        Når("oppholdsadresser mappes") {
            personhistorikk = mapTilPersonhistorikk()
        }

        Når("sivilstander mappes") {
            personhistorikk = mapTilPersonhistorikk()
        }

        Når("doedsfall mappes") {
            personhistorikk = mapTilPersonhistorikk()
        }

        Når("familierelasjoner mappes") {
            personhistorikk = mapTilPersonhistorikk()
        }

        Når("PDL Person mappes til personhistorikk i datagrunnlaget") {
            personhistorikk = mapTilPersonhistorikk()
        }

        Så<DataTable>("skal mappet statsborgerskap være") { dataTable: DataTable? ->
            val statsborgerskapForventet = domenespråkParser.mapDataTable(dataTable, StatsborgerskapMapper())

            personhistorikk!!.statsborgerskap.shouldContainExactly(statsborgerskapForventet)
        }

        Så<DataTable>("skal mappede bostedsadresser være") { dataTable: DataTable? ->
            val bostedsadresserForventet = domenespråkParser.mapDataTable(dataTable, AdresseMapper())

            personhistorikk!!.bostedsadresser.shouldContainExactly(bostedsadresserForventet)
        }

        Så<DataTable>("skal mappede kontaktadresser være") { dataTable: DataTable? ->
            val kontaktadresserForventet = domenespråkParser.mapDataTable(dataTable, AdresseMapper())

            personhistorikk!!.kontaktadresser.shouldContainExactly(kontaktadresserForventet)
        }

        Så<DataTable>("skal mappede oppholdsadresser være") { dataTable: DataTable? ->
            val oppholdsadresserForventet = domenespråkParser.mapDataTable(dataTable, AdresseMapper())

            personhistorikk!!.oppholdsadresser.shouldContainExactly(oppholdsadresserForventet)
        }

        Så<DataTable>("skal mappede doedsfall være") { dataTable: DataTable? ->
            val doedsfallForventet = domenespråkParser.mapDataTable(dataTable, DoedsfallMapper())
            personhistorikk?.doedsfall.shouldContainExactly(doedsfallForventet)
        }

        Så<DataTable>("skal mappede sivilstander være") { dataTable: DataTable? ->
            val sivilstanderForventet = domenespråkParser.mapDataTable(dataTable, SivilstandMapper())

            personhistorikk!!.sivilstand.shouldContainExactly(sivilstanderForventet)
        }

        Så<DataTable>("skal mappede familierelasjoner være") { dataTable: DataTable? ->
            val familierelasjonerForventet = domenespråkParser.mapDataTable(dataTable, FamilieRelasjonMapper())

            personhistorikk!!.familierelasjoner.shouldContainExactly(familierelasjonerForventet)
        }

        Så<DataTable>("skal personhistorikk.familierelasjoner være") { dataTable: DataTable? ->
        }
    }

    private fun mapTilPersonhistorikk(): Personhistorikk {
        return PdlMapper.mapTilPersonHistorikkTilBruker(pdlPersonBuilder.build())
    }

    class PdlPersonBuilder {
        var statsborgerskap: List<HentPerson.Statsborgerskap> = emptyList()
        var bostedsadresser: List<HentPerson.Bostedsadresse> = emptyList()
        var kontaktadresser: List<HentPerson.Kontaktadresse> = emptyList()
        var oppholdsadresser: List<HentPerson.Oppholdsadresse> = emptyList()
        var sivilstander: List<HentPerson.Sivilstand> = emptyList()
        var familierelasjoner: List<HentPerson.Familierelasjon> = emptyList()
        val personstatuser: List<HentPerson.Folkeregisterpersonstatus> = emptyList()
        var doedsfall: List<HentPerson.Doedsfall> = emptyList()

        fun build(): HentPerson.Person {
            return HentPerson.Person(
                familierelasjoner = familierelasjoner,
                statsborgerskap = statsborgerskap,
                sivilstand = sivilstander,
                bostedsadresse = bostedsadresser,
                kontaktadresse = kontaktadresser,
                oppholdsadresse = oppholdsadresser,
                folkeregisterpersonstatus = personstatuser,
                doedsfall = doedsfall
            )
        }
    }
}
