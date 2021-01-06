package no.nav.medlemskap.cucumber.SpraakParserDomene

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.cucumber.BasisDomeneParser
import no.nav.medlemskap.cucumber.Domenenøkkel
import no.nav.medlemskap.cucumber.RadMapper
import no.nav.medlemskap.domene.ArbeidomfangKategori
import no.nav.medlemskap.domene.ArbeidsAdgangType
import no.nav.medlemskap.regler.common.Datohjelper
import java.time.LocalDateTime

object OppholdstillatelseDomeneSpraakParser : BasisDomeneParser() {

    fun mapArbeidstilgang(dataTable: DataTable?): Boolean {
        return mapDataTable(dataTable, HarArbeidstilgangMapper())[0]
    }

    fun mapforesporselfodselsnummer(dataTable: DataTable?): String {
        return mapDataTable(dataTable, ForesporselfodselsnummerMapper())[0]
    }

    fun mapUttrekkstidspunkt(dataTable: DataTable?): LocalDateTime? {
        return mapDataTable(dataTable, UttrekkstidspunktMapper())[0]
    }

    fun mapArbeidsomfangKategori(dataTable: DataTable?): ArbeidomfangKategori {
        return mapDataTable(dataTable, ArbeidsomfangKategoriMapper())[0]
    }

    fun mapArbeidsadgangType(dataTable: DataTable?): ArbeidsAdgangType {
        return mapDataTable(dataTable, ArbeidsadgangTypeMapper())[0]
    }

    class ArbeidsomfangKategoriMapper() : RadMapper<ArbeidomfangKategori> {
        override fun mapRad(rad: Map<String, String>): ArbeidomfangKategori {
            return ArbeidomfangKategori.valueOf(parseString(OppholdstillatelseDomenebegrep.ARBEIDOMFANG_KATEGORI, rad))
        }
    }

    class ForesporselfodselsnummerMapper() : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(OppholdstillatelseDomenebegrep.FORESPORSELSFODSELSNUMMER, rad)
        }
    }

    class ArbeidsadgangTypeMapper() : RadMapper<ArbeidsAdgangType> {
        override fun mapRad(rad: Map<String, String>): ArbeidsAdgangType {
            return ArbeidsAdgangType.valueOf(parseString(OppholdstillatelseDomenebegrep.ARBEIDSADGANG_TYPE, rad))
        }
    }

    class UttrekkstidspunktMapper() : RadMapper<LocalDateTime?> {
        override fun mapRad(rad: Map<String, String>): LocalDateTime? {
            return Datohjelper.parseIsoDatoTid(parseString(OppholdstillatelseDomenebegrep.UTTREKKSTIDSPUNKT, rad))
        }
    }

    class HarArbeidstilgangMapper() : RadMapper<Boolean> {
        override fun mapRad(rad: Map<String, String>): Boolean {
            return parseBooleanMedBooleanVerdi(OppholdstillatelseDomenebegrep.ARBEIDSADGANG, rad)
        }
    }
}

enum class OppholdstillatelseDomenebegrep(val nøkkel: String) : Domenenøkkel {
    ARBEIDSADGANG("Arbeidsadgang"),
    ARBEIDOMFANG_KATEGORI("ArbeidomfangKategori"),
    ARBEIDSADGANG_TYPE("ArbeidsadgangType"),
    FORESPORSELSFODSELSNUMMER("Foresporselsfodselsnummer"),
    UTTREKKSTIDSPUNKT("Uttrekkstidspunkt")
    ;

    override fun nøkkel(): String {
        return nøkkel
    }
}
