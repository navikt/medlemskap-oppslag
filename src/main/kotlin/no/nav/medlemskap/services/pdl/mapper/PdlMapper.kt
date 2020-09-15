package no.nav.medlemskap.services.pdl.mapper

import com.neovisionaries.i18n.CountryCode
import mu.KotlinLogging
import no.nav.medlemskap.clients.pdl.generated.HentFoedselsaar
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.common.exceptions.DetteSkalAldriSkje
import no.nav.medlemskap.common.exceptions.PersonIkkeFunnet
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.services.pdl.PdlSivilstandMapper.mapSivilstander
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object PdlMapper {

    fun mapTilPersonHistorikkTilBruker(person: HentPerson.Person): Personhistorikk {

        val statsborgerskap: List<Statsborgerskap> = person.statsborgerskap.map { mapStatsborgerskap(it) }
        val bostedsadresser: List<Adresse> = person.bostedsadresse.map { mapBostedsadresse(it) }
        val kontaktadresser: List<Adresse> = person.kontaktadresse.map { mapKontaktAdresse(it) }
        val oppholdsadresser: List<Adresse> = mapOppholdsAdresse(person.oppholdsadresse)
        val sivilstand: List<Sivilstand> = mapSivilstander(person.sivilstand)
        val familierelasjoner: List<Familierelasjon> = person.familierelasjoner.map { mapFamilierelasjon(it) }
        val personstatuser: List<FolkeregisterPersonstatus> = emptyList()

        return Personhistorikk(
            statsborgerskap = statsborgerskap,
            personstatuser = personstatuser,
            bostedsadresser = bostedsadresser,
            sivilstand = sivilstand,
            familierelasjoner = familierelasjoner,
            kontaktadresser = kontaktadresser,
            oppholdsadresser = oppholdsadresser
        )
    }

    fun mapOppholdsAdresse(oppholdsadresser: List<HentPerson.Oppholdsadresse>): List<Adresse> {
        val pdlOppholdsadresser: List<HentPerson.Oppholdsadresse> = oppholdsadresser.sortedBy { it.gyldigFraOgMed }
        return pdlOppholdsadresser.map { mapOppholdsadresser(it) }
    }

    fun mapFamilierelasjon(familierelasjon: HentPerson.Familierelasjon): Familierelasjon {
        return Familierelasjon(
            relatertPersonsIdent = familierelasjon.relatertPersonsIdent,
            relatertPersonsRolle = mapFamileRelasjonsrolle(familierelasjon.relatertPersonsRolle)!!,
            minRolleForPerson = mapFamileRelasjonsrolle(familierelasjon.minRolleForPerson),
            folkeregistermetadata = mapFolkeregisterMetadata(familierelasjon.folkeregistermetadata)
        )
    }

    fun mapOppholdsadresser(oppholdsadresse: HentPerson.Oppholdsadresse): Adresse {
        return Adresse(
            fom = convertToLocalDateTime(oppholdsadresse.gyldigFraOgMed)?.toLocalDate(),
            tom = convertToLocalDateTime(oppholdsadresse.folkeregistermetadata?.opphoerstidspunkt)?.toLocalDate(),
            landkode = mapLandkodeForOppholdsadresse(oppholdsadresse)
        )
    }

    fun mapLandkodeForOppholdsadresse(oppholdsadresse: HentPerson.Oppholdsadresse): String {
        if (oppholdsadresse.utenlandskAdresse != null) {
            return mapLandkode(oppholdsadresse.utenlandskAdresse!!.landkode)
        }
        return CountryCode.NO.alpha3
    }

    fun mapKontaktAdresse(it: HentPerson.Kontaktadresse): Adresse {
        return Adresse(
            fom = convertToLocalDateTime(it.gyldigFraOgMed)?.toLocalDate(),
            tom = convertToLocalDateTime(it.gyldigTilOgMed)?.toLocalDate(),
            landkode = mapLandkodeForKontaktadresse(it)
        )
    }

    fun mapLandkodeForKontaktadresse(kontaktadresse: HentPerson.Kontaktadresse): String {
        if (kontaktadresse.utenlandskAdresse != null) {
            return mapLandkode(kontaktadresse.utenlandskAdresse!!.landkode)
        }
        if (kontaktadresse.utenlandskAdresseIFrittFormat != null) {
            return mapLandkode(kontaktadresse.utenlandskAdresseIFrittFormat!!.landkode)
        }
        return CountryCode.NO.alpha3
    }

    private fun mapLandkode(landkode: String): String {
        return try {
            CountryCode.getByCode(landkode.toUpperCase()).alpha3
        } catch (e: Exception) {
            logger.warn("Klarte ikke å mappe {}", landkode, e)
            "UKJENT"
        }
    }

    fun mapBostedsadresse(bostedsadresse: HentPerson.Bostedsadresse): Adresse {
        return Adresse(
            landkode = "NOR",
            fom = convertToLocalDateTime(bostedsadresse.folkeregistermetadata?.gyldighetstidspunkt)?.toLocalDate(),
            tom = convertToLocalDateTime(bostedsadresse.folkeregistermetadata?.opphoerstidspunkt)?.toLocalDate()
        )
    }

    fun mapStatsborgerskap(it: HentPerson.Statsborgerskap): no.nav.medlemskap.domene.Statsborgerskap {
        return Statsborgerskap(
            landkode = it.land,
            fom = convertToLocalDate(it.gyldigFraOgMed),
            tom = convertToLocalDate(it.gyldigTilOgMed)
        )
    }

    private fun mapFamileRelasjonsrolle(rolle: HentPerson.Familierelasjonsrolle?): no.nav.medlemskap.domene.Familierelasjonsrolle? {
        return rolle.let {
            when (it) {
                HentPerson.Familierelasjonsrolle.BARN -> no.nav.medlemskap.domene.Familierelasjonsrolle.BARN
                HentPerson.Familierelasjonsrolle.MOR -> no.nav.medlemskap.domene.Familierelasjonsrolle.MOR
                HentPerson.Familierelasjonsrolle.FAR -> no.nav.medlemskap.domene.Familierelasjonsrolle.FAR
                HentPerson.Familierelasjonsrolle.MEDMOR -> no.nav.medlemskap.domene.Familierelasjonsrolle.MEDMOR
                else -> throw DetteSkalAldriSkje("Denne familierelasjonen er ikke tilgjengelig")
            }
        }
    }

    fun mapFolkeregisterMetadata2(folkeregistermetadata: HentPerson.Folkeregistermetadata2?): Folkeregistermetadata? {
        return folkeregistermetadata?.let {
            Folkeregistermetadata(
                ajourholdstidspunkt = convertToLocalDateTime(it.ajourholdstidspunkt),
                gyldighetstidspunkt = convertToLocalDateTime(it.gyldighetstidspunkt),
                opphoerstidspunkt = convertToLocalDateTime(it.opphoerstidspunkt)
            )
        }
    }

    fun mapFolkeregisterMetadata(folkeregistermetadata: HentPerson.Folkeregistermetadata?): Folkeregistermetadata? {
        return folkeregistermetadata?.let {
            Folkeregistermetadata(
                ajourholdstidspunkt = convertToLocalDateTime(it.ajourholdstidspunkt),
                gyldighetstidspunkt = convertToLocalDateTime(it.gyldighetstidspunkt),
                opphoerstidspunkt = convertToLocalDateTime(it.opphoerstidspunkt)
            )
        }
    }

    private fun convertToLocalDateTime(dateTimeToConvert: String?): LocalDateTime? {
        return dateTimeToConvert?.let { parseLocalDateTime(it) }
    }

    private fun parseLocalDateTime(string: String): LocalDateTime? {
        return try {
            LocalDateTime.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: Exception) {
            LocalDateTime.parse(string, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }
    }

    fun convertToLocalDate(dateToConvert: String?): LocalDate? {
        return dateToConvert?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
    }

    // Vi velger det høyeste årstallet, da blir personen yngst og det er mest sannsynlig at vi må vurdere bosted
    fun mapTilFoedselsaar(foedsel: List<HentFoedselsaar.Foedsel>?): Int =
        foedsel?.map { it.foedselsaar }?.sortedBy { it }?.last() ?: throw PersonIkkeFunnet("PDL")
}
