package no.nav.medlemskap.cucumber.steps.pdl

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import io.kotest.matchers.collections.shouldContainExactly
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.cucumber.DomenespråkParser
import no.nav.medlemskap.cucumber.mapping.pdl.PdlDomenespråkParser
import no.nav.medlemskap.domene.Personhistorikk
import no.nav.medlemskap.services.pdl.mapper.PdlMapper

class PdlMapperSteps : No {

    private val pdlDomenespråkParser = PdlDomenespråkParser()

    private var pdlPersonBuilder = PdlPersonBuilder()
    private var personhistorikk: Personhistorikk? = null

    init {
        Gitt<DataTable>("følgende statsborgerskap fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.statsborgerskap = pdlDomenespråkParser.mapStatsborgerskap(dataTable)
        }

        Gitt<DataTable>("følgende bostedsadresser fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.bostedsadresser = pdlDomenespråkParser.mapBostedsadresser(dataTable)
        }

        Gitt<DataTable>("følgende kontaktadresser fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.kontaktadresser = pdlDomenespråkParser.mapKontaktadresser(dataTable)
        }

        Gitt<DataTable>("følgende oppholdsadresser fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.oppholdsadresser = pdlDomenespråkParser.mapOppholdsadresser(dataTable)
        }

        Gitt<DataTable>("følgende sivilstander fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.sivilstander = pdlDomenespråkParser.mapSivilstander(dataTable)
        }

        Gitt<DataTable>("følgende familierelasjoner fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.familierelasjoner = pdlDomenespråkParser.mapFamilierelasjoner(dataTable)
        }

        Gitt<DataTable>("følgende opplysninger om doedsfall fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.doedsfall = pdlDomenespråkParser.mapDoedsfall(dataTable)
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
            val statsborgerskapForventet = DomenespråkParser.mapStatsborgerskap(dataTable)

            personhistorikk!!.statsborgerskap.shouldContainExactly(statsborgerskapForventet)
        }

        Så<DataTable>("skal mappede bostedsadresser være") { dataTable: DataTable? ->
            val bostedsadresserForventet = DomenespråkParser.mapAdresser(dataTable)

            personhistorikk!!.bostedsadresser.shouldContainExactly(bostedsadresserForventet)
        }

        Så<DataTable>("skal mappede kontaktadresser være") { dataTable: DataTable? ->
            val kontaktadresserForventet = DomenespråkParser.mapAdresser(dataTable)

            personhistorikk!!.kontaktadresser.shouldContainExactly(kontaktadresserForventet)
        }

        Så<DataTable>("skal mappede oppholdsadresser være") { dataTable: DataTable? ->
            val oppholdsadresserForventet = DomenespråkParser.mapAdresser(dataTable)

            personhistorikk!!.oppholdsadresser.shouldContainExactly(oppholdsadresserForventet)
        }

        Så<DataTable>("skal mappede sivilstander være") { dataTable: DataTable? ->
            val sivilstanderForventet = DomenespråkParser.mapSivilstander(dataTable)

            personhistorikk!!.sivilstand.shouldContainExactly(sivilstanderForventet)
        }

        Så<DataTable>("skal mappede familierelasjoner være") { dataTable: DataTable? ->
            val familierelasjonerForventet = DomenespråkParser.mapFamilierelasjoner(dataTable)

            personhistorikk!!.familierelasjoner.shouldContainExactly(familierelasjonerForventet)
        }

        Så<DataTable>("skal mappede doedsfall være") { dataTable: DataTable? ->
            val doedsfallForventet = DomenespråkParser.mapDoedsfall(dataTable)
            personhistorikk?.doedsfall.shouldContainExactly(doedsfallForventet)
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
        var doedsfall: List<HentPerson.Doedsfall> = emptyList()

        fun build(): HentPerson.Person {
            return HentPerson.Person(
                familierelasjoner = familierelasjoner,
                statsborgerskap = statsborgerskap,
                sivilstand = sivilstander,
                bostedsadresse = bostedsadresser,
                kontaktadresse = kontaktadresser,
                oppholdsadresse = oppholdsadresser,
                doedsfall = doedsfall
            )
        }
    }
}
