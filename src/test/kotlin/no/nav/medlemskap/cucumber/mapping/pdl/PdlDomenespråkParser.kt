package no.nav.medlemskap.cucumber.mapping.pdl

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.cucumber.BasisDomeneParser

class PdlDomenespråkParser : BasisDomeneParser() {
    fun <T> mapDataTable(dataTable: DataTable?, radMapper: RadMapper<T>): List<T> {
        if (dataTable == null) {
            return emptyList()
        }

        return dataTable.asMaps().map { radMapper.mapRad(this, it) }
    }

    interface RadMapper<T> {
        fun mapRad(domenespråkParser: PdlDomenespråkParser, rad: Map<String, String>): T
    }

    class StatsborgerskapMapper : RadMapper<HentPerson.Statsborgerskap> {
        override fun mapRad(domenespråkParser: PdlDomenespråkParser, rad: Map<String, String>): HentPerson.Statsborgerskap {

            return HentPerson.Statsborgerskap(
                domenespråkParser.parseString(Domenebegrep.LAND.nøkkel, rad),
                domenespråkParser.parseValgfriString(Domenebegrep.GYLDIG_FRA_OG_MED_DATO.nøkkel, rad),
                domenespråkParser.parseValgfriString(Domenebegrep.GYLDIG_TIL_OG_MED_DATO.nøkkel, rad)
            )
        }
    }

    class BostedsadresseMapper : RadMapper<HentPerson.Bostedsadresse> {
        override fun mapRad(domenespråkParser: PdlDomenespråkParser, rad: Map<String, String>): HentPerson.Bostedsadresse {

            return HentPerson.Bostedsadresse(
                angittFlyttedato = null,
                vegadresse = null,
                matrikkeladresse = null,
                ukjentBosted = null,
                folkeregistermetadata = HentPerson.Folkeregistermetadata2(
                    ajourholdstidspunkt = null,
                    gyldighetstidspunkt = domenespråkParser.parseValgfriString(Domenebegrep.FOLKE_REG_GYLDIGHETSTIDSPUNKT.nøkkel, rad),
                    opphoerstidspunkt = domenespråkParser.parseValgfriString(Domenebegrep.FOLKE_REG_OPPHOERSTIDSPUNKT.nøkkel, rad)
                )
            )
        }
    }

    class KontaktadresseMapper : RadMapper<HentPerson.Kontaktadresse> {
        override fun mapRad(domenespråkParser: PdlDomenespråkParser, rad: Map<String, String>): HentPerson.Kontaktadresse {
            val utenlandskLandkode = domenespråkParser.parseValgfriString(Domenebegrep.UTENLANDSK_ADRESSE_LANDKODE.nøkkel, rad)

            val utenlandskAdresse = if (utenlandskLandkode != null) {
                HentPerson.UtenlandskAdresse(utenlandskLandkode)
            } else {
                null
            }

            val utenlandskLandkodeFrittFormat = domenespråkParser.parseValgfriString(Domenebegrep.UTENLANDSK_ADRESSE_FRITT_FORMAT_LANDKODE.nøkkel, rad)

            val utenlandskAdresseFrittFormat = if (utenlandskLandkodeFrittFormat != null) {
                HentPerson.UtenlandskAdresseIFrittFormat(utenlandskLandkodeFrittFormat)
            } else {
                null
            }

            return HentPerson.Kontaktadresse(
                gyldigFraOgMed = domenespråkParser.parseValgfriString(Domenebegrep.GYLDIG_FRA_OG_MED.nøkkel, rad),
                gyldigTilOgMed = domenespråkParser.parseValgfriString(Domenebegrep.GYLDIG_TIL_OG_MED.nøkkel, rad),
                utenlandskAdresse = utenlandskAdresse,
                utenlandskAdresseIFrittFormat = utenlandskAdresseFrittFormat,
                folkeregistermetadata = null
            )
        }
    }

    enum class Domenebegrep(val nøkkel: String) {
        GYLDIG_FRA_OG_MED_DATO("Gyldig fra og med dato"),
        GYLDIG_TIL_OG_MED_DATO("Gyldig til og med dato"),
        GYLDIG_FRA_OG_MED("Gyldig fra og med"),
        GYLDIG_TIL_OG_MED("Gyldig til og med"),
        FOLKE_REG_GYLDIGHETSTIDSPUNKT("Folkeregistermetadata gyldighetstidspunkt"),
        FOLKE_REG_OPPHOERSTIDSPUNKT("Folkeregistermetadata opphoerstidspunkt"),
        LAND("Land"),
        UTENLANDSK_ADRESSE_LANDKODE("Utenlandsk adresse landkode"),
        UTENLANDSK_ADRESSE_FRITT_FORMAT_LANDKODE("Utenlandsk adresse frittformat landkode")
    }
}
