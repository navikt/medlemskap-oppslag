package no.nav.medlemskap.cucumber.steps.saf

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import io.kotest.matchers.shouldBe
import no.nav.medlemskap.clients.saf.generated.dokumenter.DokumentInfo
import no.nav.medlemskap.clients.saf.generated.dokumenter.Sak
import no.nav.medlemskap.clients.saf.generated.enums.Journalposttype
import no.nav.medlemskap.clients.saf.generated.enums.Journalstatus
import no.nav.medlemskap.clients.saf.generated.enums.Tema
import no.nav.medlemskap.cucumber.SpraakParserDomene.DokumentDomeneSpraakParser
import no.nav.medlemskap.cucumber.mapping.saf.SafDomeneSpraakParser
import no.nav.medlemskap.domene.Dokument
import no.nav.medlemskap.domene.Journalpost
import no.nav.medlemskap.services.saf.mapJournalResultat
import java.time.LocalDateTime

class SafMapperSteps : No {

    private val safDomenespråkParser = SafDomeneSpraakParser()
    private val safDokumentBuilder = SafDokumentBuilder()
    private var dokumentInfoBuilder = DokumentInfoBuilder()
    private var dokument: Dokument? = null
    private var dokumenter: List<Journalpost>? = null

    init {
        Gitt<DataTable>("følgende tittel fra journalpost") { dataTable: DataTable ->
            safDokumentBuilder.tittel = safDomenespråkParser.mapDokumentTittel(dataTable)
        }

        Gitt<DataTable>("følgende dokumentInfoId fra dokument") { dataTable: DataTable ->
            dokumentInfoBuilder.dokumentInfoId = safDomenespråkParser.mapDokumentInfoId(dataTable)
        }

        Gitt<DataTable>("følgende fagsakId fra dokument") { dataTable: DataTable ->
            safDokumentBuilder.sak = Sak(safDomenespråkParser.mapFagsakId(dataTable))
        }

        Gitt<DataTable>("følgende tittel fra dokument") { dataTable: DataTable ->
            dokumentInfoBuilder.tittel = safDomenespråkParser.mapDokumentTittel(dataTable)
        }

        Gitt<DataTable>("følgende journalpostId fra journalpost") { dataTable: DataTable ->
            safDokumentBuilder.journalpostId = safDomenespråkParser.mapJournalPostId(dataTable)
        }

        Gitt<DataTable>("følgende journalført av navn fra journalpost") { dataTable: DataTable ->
            safDokumentBuilder.journalfortAvNavn = safDomenespråkParser.mapJournalfortAvNavn(dataTable)
        }

        Gitt<DataTable>("følgende journalposttype fra journalpost") { dataTable: DataTable ->
            safDokumentBuilder.journalposttype = safDomenespråkParser.mapJournalPostType(dataTable)
        }

        Gitt<DataTable>("følgende tema fra journalpost") { dataTable: DataTable ->
            safDokumentBuilder.tema = safDomenespråkParser.mapJournalTema(dataTable)
        }

        Gitt<DataTable>("følgende journalstatus fra journalpost") { dataTable: DataTable ->
            safDokumentBuilder.journalStatus = safDomenespråkParser.mapJournalStatus(dataTable)
        }

        Når("journalposter mappes") {
            dokumenter = mapTilDokumenter()
        }

        Så<DataTable>("skal mappede journalpostid være") { dataTable: DataTable ->
            val journalpostIdForventet = DokumentDomeneSpraakParser.mapJournalpostId(dataTable)
            dokumenter?.get(0)?.journalpostId.shouldBe(journalpostIdForventet)
        }

        Så<DataTable>("skal mappede journalført av navn være") { dataTable: DataTable ->
            val forventetJournalfortAvNavn = DokumentDomeneSpraakParser.mapJournalfortAvNavn(dataTable)
            dokumenter?.get(0)?.journalfortAvNavn.shouldBe(forventetJournalfortAvNavn)
        }

        Så<DataTable>("skal mappede journalstatus være") { dataTable: DataTable ->
            val journalstatusForventet = DokumentDomeneSpraakParser.mapJournalstatus(dataTable)
            dokumenter?.get(0)?.journalstatus.shouldBe(journalstatusForventet)
        }

        Så<DataTable>("skal mappede dokumentInfoId være") { dataTable: DataTable ->
            val dokumentInfoIdForventet = DokumentDomeneSpraakParser.mapDokumentInfoId(dataTable)
            dokumenter?.get(0)?.dokumenter?.get(0)?.dokumentId.shouldBe(dokumentInfoIdForventet)
        }

        Så("skal mappede fagsakId være") { dataTable: DataTable ->
            val forventetFagsakId = DokumentDomeneSpraakParser.mapFagsakId(dataTable)
            dokumenter?.get(0)?.sak?.fagsakId.shouldBe(forventetFagsakId)
        }

        Så<DataTable>("mappede tittel være") { dataTable: DataTable ->
            val tittelForventet = DokumentDomeneSpraakParser.mapTittel(dataTable)
            dokumenter?.get(0)?.dokumenter?.get(0)?.tittel.shouldBe(tittelForventet)
        }

        Så<DataTable>("skal mappede tema være") { dataTable: DataTable ->
            val temaForventet = DokumentDomeneSpraakParser.mapTema(dataTable)
            dokumenter?.get(0)?.tema.shouldBe(temaForventet)
        }

        Så<DataTable>("skal mappede tittel i journalpost være") { dataTable: DataTable ->
            val tittelForventet = DokumentDomeneSpraakParser.mapTittel(dataTable)
            dokumenter?.get(0)?.tittel.shouldBe(tittelForventet)
        }

        Så<DataTable>("skal mappede journalposttype være") { dataTable: DataTable ->
            val journalposttypeForventet = DokumentDomeneSpraakParser.mapJournalposttype(dataTable)
            dokumenter?.get(0)?.journalposttype.shouldBe(journalposttypeForventet)
        }
    }

    private fun mapTilDokumenter(): List<Journalpost> {
        safDokumentBuilder.dokumenter = listOf(dokumentInfoBuilder.build())
        return mapJournalResultat(listOf(safDokumentBuilder.build()))
    }

    class SafDokumentBuilder {

        var dokumentInfoBuilder = DokumentInfoBuilder()

        var tema = Tema.AAP
        var journalpostId = String()
        var journalfortAvNavn = String()
        var journalStatus = Journalstatus.FERDIGSTILT
        var journalposttype = Journalposttype.I
        var datoOpprettet = LocalDateTime.now().toString()
        var dokumenter = listOf<DokumentInfo?>(dokumentInfoBuilder.build())
        var tittel = String()
        var fagsakId = ""
        var sak = no.nav.medlemskap.clients.saf.generated.dokumenter.Sak(fagsakId)

        fun build(): no.nav.medlemskap.clients.saf.generated.dokumenter.Journalpost {
            return no.nav.medlemskap.clients.saf.generated.dokumenter.Journalpost(
                tema = tema,
                journalpostId = journalpostId,
                journalfortAvNavn = journalfortAvNavn,
                journalstatus = journalStatus,
                journalposttype = journalposttype,
                dokumenter = dokumenter,
                datoOpprettet = datoOpprettet,
                tittel = tittel,
                sak = sak
            )
        }
    }

    class DokumentInfoBuilder {
        var dokumentInfoId = String()
        var tittel = String()
        fun build(): DokumentInfo? {
            return DokumentInfo(
                dokumentInfoId = dokumentInfoId,
                tittel = tittel
            )
        }
    }
}
