package no.nav.medlemskap.cucumber.mapping.pdl

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.cucumber.BasisDomeneParser

class PdlDomenespråkParser : BasisDomeneParser() {
    fun <T> mapDataTable(dataTable: DataTable?, radMapper: PdlRadMapper<T>): List<T> {
        if (dataTable == null) {
            return emptyList()
        }

        return dataTable.asMaps().map { radMapper.mapRad(this, it) }
    }
}

interface PdlRadMapper<T> {
    fun mapRad(domenespråkParser: PdlDomenespråkParser, rad: Map<String, String>): T
}

class PdlStatsborgerskapMapper : PdlRadMapper<HentPerson.Statsborgerskap> {
    override fun mapRad(domenespråkParser: PdlDomenespråkParser, rad: Map<String, String>): HentPerson.Statsborgerskap {

        return HentPerson.Statsborgerskap(
            domenespråkParser.parseString(Domenebegrep.LAND.nøkkel, rad),
            domenespråkParser.parseValgfriString(Domenebegrep.GYLDIG_FRA_OG_MED_DATO.nøkkel, rad),
            domenespråkParser.parseValgfriString(Domenebegrep.GYLDIG_TIL_OG_MED_DATO.nøkkel, rad)
        )
    }
}

enum class Domenebegrep(val nøkkel: String) {
    GYLDIG_FRA_OG_MED_DATO("Gyldig fra og med dato"),
    GYLDIG_TIL_OG_MED_DATO("Gyldig til og med dato"),
    LAND("Land"),
}
