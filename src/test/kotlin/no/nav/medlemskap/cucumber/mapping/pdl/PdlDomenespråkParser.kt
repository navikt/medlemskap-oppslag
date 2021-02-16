package no.nav.medlemskap.cucumber.mapping.pdl

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.cucumber.BasisDomeneParser
import no.nav.medlemskap.cucumber.Domenenøkkel
import no.nav.medlemskap.cucumber.RadMapper
import java.time.LocalDate

class PdlDomenespråkParser : BasisDomeneParser() {

    fun mapPdlParametre(dataTable: DataTable): PdlParametre {
        return mapDataTable(dataTable, PdlParametreMapper()).get(0)
    }

    fun mapStatsborgerskap(dataTable: DataTable): List<HentPerson.Statsborgerskap> {
        return mapDataTable(dataTable, StatsborgerskapMapper())
    }

    fun mapBostedsadresser(dataTable: DataTable): List<HentPerson.Bostedsadresse> {
        return mapDataTable(dataTable, BostedsadresseMapper())
    }

    fun mapKontaktadresser(dataTable: DataTable): List<HentPerson.Kontaktadresse> {
        return mapDataTable(dataTable, KontaktadresseMapper())
    }

    fun mapOppholdsadresser(dataTable: DataTable): List<HentPerson.Oppholdsadresse> {
        return mapDataTable(dataTable, OppholdsadresseMapper())
    }

    fun mapSivilstander(dataTable: DataTable): List<HentPerson.Sivilstand> {
        return mapDataTable(dataTable, SivilstandMapper())
    }

    fun mapFamilierelasjoner(dataTable: DataTable): List<HentPerson.Familierelasjon> {
        return mapDataTable(dataTable, FamilerelasjonMapper())
    }

    fun mapDoedsfall(dataTable: DataTable): List<HentPerson.Doedsfall> {
        return mapDataTable(dataTable, DoedsfallMapper())
    }

    class StatsborgerskapMapper : RadMapper<HentPerson.Statsborgerskap> {
        override fun mapRad(rad: Map<String, String>): HentPerson.Statsborgerskap {

            val historisk = parseValgfriBoolean(Domenebegrep.HISTORISK.nøkkel(), rad) ?: false

            return HentPerson.Statsborgerskap(
                parseString(Domenebegrep.LAND, rad),
                parseValgfriString(Domenebegrep.GYLDIG_FRA_OG_MED_DATO, rad),
                parseValgfriString(Domenebegrep.GYLDIG_TIL_OG_MED_DATO, rad),
                HentPerson.Metadata(historisk)
            )
        }
    }

    class BostedsadresseMapper : RadMapper<HentPerson.Bostedsadresse> {
        override fun mapRad(rad: Map<String, String>): HentPerson.Bostedsadresse {
            val utenlandskLandkode = parseValgfriString(Domenebegrep.UTENLANDSK_ADRESSE_LANDKODE, rad)

            val utenlandskAdresse = if (utenlandskLandkode != null) {
                HentPerson.UtenlandskAdresse(utenlandskLandkode)
            } else {
                null
            }

            return HentPerson.Bostedsadresse(
                gyldigFraOgMed = parseValgfriString(Domenebegrep.GYLDIG_FRA_OG_MED, rad),
                gyldigTilOgMed = parseValgfriString(Domenebegrep.GYLDIG_TIL_OG_MED, rad),
                utenlandskAdresse = utenlandskAdresse,
                angittFlyttedato = null,
                vegadresse = null,
                matrikkeladresse = null,
                ukjentBosted = null
            )
        }
    }

    class KontaktadresseMapper : RadMapper<HentPerson.Kontaktadresse> {
        override fun mapRad(rad: Map<String, String>): HentPerson.Kontaktadresse {
            val utenlandskLandkode = parseValgfriString(Domenebegrep.UTENLANDSK_ADRESSE_LANDKODE, rad)

            val utenlandskAdresse = if (utenlandskLandkode != null) {
                HentPerson.UtenlandskAdresse(utenlandskLandkode)
            } else {
                null
            }

            val utenlandskLandkodeFrittFormat = parseValgfriString(Domenebegrep.UTENLANDSK_ADRESSE_FRITT_FORMAT_LANDKODE, rad)

            val utenlandskAdresseFrittFormat = if (utenlandskLandkodeFrittFormat != null) {
                HentPerson.UtenlandskAdresseIFrittFormat(utenlandskLandkodeFrittFormat)
            } else {
                null
            }

            return HentPerson.Kontaktadresse(
                gyldigFraOgMed = parseValgfriString(Domenebegrep.GYLDIG_FRA_OG_MED, rad),
                gyldigTilOgMed = parseValgfriString(Domenebegrep.GYLDIG_TIL_OG_MED, rad),
                utenlandskAdresse = utenlandskAdresse,
                utenlandskAdresseIFrittFormat = utenlandskAdresseFrittFormat
            )
        }
    }

    class OppholdsadresseMapper : RadMapper<HentPerson.Oppholdsadresse> {
        override fun mapRad(rad: Map<String, String>): HentPerson.Oppholdsadresse {
            val utenlandskLandkode = parseValgfriString(Domenebegrep.UTENLANDSK_ADRESSE_LANDKODE, rad)

            val utenlandskAdresse = if (utenlandskLandkode != null) {
                HentPerson.UtenlandskAdresse(utenlandskLandkode)
            } else {
                null
            }

            return HentPerson.Oppholdsadresse(
                gyldigFraOgMed = parseValgfriString(Domenebegrep.GYLDIG_FRA_OG_MED, rad),
                gyldigTilOgMed = parseValgfriString(Domenebegrep.GYLDIG_TIL_OG_MED, rad),
                utenlandskAdresse = utenlandskAdresse
            )
        }
    }

    class PdlParametreMapper : RadMapper<PdlParametre> {
        override fun mapRad(rad: Map<String, String>): PdlParametre {
            return PdlParametre(parseDato(Domenebegrep.FØRSTE_DATO_FOR_YTELSE, rad))
        }
    }

    class SivilstandMapper : RadMapper<HentPerson.Sivilstand> {

        override fun mapRad(rad: Map<String, String>): HentPerson.Sivilstand {
            val sivilstandstype = HentPerson.Sivilstandstype.valueOf(parseString(Domenebegrep.SIVILSTANDSTYPE, rad))

            return HentPerson.Sivilstand(
                type = sivilstandstype,
                gyldigFraOgMed = parseValgfriString(Domenebegrep.GYLDIG_FRA_OG_MED, rad),
                bekreftelsesdato = parseValgfriString(Domenebegrep.BEKREFTELSESDATO, rad),
                relatertVedSivilstand = parseValgfriString(Domenebegrep.RELATERT_VED_SIVILSTAND, rad)
            )
        }
    }

    class FamilerelasjonMapper : RadMapper<HentPerson.Familierelasjon> {

        override fun mapRad(rad: Map<String, String>): HentPerson.Familierelasjon {
            val relatertPersonsrolle = HentPerson.Familierelasjonsrolle.valueOf(parseString(Domenebegrep.RELATERT_PERSONS_ROLLE, rad))
            val minRolleForPersonStr = parseValgfriString(Domenebegrep.MIN_ROLLE_FOR_PERSON, rad)

            val minRolleForPerson = if (minRolleForPersonStr != null) {
                HentPerson.Familierelasjonsrolle.valueOf(minRolleForPersonStr)
            } else {
                null
            }

            return HentPerson.Familierelasjon(
                relatertPersonsIdent = parseString(Domenebegrep.RELATERT_PERSONS_IDENT, rad),
                relatertPersonsRolle = relatertPersonsrolle,
                minRolleForPerson = minRolleForPerson,
                folkeregistermetadata = null
            )
        }
    }

    class DoedsfallMapper : RadMapper<HentPerson.Doedsfall> {

        override fun mapRad(rad: Map<String, String>): HentPerson.Doedsfall {
            return HentPerson.Doedsfall(
                doedsdato = parseValgfriString(Domenebegrep.DOEDSDATO, rad)
            )
        }
    }

    enum class Domenebegrep(val nøkkel: String) : Domenenøkkel {
        BEKREFTELSESDATO("Bekreftelsesdato"),
        DOEDSDATO("Dødsdato"),
        FØRSTE_DATO_FOR_YTELSE("Første dato for ytelse"),
        GYLDIG_FRA_OG_MED_DATO("Gyldig fra og med dato"),
        GYLDIG_TIL_OG_MED_DATO("Gyldig til og med dato"),
        HISTORISK("Historisk"),
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
        UTENLANDSK_ADRESSE_FRITT_FORMAT_LANDKODE("Utenlandsk adresse frittformat landkode");

        override fun nøkkel(): String {
            return nøkkel
        }
    }

    data class PdlParametre(val førsteDatoForYtelse: LocalDate)
}
