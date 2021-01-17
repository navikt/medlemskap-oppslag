package no.nav.medlemskap.cucumber.steps.medl

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import io.kotest.matchers.shouldBe
import no.nav.medlemskap.cucumber.SpraakParserDomene.MedlemskapDomeneSpraakParser
import no.nav.medlemskap.cucumber.mapping.pdl.medl.MedlDomenespraakParser
import no.nav.medlemskap.cucumber.steps.medl.medlBuilder.MedlMedlemskapsunntakBuilder
import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.services.medl.mapMedlemskapResultat

class MedlMapperSteps : No {

    private val medlDomenespråkParser = MedlDomenespraakParser()

    private var medlBuilder = MedlMedlemskapsunntakBuilder()
    private var medlemskap: List<Medlemskap?> = emptyList()

    init {

        Gitt<DataTable>("følgende om dekning fra MedlMedlemskapsunntak") { dataTable: DataTable ->
            medlBuilder.dekning = medlDomenespråkParser.mapDekning(dataTable)
        }

        Gitt<DataTable>("følgende fraOgMed fra MedlMedlemskapsunntak") { dataTable: DataTable ->
            medlBuilder.fraOgMed = medlDomenespråkParser.mapFraOgMed(dataTable)
        }

        Gitt<DataTable>("følgende tilOgMed fra MedlMedlemskapsunntak") { dataTable: DataTable ->
            medlBuilder.tilOgMed = medlDomenespråkParser.mapTilOgMed(dataTable)
        }

        Gitt<DataTable>("følgende om medlem fra MedlMedlemskapsunntak") { dataTable: DataTable ->
            medlBuilder.medlem = medlDomenespråkParser.mapMedlem(dataTable)
        }

        Gitt<DataTable>("følgende om lovvalg fra MedlMedlemskapsunntak") { dataTable: DataTable ->
            medlBuilder.lovvalg = medlDomenespråkParser.mapLovvalg(dataTable)
        }

        Gitt<DataTable>("følgende om lovvalgsland fra MedlMedlemskapsunntak") { dataTable: DataTable ->
            medlBuilder.lovvalgsland = medlDomenespråkParser.mapLovvalgsland(dataTable)
        }

        Gitt<DataTable>("følgende om status fra MedlMedlemskapsunntak") { dataTable: DataTable ->
            medlBuilder.status = medlDomenespråkParser.mapStatus(dataTable)
        }

        Når("medlemskapsuntak mappes") {
            medlemskap = mapTilMedlDomene()
        }

        Så<DataTable>("skal mappet dekning i Medlemskap være") { dataTable: DataTable ->
            val dekningForventet = MedlemskapDomeneSpraakParser.mapDekning(dataTable)
            medlemskap[0]?.dekning.shouldBe(dekningForventet)
        }

        Så<DataTable>("skal mappet lovvalgsland i Medlemskap være") { dataTable: DataTable ->
            val lovvalgslandForventet = MedlemskapDomeneSpraakParser.mapLovvalgsland(dataTable)
            medlemskap[0]?.lovvalgsland.shouldBe(lovvalgslandForventet)
        }

        Så<DataTable>("skal mappede fra og med i medlemskap domene være") { dataTable: DataTable ->
            val fraOgMedForventet = MedlemskapDomeneSpraakParser.mapFraOgMed(dataTable)
            medlemskap[0]?.fraOgMed.shouldBe(fraOgMedForventet)
        }

        Så<DataTable>("skal mappede til og med i medlemskap domene være") { dataTable: DataTable ->
            val tilOgMedForventet = MedlemskapDomeneSpraakParser.mapTilOgMed(dataTable)
            medlemskap[0]?.tilOgMed.shouldBe(tilOgMedForventet)
        }

        Så<DataTable>("skal mappet erMedlem i Medlemskap domene være") { dataTable: DataTable ->
            val erMedlemForventet = MedlemskapDomeneSpraakParser.mapErMedlem(dataTable)
            medlemskap[0]?.erMedlem.shouldBe(erMedlemForventet)
        }

        Så<DataTable>("skal mappet lovvalg i Medlemskap være") { dataTable: DataTable ->
            val lovvalgForventet = MedlemskapDomeneSpraakParser.mapLovvalg(dataTable)
            medlemskap[0]?.lovvalg.shouldBe(lovvalgForventet)
        }

        Så<DataTable>("skal mappet periodeStatus i Medlemskap være") { dataTable: DataTable ->
            val periodeStatusForventet = MedlemskapDomeneSpraakParser.mapPeriodeStatus(dataTable)
            medlemskap[0]?.periodeStatus.shouldBe(periodeStatusForventet)
        }
    }

    private fun mapTilMedlDomene(): List<Medlemskap> {
        return mapMedlemskapResultat(medlBuilder.build())
    }
}
