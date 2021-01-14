package no.nav.medlemskap.cucumber.SpraakParserDomene

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.cucumber.BasisDomeneParser
import no.nav.medlemskap.cucumber.Domenenøkkel
import no.nav.medlemskap.cucumber.RadMapper
import no.nav.medlemskap.cucumber.mapping.udi.UdiDomenebegrep
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Datohjelper
import java.time.LocalDateTime

object OppholdstillatelseDomeneSpraakParser : BasisDomeneParser() {

    fun mapArbeidstilgang(dataTable: DataTable): Boolean {
        return mapDataTable(dataTable, HarArbeidstilgangMapper())[0]
    }

    fun mapforesporselfodselsnummer(dataTable: DataTable): String {
        return mapDataTable(dataTable, ForesporselfodselsnummerMapper())[0]
    }

    fun mapUttrekkstidspunkt(dataTable: DataTable): LocalDateTime? {
        return mapDataTable(dataTable, UttrekkstidspunktMapper())[0]
    }

    fun mapArbeidsomfangKategori(dataTable: DataTable): ArbeidomfangKategori {
        return mapDataTable(dataTable, ArbeidsomfangKategoriMapper())[0]
    }

    fun mapArbeidsadgangType(dataTable: DataTable): ArbeidsadgangType {
        return mapDataTable(dataTable, ArbeidsadgangTypeMapper())[0]
    }

    fun mapPeriode(dataTable: DataTable): Periode {
        return mapDataTable(dataTable, PeriodeMapper())[0]
    }

    fun mapArbeidsadgang(dataTable: DataTable): Arbeidsadgang {
        return mapDataTable(dataTable, ArbeidsadgangMapper())[0]
    }

    fun mapOppholdstillatelse(dataTable: DataTable): Oppholdstillatelse {
        return mapDataTable(dataTable, OppholdstillatelseMapper())[0]
    }

    class PeriodeMapper() : RadMapper<Periode> {
        override fun mapRad(rad: Map<String, String>): Periode {
            return Periode(
                fom = parseDato(OppholdstillatelseDomenebegrep.GYLDIG_FRA_OG_MED, rad),
                tom = parseDato(OppholdstillatelseDomenebegrep.GYLDIG_TIL_OG_MED, rad)
            )
        }
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

    class ArbeidsadgangTypeMapper() : RadMapper<ArbeidsadgangType> {
        override fun mapRad(rad: Map<String, String>): ArbeidsadgangType {
            return ArbeidsadgangType.valueOf(parseString(OppholdstillatelseDomenebegrep.ARBEIDSADGANG_TYPE, rad))
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

    class OppholdstillatelseMapper : RadMapper<Oppholdstillatelse> {
        override fun mapRad(rad: Map<String, String>): Oppholdstillatelse {
            val periode = Periode(
                parseValgfriDato(UdiDomenebegrep.GYLDIG_FRA_OG_MED, rad),
                parseValgfriDato(UdiDomenebegrep.GYLDIG_TIL_OG_MED, rad)
            )

            return Oppholdstillatelse(
                null,
                "fnr",
                parseValgfriBoolean(OppholdstillatelseDomenebegrep.AVGJOERELSE.nøkkel(), rad),
                OppholdstillatelsePaSammeVilkar(periode, parseBoolean(OppholdstillatelseDomenebegrep.HAR_OPPHOLDSTILLATELSE, rad)),
                Arbeidsadgang(periode, true, null, null),
                parseValgfriBoolean(OppholdstillatelseDomenebegrep.UAVKLART_FLYKTNINGSTATUS.nøkkel(), rad),
                parseValgfriBoolean(OppholdstillatelseDomenebegrep.HAR_FLYKTNINGSTATUS.nøkkel(), rad)

            )
        }
    }

    class ArbeidsadgangMapper : RadMapper<Arbeidsadgang> {
        override fun mapRad(rad: Map<String, String>): Arbeidsadgang {
            val periode = Periode(
                parseValgfriDato(UdiDomenebegrep.GYLDIG_FRA_OG_MED, rad),
                parseValgfriDato(UdiDomenebegrep.GYLDIG_TIL_OG_MED, rad)
            )

            return Arbeidsadgang(
                periode,
                parseBoolean(UdiDomenebegrep.ARBEIDSADGANG, rad),
                ArbeidsadgangTypeMapper().mapRad(rad),
                ArbeidsomfangKategoriMapper().mapRad(rad)
            )
        }
    }
}

enum class OppholdstillatelseDomenebegrep(val nøkkel: String) : Domenenøkkel {
    ARBEIDSADGANG("Arbeidsadgang"),
    ARBEIDOMFANG_KATEGORI("ArbeidomfangKategori"),
    ARBEIDSADGANG_TYPE("ArbeidsadgangType"),
    AVGJOERELSE("Avgjørelse"),
    HAR_OPPHOLDSTILLATELSE("Har tillatelse"),
    HAR_FLYKTNINGSTATUS("Har flyktningstatus"),
    GYLDIG_FRA_OG_MED("Gyldig fra og med"),
    GYLDIG_TIL_OG_MED("Gyldig til og med"),
    FORESPORSELSFODSELSNUMMER("Foresporselsfodselsnummer"),
    UAVKLART_FLYKTNINGSTATUS("Uavklart flyktningstatus"),
    UTTREKKSTIDSPUNKT("Uttrekkstidspunkt")
    ;

    override fun nøkkel(): String {
        return nøkkel
    }
}