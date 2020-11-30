package no.nav.medlemskap.cucumber.SpraakParserDomene

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.cucumber.*
import no.nav.medlemskap.cucumber.Domenebegrep
import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.Familierelasjon
import no.nav.medlemskap.domene.Sivilstand
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import java.time.LocalDate

object PersonhistorikkDomeneSpraakParser : BasisDomeneParser() {

    fun mapStatsborgerskap(dataTable: DataTable?): List<Statsborgerskap> {
        return mapDataTable(dataTable, StatsborgerskapMapper())
    }

    fun mapAdresser(dataTable: DataTable?): List<Adresse> {
        return mapDataTable(dataTable, AdresseMapper())
    }

    fun mapSivilstander(dataTable: DataTable?): List<Sivilstand> {
        return mapDataTable(dataTable, SivilstandMapper())
    }

    fun mapDoedsfall(dataTable: DataTable?): List<LocalDate> {
        return mapDataTable(dataTable, DoedsfallMapper())
    }

    fun mapFamilierelasjoner(dataTable: DataTable?): List<Familierelasjon> {
        return mapDataTable(dataTable, FamilieRelasjonMapper())
    }

    fun mapPersonhistorikkBarn(dataTable: DataTable?): List<DataOmBarn> {
        return mapDataTable(dataTable, PersonhistorikkBarnMapper())
    }

    fun mapPersonhistorikkEktefelle(dataTable: DataTable?): List<PersonhistorikkEktefelle> {
        return mapDataTable(dataTable, PersonhistorikkEktefelleMapper())
    }

    fun mapBarnTilEktefelle(dataTable: DataTable?): List<String> {
        return mapDataTable(dataTable, BarnTilEktefelleMapper())
    }

    class StatsborgerskapMapper : RadMapper<Statsborgerskap> {
        override fun mapRad(rad: Map<String, String>): Statsborgerskap {
            return Statsborgerskap(
                parseString(PersonhistorikkDomenebegrep.LANDKODE, rad),
                parseValgfriDato(PersonhistorikkDomenebegrep.FRA_OG_MED_DATO, rad),
                parseValgfriDato(PersonhistorikkDomenebegrep.TIL_OG_MED_DATO, rad)
            )
        }
    }

    class SivilstandMapper : RadMapper<Sivilstand> {
        override fun mapRad(rad: Map<String, String>): Sivilstand {
            return Sivilstand(
                type = DomenespråkParser.parseSivilstandstype(Domenebegrep.SIVILSTANDSTYPE, rad),
                gyldigFraOgMed = parseValgfriDato(PersonhistorikkDomenebegrep.GYLDIG_FRA_OG_MED, rad),
                gyldigTilOgMed = parseValgfriDato(PersonhistorikkDomenebegrep.GYLDIG_TIL_OG_MED, rad),
                relatertVedSivilstand = parseValgfriString(PersonhistorikkDomenebegrep.RELATERT_VED_SIVILSTAND, rad)
            )
        }
    }

    class DoedsfallMapper : RadMapper<LocalDate> {
        override fun mapRad(rad: Map<String, String>): LocalDate {
            return parseDato(PersonhistorikkDomenebegrep.DOEDSDATO, rad)
        }
    }

    class AdresseMapper : RadMapper<Adresse> {
        override fun mapRad(rad: Map<String, String>): Adresse {
            return Adresse(
                parseString(PersonhistorikkDomenebegrep.LANDKODE, rad),
                parseValgfriDato(PersonhistorikkDomenebegrep.FRA_OG_MED_DATO, rad),
                parseValgfriDato(PersonhistorikkDomenebegrep.TIL_OG_MED_DATO, rad)
            )
        }
    }

    class FamilieRelasjonMapper : RadMapper<Familierelasjon> {
        override fun mapRad(rad: Map<String, String>): Familierelasjon {
            return Familierelasjon(
                relatertPersonsIdent = parseString(PersonhistorikkDomenebegrep.RELATERT_PERSONS_IDENT, rad),
                relatertPersonsRolle = DomenespråkParser.parseRolle(Domenebegrep.RELATERT_PERSONS_ROLLE, rad),
                minRolleForPerson = DomenespråkParser.parseValgfriRolle(Domenebegrep.MIN_ROLLE_FOR_PERSON, rad),
                folkeregistermetadata = null
            )
        }
    }

    class BarnTilEktefelleMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return String()
        }
    }

    class PersonhistorikkEktefelleMapper : RadMapper<PersonhistorikkEktefelle> {
        override fun mapRad(rad: Map<String, String>): PersonhistorikkEktefelle {
            val fraOgMedDato = parseValgfriDato(PersonhistorikkDomenebegrep.FRA_OG_MED_DATO, rad)
            val tilOgMedDato = parseValgfriDato(PersonhistorikkDomenebegrep.TIL_OG_MED_DATO, rad)

            val bostedsadresser = mutableListOf<Adresse>()
            val bostedsadresse = parseValgfriString(PersonhistorikkDomenebegrep.BOSTED, rad)
            if (bostedsadresse != null) {
                bostedsadresser.add(Adresse(bostedsadresse, fraOgMedDato, tilOgMedDato))
            }

            val kontaktadresser = mutableListOf<Adresse>()
            val kontaktadresse = parseValgfriString(PersonhistorikkDomenebegrep.KONTAKTADRESSE, rad)
            if (kontaktadresse != null) {
                kontaktadresser.add(Adresse(kontaktadresse, fraOgMedDato, tilOgMedDato))
            }

            val oppholdsadresser = mutableListOf<Adresse>()
            val oppholdsadresse = parseValgfriString(PersonhistorikkDomenebegrep.OPPHOLDSADRESSE, rad)
            if (oppholdsadresse != null) {
                oppholdsadresser.add(Adresse(oppholdsadresse, fraOgMedDato, tilOgMedDato))
            }

            return PersonhistorikkEktefelle(
                ident = parseString(Domenebegrep.IDENT, rad),
                barn = mutableListOf<String>(),
                bostedsadresser = bostedsadresser,
                kontaktadresser = kontaktadresser,
                oppholdsadresser = oppholdsadresser

            )
        }
    }

    class PersonhistorikkBarnMapper : RadMapper<DataOmBarn> {
        override fun mapRad(rad: Map<String, String>): DataOmBarn {
            val fraOgMedDato = parseValgfriDato(PersonhistorikkDomenebegrep.FRA_OG_MED_DATO, rad)
            val tilOgMedDato = parseValgfriDato(PersonhistorikkDomenebegrep.TIL_OG_MED_DATO, rad)

            val bostedsadresser = mutableListOf<Adresse>()
            val bostedsadresse = parseValgfriString(PersonhistorikkDomenebegrep.BOSTED, rad)
            if (bostedsadresse != null) {
                bostedsadresser.add(Adresse(bostedsadresse, fraOgMedDato, tilOgMedDato))
            }

            val kontaktadresser = mutableListOf<Adresse>()
            val kontaktadresse = parseValgfriString(PersonhistorikkDomenebegrep.KONTAKTADRESSE, rad)
            if (kontaktadresse != null) {
                kontaktadresser.add(Adresse(kontaktadresse, fraOgMedDato, tilOgMedDato))
            }

            val oppholdsadresser = mutableListOf<Adresse>()
            val oppholdsadresse = parseValgfriString(PersonhistorikkDomenebegrep.OPPHOLDSADRESSE, rad)
            if (oppholdsadresse != null) {
                oppholdsadresser.add(Adresse(oppholdsadresse, fraOgMedDato, tilOgMedDato))
            }

            return DataOmBarn(
                PersonhistorikkBarn(
                    ident = parseString(PersonhistorikkDomenebegrep.IDENT, rad),
                    familierelasjoner = mutableListOf<Familierelasjon>(),
                    bostedsadresser = bostedsadresser,
                    kontaktadresser = kontaktadresser,
                    oppholdsadresser = oppholdsadresser

                )
            )
        }
    }
}

enum class PersonhistorikkDomenebegrep(val nøkkel: String) : Domenenøkkel {
    ADRESSE("Adresse"),
    BOSTED("Bosted"),
    DOEDSDATO("Dødsdato"),
    FRA_OG_MED_DATO("Fra og med dato"),
    GYLDIG_FRA_OG_MED("Gyldig fra og med dato"),
    GYLDIG_TIL_OG_MED("Gyldig til og med dato"),
    IDENT("Ident"),
    KONTAKTADRESSE("Kontaktadresse"),
    LANDKODE("Landkode"),
    OPPHOLDSADRESSE("Oppholdsadresse"),
    PROSENT("Prosent"),
    RELATERT_PERSONS_IDENT("Relatert persons ident"),
    RELATERT_VED_SIVILSTAND("Relatert ved sivilstand"),
    TIL_OG_MED_DATO("Til og med dato");

    override fun nøkkel(): String {
        return nøkkel
    }
}
