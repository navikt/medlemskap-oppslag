package no.nav.medlemskap.cucumber.ytelse

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.cucumber.BasisDomeneParser
import no.nav.medlemskap.cucumber.Domenenøkkel
import no.nav.medlemskap.cucumber.RadMapper
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Kontrollperiode
import java.time.LocalDate

class YtelseDomenespråkParser : BasisDomeneParser() {

    fun mapKontrollperiode(dataTable: DataTable): Kontrollperiode {
        return mapDataTable(dataTable, KontrollperiodeMapper()).get(0)
    }

    fun mapParametreKontrollperiode(dataTable: DataTable): ParametreKontrollperiode {
        return mapDataTable(dataTable, ParametreKontrollperiodeMapper()).get(0)
    }

    class KontrollperiodeMapper : RadMapper<Kontrollperiode> {
        override fun mapRad(rad: Map<String, String>): Kontrollperiode {
            return Kontrollperiode(
                parseDato(Domenebegrep.FRA_OG_MED_DATO, rad),
                parseDato(Domenebegrep.TIL_OG_MED_DATO, rad)
            )
        }
    }

    class ParametreKontrollperiodeMapper : RadMapper<ParametreKontrollperiode> {
        override fun mapRad(rad: Map<String, String>): ParametreKontrollperiode {
            return ParametreKontrollperiode(
                InputPeriode(
                    parseDato(Domenebegrep.FRA_OG_MED_DATO, rad),
                    parseDato(Domenebegrep.TIL_OG_MED_DATO, rad)
                ),
                parseValgfriDato(Domenebegrep.FØRSTE_DAG_FOR_YTELSE, rad)
            )
        }
    }

    data class ParametreKontrollperiode(val iputPeriode: InputPeriode, val førsteDagForYtelse: LocalDate?)

    enum class Domenebegrep(val nøkkel: String) : Domenenøkkel {
        FRA_OG_MED_DATO("Fra og med dato"),
        FØRSTE_DAG_FOR_YTELSE("Første dag for ytelse"),
        TIL_OG_MED_DATO("Til og med dato");

        override fun nøkkel(): String {
            return nøkkel
        }
    }
}
