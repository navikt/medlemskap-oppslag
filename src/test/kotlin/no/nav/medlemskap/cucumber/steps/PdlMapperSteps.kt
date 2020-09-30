package no.nav.medlemskap.cucumber.steps

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import io.kotest.matchers.collections.shouldContainExactly
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.cucumber.DomenespråkParser
import no.nav.medlemskap.cucumber.StatsborgerskapMapper
import no.nav.medlemskap.cucumber.mapping.pdl.PdlDomenespråkParser
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.services.pdl.mapper.PdlMapper.mapStatsborgerskap

class PdlMapperSteps : No {
    private val pdlDomenespråkParser = PdlDomenespråkParser()
    private val domenespråkParser = DomenespråkParser()

    private var pdlStatsborgerskap: List<HentPerson.Statsborgerskap> = emptyList()

    private var statsborgerskap: List<Statsborgerskap> = emptyList()

    init {
        Gitt<DataTable>("følgende statsborgerskap fra PDL:") { dataTable: DataTable? ->
            pdlStatsborgerskap = pdlDomenespråkParser.mapDataTable(dataTable, PdlDomenespråkParser.StatsborgerskapMapper())
        }

        Når("statsborgerskap mappes") {
            statsborgerskap = mapStatsborgerskap(pdlStatsborgerskap)
        }

        Så<DataTable>("skal mappet statsborgerskap være") { dataTable: DataTable? ->
            val statsborgerskapForventet = domenespråkParser.mapDataTable(dataTable, StatsborgerskapMapper())

            statsborgerskap.shouldContainExactly(statsborgerskapForventet)
        }
    }
}
