package no.nav.medlemskap.cucumber.mapping.udi

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.cucumber.BasisDomeneParser
import no.nav.medlemskap.cucumber.BasisDomeneParser.Companion.mapDataTable
import no.nav.medlemskap.cucumber.Domenenøkkel
import no.nav.medlemskap.cucumber.RadMapper
import no.udi.mt_1067_nav_data.v1.ArbeidOmfangKategori
import no.udi.mt_1067_nav_data.v1.ArbeidsadgangType
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

class UdiDomeneSpraakParser {
    fun mapHarArbeidsadgang(dataTable: DataTable?): JaNeiUavklart {
        return mapDataTable(dataTable, HarArbeidsadgangMapper())[0]
    }

    fun mapArbeidsomfang(dataTable: DataTable?): ArbeidOmfangKategori {
        return mapDataTable(dataTable, ArbeidsomfangTypeMapper())[0]
    }

    fun mapforesporselfodselsnummer(dataTable: DataTable?): String {
        return mapDataTable(dataTable, ForesporselfodselsnummerMapper())[0]
    }

    fun mapArbeidsadgangType(dataTable: DataTable?): ArbeidsadgangType {
        return mapDataTable(dataTable, ArbeidsadgangTypeMapper())[0]
    }

    fun mapUttrekkstidspunkt(dataTable: DataTable?): XMLGregorianCalendar ? {
        return mapDataTable(dataTable, UttrekkstidspunktMapper())[0]
    }

    class HarArbeidsadgangMapper() : RadMapper<JaNeiUavklart> {
        override fun mapRad(rad: Map<String, String>): JaNeiUavklart {
            return JaNeiUavklart.valueOf(BasisDomeneParser.parseString(UdiDomenebegrep.ARBEIDSADGANG, rad))
        }
    }

    class ArbeidsomfangTypeMapper() : RadMapper<ArbeidOmfangKategori> {
        override fun mapRad(rad: Map<String, String>): ArbeidOmfangKategori {
            return ArbeidOmfangKategori.valueOf(BasisDomeneParser.parseString(UdiDomenebegrep.ARBEIDOMFANG_KATEGORI, rad))
        }
    }

    class ForesporselfodselsnummerMapper() : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return BasisDomeneParser.parseString(UdiDomenebegrep.FORESPORSELSFODSELSNUMMER, rad)
        }
    }

    class ArbeidsadgangTypeMapper() : RadMapper<ArbeidsadgangType> {
        override fun mapRad(rad: Map<String, String>): ArbeidsadgangType {
            return ArbeidsadgangType.valueOf(BasisDomeneParser.parseString(UdiDomenebegrep.ARBEIDSADGANG_TYPE, rad))
        }
    }

    class UttrekkstidspunktMapper() : RadMapper<XMLGregorianCalendar> {
        override fun mapRad(rad: Map<String, String>): XMLGregorianCalendar {

            val uttrekkstidpunktLocalDateTime = BasisDomeneParser.parseString(UdiDomenebegrep.UTTREKKSTIDSPUNKT, rad)
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(uttrekkstidpunktLocalDateTime)
        }
    }
}

enum class UdiDomenebegrep(val nøkkel: String) : Domenenøkkel {
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
