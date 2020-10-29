package no.nav.medlemskap.cucumber.mapping.pdl.aareg

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.clients.aareg.AaRegPeriode
import no.nav.medlemskap.cucumber.BasisDomeneParser
import no.nav.medlemskap.cucumber.Domenenøkkel
import no.nav.medlemskap.cucumber.RadMapper

class AaregDomenespraakParser : BasisDomeneParser() {
    fun mapPeriode(dataTable: DataTable?): AaRegPeriode {
        return mapDataTable(dataTable, PeriodeMapper())[0]
    }

    class PeriodeMapper : RadMapper<AaRegPeriode> {
        override fun mapRad(rad: Map<String, String>): AaRegPeriode {

            return AaRegPeriode(
                parseValgfriDato(Domenebegrep.GYLDIG_FRA_OG_MED_DATO, rad),
                parseValgfriDato(Domenebegrep.GYLDIG_TIL_OG_MED_DATO, rad)
            )
        }
    }
    enum class Domenebegrep(val nøkkel: String) : Domenenøkkel {
        GYLDIG_FRA_OG_MED_DATO("Gyldig fra og med dato"),
        GYLDIG_TIL_OG_MED_DATO("Gyldig til og med dato"),
        GYLDIG_FRA_OG_MED("Gyldig fra og med"),
        GYLDIG_TIL_OG_MED("Gyldig til og med"),
        FOLKE_REG_GYLDIGHETSTIDSPUNKT("Folkeregistermetadata gyldighetstidspunkt"),
        FOLKE_REG_OPPHOERSTIDSPUNKT("Folkeregistermetadata opphoerstidspunkt");

        override fun nøkkel(): String {
            return nøkkel
        }
    }
}
