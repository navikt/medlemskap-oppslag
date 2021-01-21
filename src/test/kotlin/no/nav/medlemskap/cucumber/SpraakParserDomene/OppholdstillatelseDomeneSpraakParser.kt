package no.nav.medlemskap.cucumber.SpraakParserDomene

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.cucumber.BasisDomeneParser
import no.nav.medlemskap.cucumber.Domenenøkkel
import no.nav.medlemskap.cucumber.RadMapper
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Datohjelper
import java.time.LocalDate
import java.time.LocalDateTime

object OppholdstillatelseDomeneSpraakParser : BasisDomeneParser() {

    fun mapHarArbeidstilgang(dataTable: DataTable): Boolean {
        return mapDataTable(dataTable, HarArbeidstilgangMapper())[0]
    }

    fun mapJaNeiUavklart(dataTable: DataTable): JaNeiUavklart {
        return mapDataTable(dataTable, JaNeiUavklartMapper())[0]
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

    fun mapAvgjorelseDato(dataTable: DataTable): LocalDate {
        return mapDataTable(dataTable, AvgjorelseDatoMapper())[0]
    }

    fun mapOvrigIkkeOppholdsKategori(dataTable: DataTable): OvrigIkkeOppholdsKategori {
        return mapDataTable(dataTable, OvrigIkkeOppholdsKategoriMapper())[0]
    }

    fun mapPeriode(dataTable: DataTable): Periode {
        return mapDataTable(dataTable, PeriodeMapper())[0]
    }

    fun mapOppholdstillatelse(dataTable: DataTable): Oppholdstillatelse {
        return mapDataTable(dataTable, OppholdstillatelseMapper())[0]
    }

    fun mapArbeidstilgang(dataTable: DataTable): Arbeidsadgang {
        return mapDataTable(dataTable, ArbeidsadgangMapper())[0]
    }

    fun mapOppholdstillatelsePaSammeVilkar(dataTable: DataTable): OppholdstillatelsePaSammeVilkar {
        return mapDataTable(dataTable, HarOppholdMapper())[0]
    }

    fun mapUavklart(dataTable: DataTable): Uavklart {
        return mapDataTable(dataTable, UavklartMapper())[0]
    }

    class UavklartMapper() : RadMapper<Uavklart> {
        override fun mapRad(rad: Map<String, String>): Uavklart {
            return Uavklart(
                parseBooleanMedBooleanVerdi(OppholdstillatelseDomenebegrep.UAVKLART, rad)
            )
        }
    }

    class JaNeiUavklartMapper() : RadMapper<JaNeiUavklart> {
        override fun mapRad(rad: Map<String, String>): JaNeiUavklart {
            return JaNeiUavklart.valueOf(parseString(OppholdstillatelseDomenebegrep.JA_NEI_UAVKLART, rad))
        }
    }

    class PeriodeMapper() : RadMapper<Periode> {
        override fun mapRad(rad: Map<String, String>): Periode {
            return Periode(
                fom = parseDato(OppholdstillatelseDomenebegrep.GYLDIG_FRA_OG_MED, rad),
                tom = parseDato(OppholdstillatelseDomenebegrep.GYLDIG_TIL_OG_MED, rad)
            )
        }
    }

    class OvrigIkkeOppholdsKategoriMapper() : RadMapper<OvrigIkkeOppholdsKategori> {
        override fun mapRad(rad: Map<String, String>): OvrigIkkeOppholdsKategori {
            return OvrigIkkeOppholdsKategori.valueOf(parseString(OppholdstillatelseDomenebegrep.OVRIG_IKKE_OPPHOLD_KATEGORI, rad))
        }
    }

    class AvgjorelseDatoMapper() : RadMapper<LocalDate> {
        override fun mapRad(rad: Map<String, String>): LocalDate {
            return parseDato(OppholdstillatelseDomenebegrep.AVGJORELSEDATO, rad)
        }
    }

    class HarOppholdMapper() : RadMapper<OppholdstillatelsePaSammeVilkar> {
        override fun mapRad(rad: Map<String, String>): OppholdstillatelsePaSammeVilkar {
            return OppholdstillatelsePaSammeVilkar(
                periode = Periode(
                    fom = parseDato(OppholdstillatelseDomenebegrep.GYLDIG_FRA_OG_MED, rad),
                    tom = parseDato(OppholdstillatelseDomenebegrep.GYLDIG_TIL_OG_MED, rad)
                ),
                harTillatelse = parseBooleanMedBooleanVerdi(OppholdstillatelseDomenebegrep.HAR_OPPHOLD, rad),
                type = OppholdstillaelsePaSammeVilkarType.valueOf(parseString(OppholdstillatelseDomenebegrep.OPPHOLDSTILLATELSE_TYPE, rad))

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
                parseValgfriDato(OppholdstillatelseDomenebegrep.GYLDIG_FRA_OG_MED, rad),
                parseValgfriDato(OppholdstillatelseDomenebegrep.GYLDIG_TIL_OG_MED, rad)
            )

            return Oppholdstillatelse(
                null,
                "fnr",
                parseValgfriBoolean(OppholdstillatelseDomenebegrep.AVGJOERELSE.nøkkel(), rad),
                GjeldendeOppholdsstatus(
                    oppholdstillatelsePaSammeVilkar = OppholdstillatelsePaSammeVilkar(
                        periode = periode,
                        harTillatelse = parseBoolean(OppholdstillatelseDomenebegrep.HAR_OPPHOLDSTILLATELSE, rad),
                        type = OppholdstillaelsePaSammeVilkarType.valueOf(parseString(OppholdstillatelseDomenebegrep.OPPHOLDSTILLATELSE_TYPE, rad))
                    ),
                    ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum = null,
                    eosellerEFTAOpphold = null,
                    uavklart = null

                ),
                Arbeidsadgang(periode, true, null, null),
                parseValgfriBoolean(OppholdstillatelseDomenebegrep.UAVKLART_FLYKTNINGSTATUS.nøkkel(), rad),
                parseValgfriBoolean(OppholdstillatelseDomenebegrep.HAR_FLYKTNINGSTATUS.nøkkel(), rad)

            )
        }
    }

    class ArbeidsadgangMapper : RadMapper<Arbeidsadgang> {
        override fun mapRad(rad: Map<String, String>): Arbeidsadgang {
            val periode = Periode(
                parseValgfriDato(OppholdstillatelseDomenebegrep.GYLDIG_FRA_OG_MED, rad),
                parseValgfriDato(OppholdstillatelseDomenebegrep.GYLDIG_TIL_OG_MED, rad)
            )

            return Arbeidsadgang(
                periode,
                parseBoolean(OppholdstillatelseDomenebegrep.ARBEIDSADGANG, rad),
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
    AVGJORELSEDATO("Avgjorelsesdato"),
    HAR_OPPHOLD("Har opphold"),
    HAR_OPPHOLDSTILLATELSE("Har tillatelse"),
    HAR_FLYKTNINGSTATUS("Har flyktningstatus"),
    JA_NEI_UAVKLART("JaNeiUavklart"),
    OVRIG_IKKE_OPPHOLD_KATEGORI("OvrigIkkeOppholdsKategori"),
    GYLDIG_FRA_OG_MED("Gyldig fra og med"),
    GYLDIG_TIL_OG_MED("Gyldig til og med"),
    OPPHOLDSTILLATELSE_TYPE("Type"),
    FORESPORSELSFODSELSNUMMER("Foresporselsfodselsnummer"),
    UAVKLART("Uavklart"),
    UAVKLART_FLYKTNINGSTATUS("Uavklart flyktningstatus"),
    UTTREKKSTIDSPUNKT("Uttrekkstidspunkt")
    ;

    override fun nøkkel(): String {
        return nøkkel
    }
}
