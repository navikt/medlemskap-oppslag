package no.nav.medlemskap.cucumber.SpraakParserDomene

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.cucumber.*

object OppgaveDomeneSpraakParser : BasisDomeneParser() {
    fun mapJournalpostId(dataTable: DataTable?): String {
        return mapDataTable(dataTable, JournalPostIdMapper())[0]
    }

    fun mapJournalstatus(dataTable: DataTable?): String {
        return mapDataTable(dataTable, JournalStatusMapper())[0]
    }

    fun mapDokumentInfoId(dataTable: DataTable?): String {
        return mapDataTable(dataTable, DokumentInfoIdMapper())[0]
    }

    fun mapTittel(dataTable: DataTable?): String {
        return mapDataTable(dataTable, TittelMapper())[0]
    }

    fun mapTema(dataTable: DataTable?): String {
        return mapDataTable(dataTable, TemaMapper())[0]
    }

    fun mapJournalposttype(dataTable: DataTable?): String {
        return mapDataTable(dataTable, JournalpostTypeMapper())[0]
    }

    class JournalpostTypeMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(DokumentDomenebegrep.JOURNALPOST_TYPE, rad)
        }
    }

    class TemaMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(DokumentDomenebegrep.TEMA, rad)
        }
    }

    class DokumentInfoIdMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(DokumentDomenebegrep.DOKUMENT_INFO_ID, rad)
        }
    }

    class TittelMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(DokumentDomenebegrep.TITTEL, rad)
        }
    }

    class JournalStatusMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(DokumentDomenebegrep.JOURNAL_STATUS, rad)
        }
    }

    class JournalPostIdMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(DokumentDomenebegrep.JOURNAL_POST_ID, rad)
        }
    }

    enum class DokumentDomenebegrep(val nøkkel: String) : Domenenøkkel {
        DOKUMENT_INFO_ID("DokumentInfoId"),
        JOURNAL_STATUS("Journalstatus"),
        JOURNALPOST_TYPE("JournalpostType"),
        JOURNAL_POST_ID("JournalpostId"),
        PROSENT("Prosent"),
        RELATERT_VED_SIVILSTAND("Relatert ved sivilstand"),
        TEMA("Tema"),
        TITTEL("Tittel");

        override fun nøkkel(): String {
            return nøkkel
        }
    }
}
