package no.nav.medlemskap.cucumber.mapping.saf

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.clients.saf.generated.Dokumenter
import no.nav.medlemskap.cucumber.BasisDomeneParser
import no.nav.medlemskap.cucumber.Domenenøkkel
import no.nav.medlemskap.cucumber.RadMapper

class SafDomeneSpraakParser : BasisDomeneParser() {
    fun mapDokumentInfoId(dataTable: DataTable?): String {
        return mapDataTable(dataTable, DokumentInfoIdMapper())[0]
    }

    fun mapJournalStatus(dataTable: DataTable?): Dokumenter.Journalstatus {
        return mapDataTable(dataTable, JournalStatusMapper())[0]
    }

    fun mapDokumentTittel(dataTable: DataTable?): String {
        return mapDataTable(dataTable, DokumentTittelMapper())[0]
    }

    fun mapJournalPostId(dataTable: DataTable?): String {
        return mapDataTable(dataTable, JournalPostIdMapper())[0]
    }

    fun mapJournalTema(dataTable: DataTable?): Dokumenter.Tema {
        return mapDataTable(dataTable, JournalTemaMappeer())[0]
    }

    fun mapJournalPostType(dataTable: DataTable?): Dokumenter.Journalposttype {
        return mapDataTable(dataTable, JournalPostTypeMapper())[0]
    }

    class JournalPostTypeMapper() : RadMapper<Dokumenter.Journalposttype> {
        override fun mapRad(rad: Map<String, String>): Dokumenter.Journalposttype {
            return Dokumenter.Journalposttype.valueOf(parseString(Domenebegrep.JOURNAL_POST_TYPE, rad))
        }
    }

    class JournalTemaMappeer() : RadMapper<Dokumenter.Tema> {
        override fun mapRad(rad: Map<String, String>): Dokumenter.Tema {
            return Dokumenter.Tema.valueOf(parseString(Domenebegrep.TEMA, rad))
        }
    }

    class JournalStatusMapper() : RadMapper<Dokumenter.Journalstatus> {
        override fun mapRad(rad: Map<String, String>): Dokumenter.Journalstatus {
            return Dokumenter.Journalstatus.valueOf(parseString(Domenebegrep.JOURNAL_STATUS, rad))
        }
    }

    class JournalPostIdMapper() : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(Domenebegrep.JOURNAL_POST_ID, rad)
        }
    }

    class DokumentTittelMapper() : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(Domenebegrep.TITTEL, rad)
        }
    }

    class DokumentInfoIdMapper() : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(Domenebegrep.DOKUMENT_INFO_ID, rad)
        }
    }
}

enum class Domenebegrep(val nøkkel: String) : Domenenøkkel {
    DOKUMENT_INFO_ID("DokumentInfoId"),
    JOURNAL_POST_ID("JournalpostId"),
    JOURNAL_POST_TYPE("Journalposttype"),
    JOURNAL_STATUS("Journalstatus"),
    TEMA("Tema"),
    TITTEL("Tittel")
    ;
    override fun nøkkel(): String {
        return nøkkel
    }
}
