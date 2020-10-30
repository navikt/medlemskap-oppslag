package no.nav.medlemskap.cucumber.mapping.pdl.aareg

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.clients.aareg.AaRegOpplysningspliktigArbeidsgiverType
import no.nav.medlemskap.clients.aareg.AaRegPeriode
import no.nav.medlemskap.clients.ereg.Status
import no.nav.medlemskap.cucumber.BasisDomeneParser
import no.nav.medlemskap.cucumber.Domenenøkkel
import no.nav.medlemskap.cucumber.RadMapper

class AaregDomenespraakParser : BasisDomeneParser() {
    fun mapPeriode(dataTable: DataTable?): AaRegPeriode {
        return mapDataTable(dataTable, PeriodeMapper())[0]
    }

    fun mapArbeidsgiverType(dataTable: DataTable?): AaRegOpplysningspliktigArbeidsgiverType {
        return mapDataTable(dataTable, ArbeidsgiverTypeMapper())[0]
    }

    fun mapArbeidsforholdsType(dataTable: DataTable?): String {
        return mapDataTable(dataTable, ArbeidsforholdTypeMappe())[0]
    }

    fun mapType(dataTable: DataTable?): String {
        return mapDataTable(dataTable, TypeMapper())[0]
    }

    fun mapOrganisasjonsnummer(dataTable: DataTable?): String {
        return mapDataTable(dataTable, OrganisasjonsnummerMapper())[0]
    }

    fun mapAntallAnsatte(dataTable: DataTable?): Int {
        return mapDataTable(dataTable, AntallAnsattMapper())[0]
    }

    fun mapStatuser(dataTable: DataTable?): List<Status> {
        return mapDataTable(dataTable, StatusMapper())
    }

    class AntallAnsattMapper : RadMapper<Int> {
        override fun mapRad(rad: Map<String, String>): Int {
            return parseInt(Domenebegrep.ANTALL_ANSATTE, rad)
        }
    }

    class OrganisasjonsnummerMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(Domenebegrep.ORGANISASJONSNUMMER, rad)
        }
    }

    class TypeMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(Domenebegrep.TYPE, rad)
        }
    }

    class PeriodeMapper : RadMapper<AaRegPeriode> {
        override fun mapRad(rad: Map<String, String>): AaRegPeriode {
            return AaRegPeriode(
                parseValgfriDato(Domenebegrep.GYLDIG_FRA_OG_MED_DATO, rad),
                parseValgfriDato(Domenebegrep.GYLDIG_TIL_OG_MED_DATO, rad)
            )
        }
    }

    class ArbeidsforholdTypeMappe() : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(Domenebegrep.ARBEIDSFORHOLDSTYPE, rad)
        }
    }

    class ArbeidsgiverTypeMapper : RadMapper<AaRegOpplysningspliktigArbeidsgiverType> {
        override fun mapRad(rad: Map<String, String>): AaRegOpplysningspliktigArbeidsgiverType {
            return AaRegOpplysningspliktigArbeidsgiverType.valueOf(parseString(Domenebegrep.ARBEIDSGIVERTYPE, rad))
        }
    }
    enum class Domenebegrep(val nøkkel: String) : Domenenøkkel {
        ANTALL_ANSATTE("Antall"),
        ARBEIDSGIVERTYPE("Arbeidsgivertype"),
        ARBEIDSFORHOLDSTYPE("Type"),
        GYLDIG_FRA_OG_MED_DATO("Gyldig fra og med dato"),
        GYLDIG_TIL_OG_MED_DATO("Gyldig til og med dato"),
        ORGANISASJONSNUMMER("Organisasjonsnummer"),
        TYPE("Type"),
        STATUS("Konkurstatus"),
        GYLDIG_FRA_OG_MED("Gyldig fra og med"),
        GYLDIG_TIL_OG_MED("Gyldig til og med"),
        FOLKE_REG_GYLDIGHETSTIDSPUNKT("Folkeregistermetadata gyldighetstidspunkt"),
        FOLKE_REG_OPPHOERSTIDSPUNKT("Folkeregistermetadata opphoerstidspunkt");

        override fun nøkkel(): String {
            return nøkkel
        }
    }
}
