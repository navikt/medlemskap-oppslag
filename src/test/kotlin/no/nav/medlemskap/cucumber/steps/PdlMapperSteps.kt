package no.nav.medlemskap.cucumber.steps

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import io.kotest.matchers.collections.shouldContainExactly
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.cucumber.AdresseMapper
import no.nav.medlemskap.cucumber.DomenespråkParser
import no.nav.medlemskap.cucumber.SivilstandMapper
import no.nav.medlemskap.cucumber.StatsborgerskapMapper
import no.nav.medlemskap.cucumber.mapping.pdl.PdlDomenespråkParser
import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.Sivilstand
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.services.pdl.PdlSivilstandMapper.mapSivilstander
import no.nav.medlemskap.services.pdl.mapper.PdlMapper.mapBostedsadresser
import no.nav.medlemskap.services.pdl.mapper.PdlMapper.mapKontaktAdresser
import no.nav.medlemskap.services.pdl.mapper.PdlMapper.mapOppholdsadresser
import no.nav.medlemskap.services.pdl.mapper.PdlMapper.mapStatsborgerskap

class PdlMapperSteps : No {
    private val pdlDomenespråkParser = PdlDomenespråkParser()
    private val domenespråkParser = DomenespråkParser()

    private var pdlStatsborgerskap: List<HentPerson.Statsborgerskap> = emptyList()
    private var pdlBostedsadresser: List<HentPerson.Bostedsadresse> = emptyList()
    private var pdlKontaktadresser: List<HentPerson.Kontaktadresse> = emptyList()
    private var pdlOppholdsadresser: List<HentPerson.Oppholdsadresse> = emptyList()
    private var pdlSivilstander: List<HentPerson.Sivilstand> = emptyList()

    private var statsborgerskap: List<Statsborgerskap> = emptyList()
    private var bostedsadresser: List<Adresse> = emptyList()
    private var kontaktadresser: List<Adresse> = emptyList()
    private var oppholdsadresser: List<Adresse> = emptyList()
    private var sivilstander: List<Sivilstand> = emptyList()

    init {
        Gitt<DataTable>("følgende statsborgerskap fra PDL:") { dataTable: DataTable? ->
            pdlStatsborgerskap = pdlDomenespråkParser.mapDataTable(dataTable, PdlDomenespråkParser.StatsborgerskapMapper())
        }

        Gitt<DataTable>("følgende bostedsadresser fra PDL:") { dataTable: DataTable? ->
            pdlBostedsadresser = pdlDomenespråkParser.mapDataTable(dataTable, PdlDomenespråkParser.BostedsadresseMapper())
        }

        Gitt<DataTable>("følgende kontaktadresser fra PDL:") { dataTable: DataTable? ->
            pdlKontaktadresser = pdlDomenespråkParser.mapDataTable(dataTable, PdlDomenespråkParser.KontaktadresseMapper())
        }

        Gitt<DataTable>("følgende oppholdsadresser fra PDL:") { dataTable: DataTable? ->
            pdlOppholdsadresser = pdlDomenespråkParser.mapDataTable(dataTable, PdlDomenespråkParser.OppholdsadresseMapper())
        }

        Gitt<DataTable>("følgende sivilstander fra PDL:") { dataTable: DataTable? ->
            pdlSivilstander = pdlDomenespråkParser.mapDataTable(dataTable, PdlDomenespråkParser.SivilstandMapper())
        }

        Når("statsborgerskap mappes") {
            statsborgerskap = mapStatsborgerskap(pdlStatsborgerskap)
        }

        Når("bostedsadresser mappes") {
            bostedsadresser = mapBostedsadresser(pdlBostedsadresser)
        }

        Når("kontaktadresser mappes") {
            kontaktadresser = mapKontaktAdresser(pdlKontaktadresser)
        }

        Når("oppholdsadresser mappes") {
            oppholdsadresser = mapOppholdsadresser(pdlOppholdsadresser)
        }

        Når("sivilstander mappes") {
            sivilstander = mapSivilstander(pdlSivilstander)
        }

        Så<DataTable>("skal mappet statsborgerskap være") { dataTable: DataTable? ->
            val statsborgerskapForventet = domenespråkParser.mapDataTable(dataTable, StatsborgerskapMapper())

            statsborgerskap.shouldContainExactly(statsborgerskapForventet)
        }

        Så<DataTable>("skal mappede bostedsadresser være") { dataTable: DataTable? ->
            val bostedsadresserForventet = domenespråkParser.mapDataTable(dataTable, AdresseMapper())

            bostedsadresser.shouldContainExactly(bostedsadresserForventet)
        }

        Så<DataTable>("skal mappede kontaktadresser være") { dataTable: DataTable? ->
            val kontaktadresserForventet = domenespråkParser.mapDataTable(dataTable, AdresseMapper())

            kontaktadresser.shouldContainExactly(kontaktadresserForventet)
        }

        Så<DataTable>("skal mappede oppholdsadresser være") { dataTable: DataTable? ->
            val oppholdsadresserForventet = domenespråkParser.mapDataTable(dataTable, AdresseMapper())

            oppholdsadresser.shouldContainExactly(oppholdsadresserForventet)
        }

        Så<DataTable>("skal mappede sivilstander være") { dataTable: DataTable? ->
            val sivilstanderForventet = domenespråkParser.mapDataTable(dataTable, SivilstandMapper())

            sivilstander.shouldContainExactly(sivilstanderForventet)
        }
    }
}
