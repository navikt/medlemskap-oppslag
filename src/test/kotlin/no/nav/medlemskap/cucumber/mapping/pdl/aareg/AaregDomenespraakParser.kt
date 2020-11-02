package no.nav.medlemskap.cucumber.mapping.pdl.aareg

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.clients.aareg.AaRegOpplysningspliktigArbeidsgiverType
import no.nav.medlemskap.clients.aareg.AaRegPeriode
import no.nav.medlemskap.clients.ereg.Ansatte
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

    fun mapEnhetstype(dataTable: DataTable?): String {
        return mapDataTable(dataTable, EnhetstypeMapper())[0]
    }

    fun mapOrganisasjonsnummer(dataTable: DataTable?): String {
        return mapDataTable(dataTable, OrganisasjonsnummerMapper())[0]
    }

    fun mapAntallAnsatte(dataTable: DataTable?): List<Ansatte> {
        return mapDataTable(dataTable, AntallAnsattMapper())
    }

    fun mapStatuser(dataTable: DataTable?): List<Status> {
        return mapDataTable(dataTable, StatusMapper())
    }

    class StatusMapper : RadMapper<Status> {
        override fun mapRad(rad: Map<String, String>): Status {
            return Status(
                null,
                null,
                parseString(Domenebegrep.STATUS, rad)
            )
        }
    }

    class AntallAnsattMapper : RadMapper<Ansatte> {
        override fun mapRad(rad: Map<String, String>): Ansatte {
            return Ansatte(
                parseInt(Domenebegrep.ANTALL_ANSATTE, rad),
                no.nav.medlemskap.clients.ereg.Bruksperiode(
                    parseDato(Domenebegrep.BRUKSPERIODE_GYLDIG_FRA, rad),
                    parseDato(Domenebegrep.BRUKSPERIODE_GYLDIG_TIL, rad)
                ),
                no.nav.medlemskap.clients.ereg.Gyldighetsperiode(
                    parseDato(no.nav.medlemskap.cucumber.Domenebegrep.GYLDIGHETSPERIODE_FRA_OG_MED, rad),
                    parseDato(no.nav.medlemskap.cucumber.Domenebegrep.GYLDIGHETSPERIODE_TIL_OG_MED, rad)
                )
            )
        }
    }

    class OrganisasjonsnummerMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(Domenebegrep.ORGANISASJONSNUMMER, rad)
        }
    }

    class EnhetstypeMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(Domenebegrep.ENHETSTYPE, rad)
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
        BRUKSPERIODE_GYLDIG_FRA("Bruksperiode gyldig fra"),
        BRUKSPERIODE_GYLDIG_TIL("Bruksperiode gyldig til"),
        ENHETSTYPE("Enhetstype"),
        GYLDIG_FRA_OG_MED_DATO("Gyldig fra og med dato"),
        GYLDIG_TIL_OG_MED_DATO("Gyldig til og med dato"),
        GYLDIGHETSPERIODE_FRA_OG_MED("Gyldighetsperiode gyldig fra"),
        GYLDIGHETSPERIODE_TIL_OG_MED("Bruksperiode gyldig til"),
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
