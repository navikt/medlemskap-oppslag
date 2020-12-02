package no.nav.medlemskap.cucumber

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.cucumber.SpraakParserDomene.ArbeidDomenebegrep
import no.nav.medlemskap.domene.Arbeidsforholdstype
import no.nav.medlemskap.domene.Fartsomraade
import no.nav.medlemskap.domene.PermisjonPermitteringType
import no.nav.medlemskap.domene.Skipsregister
import no.nav.medlemskap.regler.common.Datohjelper
import java.time.LocalDate

abstract class BasisDomeneParser {

    companion object {
        fun parseDato(domenebegrep: Domenenøkkel, rad: Map<String, String>): LocalDate {
            return parseDato(domenebegrep.nøkkel(), rad)
        }

        fun parseValgfriDato(domenebegrep: Domenenøkkel, rad: Map<String, String?>): LocalDate? {
            return parseValgfriDato(domenebegrep.nøkkel(), rad)
        }

        fun parseString(domenebegrep: Domenenøkkel, rad: Map<String, String>): String {
            return verdi(domenebegrep.nøkkel(), rad)
        }

        fun parseFartsområde(rad: Map<String, String>): Fartsomraade? {
            val verdi = valgfriVerdi(ArbeidDomenebegrep.FARTSOMRÅDE.nøkkel, rad)
            return if (verdi == null) null else Fartsomraade.valueOf(verdi)
        }

        fun parseSkipsregister(rad: Map<String, String>): Skipsregister? {
            val verdi = valgfriVerdi(ArbeidDomenebegrep.SKIPSREGISTER.nøkkel, rad)
            return if (verdi == null) null else Skipsregister.valueOf(verdi)
        }

        fun parsePermisjonPermitteringType(rad: Map<String, String>): PermisjonPermitteringType {
            val verdi = verdi(ArbeidDomenebegrep.PERMITTERINGSTYPE.nøkkel, rad)
            return PermisjonPermitteringType.valueOf(verdi)
        }

        fun parseArbeidsforholdstype(rad: Map<String, String>): Arbeidsforholdstype {
            val verdi = verdi(ArbeidDomenebegrep.ARBEIDSFORHOLDSTYPE.nøkkel, rad)
            return Arbeidsforholdstype.valueOf(verdi)
        }

        fun parseValgfriString(domenebegrep: Domenenøkkel, rad: Map<String, String>): String? {
            return valgfriVerdi(domenebegrep.nøkkel(), rad)
        }

        fun parseBoolean(domenebegrep: Domenenøkkel, rad: Map<String, String>): Boolean {
            val verdi = verdi(domenebegrep.nøkkel(), rad)

            return when (verdi) {
                "Ja" -> true
                else -> false
            }
        }

        fun parseDato(domenebegrep: String, rad: Map<String, String>): LocalDate {
            return Datohjelper.parseDato(verdi(domenebegrep, rad))
        }

        fun parseValgfriDato(domenebegrep: String, rad: Map<String, String?>): LocalDate? {
            if (rad.get(domenebegrep) == null || rad.get(domenebegrep) == "") {
                return null
            }

            return Datohjelper.parseDato(rad.get(domenebegrep)!!)
        }

        fun verdi(nøkkel: String, rad: Map<String, String>): String {
            val verdi = rad.get(nøkkel)

            if (verdi == null || verdi == "") {
                throw java.lang.RuntimeException("Fant ingen verdi for $nøkkel")
            }

            return verdi
        }

        fun valgfriVerdi(nøkkel: String, rad: Map<String, String>): String? {
            val verdi = rad.get(nøkkel)

            return verdi
        }

        fun parseInt(domenebegrep: Domenenøkkel, rad: Map<String, String>): Int {
            val verdi = verdi(domenebegrep.nøkkel(), rad)

            return Integer.parseInt(verdi)
        }

        fun parseDouble(domenebegrep: Domenenøkkel, rad: Map<String, String>): Double {
            val verdi = verdi(domenebegrep.nøkkel(), rad)
            return verdi.toDouble()
        }

        fun parseValgfriDouble(domenebegrep: Domenenøkkel, rad: Map<String, String>): Double? {
            return valgfriVerdi(domenebegrep.nøkkel(), rad)?.toDouble() ?: return null
        }

        fun parseValgfriInt(domenebegrep: Domenenøkkel, rad: Map<String, String>): Int? {
            val verdi = valgfriVerdi(domenebegrep.nøkkel(), rad)
            if (verdi == null) {
                return null
            }

            return parseInt(domenebegrep, rad)
        }

        fun <T> mapDataTable(dataTable: DataTable?, radMapper: RadMapper<T>): List<T> {
            if (dataTable == null) {
                return emptyList()
            }

            return dataTable.asMaps().map { radMapper.mapRad(it) }
        }
    }
}

interface RadMapper<T> {
    fun mapRad(rad: Map<String, String>): T
}
