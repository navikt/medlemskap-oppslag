package no.nav.medlemskap.cucumber.mapping.pdl

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.cucumber.BasisDomeneParser

class PdlDomenespråkParser : BasisDomeneParser() {
    fun <T> mapDataTable(dataTable: DataTable?, radMapper: RadMapper<T>): List<T> {
        if (dataTable == null) {
            return emptyList()
        }

        return dataTable.asMaps().map { radMapper.mapRad(it) }
    }

    interface RadMapper<T> {
        fun mapRad(rad: Map<String, String>): T
    }

    class StatsborgerskapMapper : RadMapper<HentPerson.Statsborgerskap> {
        override fun mapRad(rad: Map<String, String>): HentPerson.Statsborgerskap {

            return HentPerson.Statsborgerskap(
                parseString(Domenebegrep.LAND.nøkkel, rad),
                parseValgfriString(Domenebegrep.GYLDIG_FRA_OG_MED_DATO.nøkkel, rad),
                parseValgfriString(Domenebegrep.GYLDIG_TIL_OG_MED_DATO.nøkkel, rad)
            )
        }
    }

    class BostedsadresseMapper : RadMapper<HentPerson.Bostedsadresse> {
        override fun mapRad(rad: Map<String, String>): HentPerson.Bostedsadresse {

            return HentPerson.Bostedsadresse(
                angittFlyttedato = null,
                vegadresse = null,
                matrikkeladresse = null,
                ukjentBosted = null,
                folkeregistermetadata = HentPerson.Folkeregistermetadata2(
                    ajourholdstidspunkt = null,
                    gyldighetstidspunkt = parseValgfriString(Domenebegrep.FOLKE_REG_GYLDIGHETSTIDSPUNKT.nøkkel, rad),
                    opphoerstidspunkt = parseValgfriString(Domenebegrep.FOLKE_REG_OPPHOERSTIDSPUNKT.nøkkel, rad)
                )
            )
        }
    }

    class KontaktadresseMapper : RadMapper<HentPerson.Kontaktadresse> {
        override fun mapRad(rad: Map<String, String>): HentPerson.Kontaktadresse {
            val utenlandskLandkode = parseValgfriString(Domenebegrep.UTENLANDSK_ADRESSE_LANDKODE.nøkkel, rad)

            val utenlandskAdresse = if (utenlandskLandkode != null) {
                HentPerson.UtenlandskAdresse(utenlandskLandkode)
            } else {
                null
            }

            val utenlandskLandkodeFrittFormat = parseValgfriString(Domenebegrep.UTENLANDSK_ADRESSE_FRITT_FORMAT_LANDKODE.nøkkel, rad)

            val utenlandskAdresseFrittFormat = if (utenlandskLandkodeFrittFormat != null) {
                HentPerson.UtenlandskAdresseIFrittFormat(utenlandskLandkodeFrittFormat)
            } else {
                null
            }

            return HentPerson.Kontaktadresse(
                gyldigFraOgMed = parseValgfriString(Domenebegrep.GYLDIG_FRA_OG_MED.nøkkel, rad),
                gyldigTilOgMed = parseValgfriString(Domenebegrep.GYLDIG_TIL_OG_MED.nøkkel, rad),
                utenlandskAdresse = utenlandskAdresse,
                utenlandskAdresseIFrittFormat = utenlandskAdresseFrittFormat,
                folkeregistermetadata = null
            )
        }
    }

    class OppholdsadresseMapper : RadMapper<HentPerson.Oppholdsadresse> {
        override fun mapRad(rad: Map<String, String>): HentPerson.Oppholdsadresse {
            val utenlandskLandkode = parseValgfriString(Domenebegrep.UTENLANDSK_ADRESSE_LANDKODE.nøkkel, rad)

            val utenlandskAdresse = if (utenlandskLandkode != null) {
                HentPerson.UtenlandskAdresse2(utenlandskLandkode)
            } else {
                null
            }

            return HentPerson.Oppholdsadresse(
                gyldigFraOgMed = parseValgfriString(Domenebegrep.GYLDIG_FRA_OG_MED.nøkkel, rad),
                utenlandskAdresse = utenlandskAdresse,
                folkeregistermetadata = HentPerson.Folkeregistermetadata2(
                    ajourholdstidspunkt = null,
                    gyldighetstidspunkt = parseValgfriString(Domenebegrep.FOLKE_REG_GYLDIGHETSTIDSPUNKT.nøkkel, rad),
                    opphoerstidspunkt = parseValgfriString(Domenebegrep.FOLKE_REG_OPPHOERSTIDSPUNKT.nøkkel, rad)
                )
            )
        }
    }

    class SivilstandMapper : RadMapper<HentPerson.Sivilstand> {

        override fun mapRad(rad: Map<String, String>): HentPerson.Sivilstand {
            val sivilstandstype = HentPerson.Sivilstandstype.valueOf(parseString(Domenebegrep.SIVILSTANDSTYPE.nøkkel, rad))

            return HentPerson.Sivilstand(
                type = sivilstandstype,
                gyldigFraOgMed = parseValgfriString(Domenebegrep.GYLDIG_FRA_OG_MED.nøkkel, rad),
                relatertVedSivilstand = parseValgfriString(Domenebegrep.RELATERT_VED_SIVILSTAND.nøkkel, rad),
                folkeregistermetadata = null
            )
        }
    }

    class FamilerelasjonMapper : RadMapper<HentPerson.Familierelasjon> {

        override fun mapRad(rad: Map<String, String>): HentPerson.Familierelasjon {
            val relatertPersonsrolle = HentPerson.Familierelasjonsrolle.valueOf(parseString(Domenebegrep.RELATERT_PERSONS_ROLLE.nøkkel, rad))
            val minRolleForPersonStr = parseValgfriString(Domenebegrep.MIN_ROLLE_FOR_PERSON.nøkkel, rad)

            val minRolleForPerson = if (minRolleForPersonStr != null) {
                HentPerson.Familierelasjonsrolle.valueOf(minRolleForPersonStr)
            } else {
                null
            }

            return HentPerson.Familierelasjon(
                relatertPersonsIdent = parseString(Domenebegrep.RELATERT_PERSONS_IDENT.nøkkel, rad),
                relatertPersonsRolle = relatertPersonsrolle,
                minRolleForPerson = minRolleForPerson,
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
        MIN_ROLLE_FOR_PERSON("Min rolle for person"),
        RELATERT_PERSONS_IDENT("Relatert persons ident"),
        RELATERT_PERSONS_ROLLE("Relatert persons rolle"),
        RELATERT_VED_SIVILSTAND("Relatert ved sivilstand"),
        SIVILSTANDSTYPE("Type"),
        UTENLANDSK_ADRESSE_LANDKODE("Utenlandsk adresse landkode"),
        UTENLANDSK_ADRESSE_FRITT_FORMAT_LANDKODE("Utenlandsk adresse frittformat landkode")
    }
}
