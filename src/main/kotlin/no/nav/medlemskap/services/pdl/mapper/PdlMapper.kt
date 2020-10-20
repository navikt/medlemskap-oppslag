package no.nav.medlemskap.services.pdl.mapper

import com.neovisionaries.i18n.CountryCode
import mu.KotlinLogging
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.common.exceptions.DetteSkalAldriSkje
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Datohjelper.Companion.parseIsoDato
import no.nav.medlemskap.services.pdl.PdlSivilstandMapper.mapSivilstander
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object PdlMapper {

    private val logger = KotlinLogging.logger { }

    fun mapTilPersonHistorikkTilBruker(person: HentPerson.Person): Personhistorikk {

        val statsborgerskap: List<Statsborgerskap> = mapStatsborgerskap(person.statsborgerskap)
        val bostedsadresser: List<Adresse> = mapBostedsadresser(person.bostedsadresse)
        val kontaktadresser: List<Adresse> = mapKontaktAdresser(person.kontaktadresse)
        val oppholdsadresser: List<Adresse> = mapOppholdsadresser(person.oppholdsadresse)
        val sivilstand: List<Sivilstand> = mapSivilstander(person.sivilstand)
        val familierelasjoner: List<Familierelasjon> = mapFamilierelasjoner(person.familierelasjoner)
        val doedsfall: List<LocalDate?> = mapDoedsfall(person.doedsfall)

        return Personhistorikk(
            statsborgerskap = statsborgerskap,
            bostedsadresser = bostedsadresser,
            sivilstand = sivilstand,
            familierelasjoner = familierelasjoner,
            kontaktadresser = kontaktadresser,
            oppholdsadresser = oppholdsadresser,
            doedsfall = doedsfall
        )
    }

    private fun mapDoedsfall(doedsfall: List<HentPerson.Doedsfall>): List<LocalDate?> {
        return doedsfall.map {
            convertToLocalDate(it.doedsdato)
        }
    }

    fun mapOppholdsadresser(oppholdsadresser: List<HentPerson.Oppholdsadresse>): List<Adresse> {
        return oppholdsadresser.map { mapOppholdsadresse(it) }.sortedBy { it.fom }
    }

    fun mapFamilierelasjoner(pdlFamilierelasjoner: List<HentPerson.Familierelasjon>): List<Familierelasjon> {
        return pdlFamilierelasjoner.map { mapFamilierelasjon(it) }
    }

    fun mapFamilierelasjon(familierelasjon: HentPerson.Familierelasjon): Familierelasjon {
        return Familierelasjon(
            relatertPersonsIdent = familierelasjon.relatertPersonsIdent,
            relatertPersonsRolle = mapFamileRelasjonsrolle(familierelasjon.relatertPersonsRolle)!!,
            minRolleForPerson = mapFamileRelasjonsrolle(familierelasjon.minRolleForPerson),
            folkeregistermetadata = mapFolkeregisterMetadata(familierelasjon.folkeregistermetadata)
        )
    }

    fun mapOppholdsadresse(oppholdsadresse: HentPerson.Oppholdsadresse): Adresse {
        return Adresse(
            fom = convertToLocalDateTime(oppholdsadresse.gyldigFraOgMed)?.toLocalDate(),
            tom = convertToLocalDateTime(oppholdsadresse.gyldigTilOgMed)?.toLocalDate(),
            landkode = mapLandkodeForOppholdsadresse(oppholdsadresse)
        )
    }

    fun mapLandkodeForOppholdsadresse(oppholdsadresse: HentPerson.Oppholdsadresse): String {
        if (oppholdsadresse.utenlandskAdresse != null) {
            return mapLandkode(oppholdsadresse.utenlandskAdresse!!.landkode)
        }
        return CountryCode.NO.alpha3
    }

    fun mapKontaktAdresser(pdlKontaktadresser: List<HentPerson.Kontaktadresse>): List<Adresse> {
        return pdlKontaktadresser.map {
            Adresse(
                fom = convertToLocalDateTime(it.gyldigFraOgMed)?.toLocalDate(),
                tom = convertToLocalDateTime(it.gyldigTilOgMed)?.toLocalDate(),
                landkode = mapLandkodeForKontaktadresse(it)
            )
        }.sortedBy { it.fom }
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
        if (landkode.length == 3) {
            return landkode
        }
        return try {
            CountryCode.getByCode(landkode.toUpperCase()).alpha3
        } catch (e: Exception) {
            logger.warn("Klarte ikke å mappe {}", landkode, e)
            "UKJENT"
        }
    }

    fun mapBostedsadresser(pdlPostedsadresser: List<HentPerson.Bostedsadresse>): List<Adresse> {
        return pdlPostedsadresser.map {
            Adresse(
                landkode = "NOR",
                fom = convertToLocalDateTime(it.gyldigFraOgMed)?.toLocalDate(),
                tom = convertToLocalDateTime(it.gyldigTilOgMed)?.toLocalDate()
            )
        }.sortedBy { it.fom }
    }

    fun mapStatsborgerskap(statsborgerskap: List<HentPerson.Statsborgerskap>): List<Statsborgerskap> {
        return statsborgerskap.map {
            Statsborgerskap(
                landkode = it.land,
                fom = parseIsoDato(it.gyldigFraOgMed),
                tom = parseIsoDato(it.gyldigTilOgMed)
            )
        }.sortedBy { it.fom }
    }

    private fun mapFamileRelasjonsrolle(rolle: HentPerson.Familierelasjonsrolle?): Familierelasjonsrolle? {
        return rolle.let {
            when (it) {
                HentPerson.Familierelasjonsrolle.BARN -> Familierelasjonsrolle.BARN
                HentPerson.Familierelasjonsrolle.MOR -> Familierelasjonsrolle.MOR
                HentPerson.Familierelasjonsrolle.FAR -> Familierelasjonsrolle.FAR
                HentPerson.Familierelasjonsrolle.MEDMOR -> Familierelasjonsrolle.MEDMOR
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
}
