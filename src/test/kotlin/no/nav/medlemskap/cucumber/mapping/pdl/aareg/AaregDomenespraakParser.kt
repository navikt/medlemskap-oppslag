package no.nav.medlemskap.cucumber.mapping.pdl.aareg

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.clients.aareg.AaRegBruksperiode
import no.nav.medlemskap.clients.aareg.AaRegGyldighetsperiode
import no.nav.medlemskap.clients.aareg.AaRegOpplysningspliktigArbeidsgiverType
import no.nav.medlemskap.clients.aareg.AaRegPeriode
import no.nav.medlemskap.clients.ereg.Ansatte
import no.nav.medlemskap.clients.ereg.Bruksperiode
import no.nav.medlemskap.clients.ereg.Gyldighetsperiode
import no.nav.medlemskap.clients.ereg.Status
import no.nav.medlemskap.cucumber.BasisDomeneParser
import no.nav.medlemskap.cucumber.Domenenøkkel
import no.nav.medlemskap.cucumber.RadMapper
import no.nav.medlemskap.regler.common.Datohjelper
import java.time.YearMonth

class AaregDomenespraakParser : BasisDomeneParser() {
    fun mapPeriode(dataTable: DataTable?): AaRegPeriode {
        return mapDataTable(dataTable, PeriodeMapper())[0]
    }

    fun mapRapporteringsperiode(dataTable: DataTable?): YearMonth {
        return mapDataTable(dataTable, RapporteringsperiodeMapper())[0]
    }

    fun mapArbeidsgiverType(dataTable: DataTable?): AaRegOpplysningspliktigArbeidsgiverType {
        return mapDataTable(dataTable, ArbeidsgiverTypeMapper())[0]
    }

    fun mapArbeidsforholdsType(dataTable: DataTable?): String {
        return mapDataTable(dataTable, ArbeidsforholdTypeMappe())[0]
    }

    fun mapLandkode(dataTable: DataTable?): String {
        return mapDataTable(dataTable, LandkodeMapper())[0]
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

    fun mapBruksPeriode(dataTable: DataTable?): AaRegBruksperiode {
        return mapDataTable(dataTable, BruksperiodeMapper())[0]
    }

    fun mapGyldighetsPeriode(dataTable: DataTable?): AaRegGyldighetsperiode {
        return mapDataTable(dataTable, GyldighetsperiodeMapper())[0]
    }

    fun mapYrkeskode(dataTable: DataTable?): String {
        return mapDataTable(dataTable, YrkeskodeMapper())[0]
    }
    fun mapSkipsregister(dataTable: DataTable?): String {
        return mapDataTable(dataTable, SkipsregisterMapper())[0]
    }

    fun mapStillingsprosent(dataTable: DataTable?): Double {
        return mapDataTable(dataTable, StillingsprosentMapper())[0]
    }

    fun mapBeregnetAntallTimer(dataTable: DataTable?): Double {
        return mapDataTable(dataTable, BeregnetAntallTimerMapper())[0]
    }

    class BeregnetAntallTimerMapper : RadMapper<Double> {
        override fun mapRad(rad: Map<String, String>): Double {
            return parseDouble(Domenebegrep.BEREGNET_ANTALL_TIMER, rad)
        }
    }
    class StillingsprosentMapper : RadMapper<Double> {
        override fun mapRad(rad: Map<String, String>): Double {
            return parseDouble(Domenebegrep.STILLINGSPROSENT, rad)
        }
    }

    class SkipsregisterMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(Domenebegrep.SKIPSREGISTER, rad)
        }
    }

    class YrkeskodeMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(Domenebegrep.YRKESKODE, rad)
        }
    }

    class GyldighetsperiodeMapper : RadMapper<AaRegGyldighetsperiode> {
        override fun mapRad(rad: Map<String, String>): AaRegGyldighetsperiode {
            return AaRegGyldighetsperiode(
                parseValgfriDato(Domenebegrep.GYLDIG_FRA_OG_MED_DATO, rad)!!,
                parseValgfriDato(Domenebegrep.GYLDIG_TIL_OG_MED_DATO, rad)
            )
        }
    }

    class BruksperiodeMapper : RadMapper<AaRegBruksperiode> {
        override fun mapRad(rad: Map<String, String>): AaRegBruksperiode {
            return AaRegBruksperiode(
                Datohjelper.parseIsoDatoTid(parseString(Domenebegrep.GYLDIG_FRA_OG_MED_DATO, rad))!!,
                Datohjelper.parseIsoDatoTid(parseString(Domenebegrep.GYLDIG_TIL_OG_MED_DATO, rad))
            )
        }
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

    class RapporteringsperiodeMapper : RadMapper<YearMonth> {
        override fun mapRad(rad: Map<String, String>): YearMonth {
            return YearMonth.parse(parseString(Domenebegrep.RAPPORTERINGSPERIODE, rad))
        }
    }

    class OrganisasjonsnummerMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(Domenebegrep.ORGANISASJONSNUMMER, rad)
        }
    }

    class LandkodeMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(Domenebegrep.LANDKODE, rad)
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
        BEREGNET_ANTALL_TIMER("BeregnetAntallTimerPrUke"),
        BRUKSPERIODE_GYLDIG_FRA("Bruksperiode gyldig fra"),
        BRUKSPERIODE_GYLDIG_TIL("Bruksperiode gyldig til"),
        ENHETSTYPE("Enhetstype"),
        GYLDIG_FRA_OG_MED_DATO("Gyldig fra og med dato"),
        GYLDIG_TIL_OG_MED_DATO("Gyldig til og med dato"),
        GYLDIGHETSPERIODE_FRA_OG_MED("Gyldighetsperiode gyldig fra"),
        GYLDIGHETSPERIODE_TIL_OG_MED("Bruksperiode gyldig til"),
        LANDKODE("Landkode"),
        ORGANISASJONSNUMMER("Organisasjonsnummer"),
        TYPE("Type"),
        SKIPSREGISTER("Skipsregister"),
        STATUS("Konkurstatus"),
        STILLINGSPROSENT("Stillingsprosent"),
        RAPPORTERINGSPERIODE("Rapporteringsperiode"),
        YRKESKODE("Yrke"),
        GYLDIG_FRA_OG_MED("Gyldig fra og med"),
        GYLDIG_TIL_OG_MED("Gyldig til og med"),
        FOLKE_REG_GYLDIGHETSTIDSPUNKT("Folkeregistermetadata gyldighetstidspunkt"),
        FOLKE_REG_OPPHOERSTIDSPUNKT("Folkeregistermetadata opphoerstidspunkt");

        override fun nøkkel(): String {
            return nøkkel
        }
    }
}
