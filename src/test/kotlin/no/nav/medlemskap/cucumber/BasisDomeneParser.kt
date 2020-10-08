package no.nav.medlemskap.cucumber

import io.cucumber.datatable.DataTable
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

        fun parseString(domenebegrep: String, rad: Map<String, String>): String {
            return verdi(domenebegrep, rad)
        }

        fun parseValgfriString(domenebegrep: String, rad: Map<String, String>): String? {
            return valgfriVerdi(domenebegrep, rad)
        }

        fun parseBoolean(domenebegrep: String, rad: Map<String, String>): Boolean {
            val verdi = verdi(domenebegrep, rad)

            return when (verdi) {
                "Ja" -> true
                else -> false
            }
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

        fun parseInt(domenebegrep: String, rad: Map<String, String>): Int {
            val verdi = verdi(domenebegrep, rad)

            return Integer.parseInt(verdi)
        }

        fun parseDouble(domenebegrep: String, rad: Map<String, String>): Double {
            val verdi = verdi(domenebegrep, rad)
            return verdi.toDouble()
        }

        fun parseValgfriInt(domenebegrep: String, rad: Map<String, String>): Int? {
            val verdi = valgfriVerdi(domenebegrep, rad)
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
