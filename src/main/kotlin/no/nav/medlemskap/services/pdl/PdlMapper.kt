package no.nav.medlemskap.services.pdl

import no.nav.medlemskap.client.generated.pdl.HentFoedselsaar
import no.nav.medlemskap.client.generated.pdl.HentNasjonalitet
import no.nav.medlemskap.client.generated.pdl.HentPerson
import no.nav.medlemskap.common.exceptions.DetteSkalAldriSkje
import no.nav.medlemskap.common.exceptions.PersonIkkeFunnet
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import no.nav.medlemskap.services.pdl.PdlSivilstandMapper.mapSivilstander
import com.neovisionaries.i18n.LanguageAlpha3Code
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object PdlMapper {
    fun mapTilPersonHistorikk(person: HentPerson.Person): Personhistorikk {

        val statsborgerskap: List<Statsborgerskap> = person.statsborgerskap.map { mapStatsborgerskap(it) }
        val sivilstand: List<Sivilstand> = mapSivilstander(person.sivilstand)
        val bostedsadresser: List<Adresse> = person.bostedsadresse.map { mapBostedsadresse(it) }

        val kontaktadresser: List<Adresse> = person.kontaktadresse.map { mapKontaktAdresse(it) }
        val oppholdsadresser: List<Adresse> = person.oppholdsadresse.map { mapOppholdsadresser(it) }
        val postadresser: List<Adresse> = emptyList()
        val midlertidigAdresser: List<Adresse> = emptyList()

        val familierelasjoner: List<Familierelasjon> = person.familierelasjoner
                .filter { it.relatertPersonsRolle == HentPerson.Familierelasjonsrolle.BARN }
                .map { mapFamilierelasjon(it) }

        val personstatuser: List<FolkeregisterPersonstatus> = emptyList()

        return Personhistorikk(statsborgerskap = statsborgerskap,
                personstatuser = personstatuser,
                bostedsadresser = bostedsadresser,
                midlertidigAdresser = midlertidigAdresser,
                sivilstand = sivilstand,
                familierelasjoner = familierelasjoner,
                kontaktadresser = kontaktadresser,
                postadresser = postadresser,
                oppholdsadresser = oppholdsadresser)
    }

    private fun mapFamilierelasjon(familierelasjon: HentPerson.Familierelasjon): Familierelasjon {
        return Familierelasjon(
                relatertPersonsIdent = familierelasjon.relatertPersonsIdent,
                relatertPersonsRolle = mapFamileRelasjonsrolle(familierelasjon.relatertPersonsRolle)!!,
                minRolleForPerson = mapFamileRelasjonsrolle(familierelasjon.minRolleForPerson),
                folkeregistermetadata = mapFolkeregisterMetadata(familierelasjon.folkeregistermetadata)
        )
    }

    private fun mapOppholdsadresser(oppholdsadresse: HentPerson.Oppholdsadresse): Adresse{
        return Adresse(
                fom = convertToLocalDate(oppholdsadresse.oppholdsadressedato),
                tom = convertToLocalDate(oppholdsadresse.folkeregistermetadata?.opphoerstidspunkt),
                landkode = mapLandkodeForOppholdsadresse(oppholdsadresse)
        )
    }

    private fun mapLandkodeForOppholdsadresse(oppholdsadresse: HentPerson.Oppholdsadresse): String {
        if(oppholdsadresse.utenlandskAdresse != null){
            return LanguageAlpha3Code.getByCode(oppholdsadresse.utenlandskAdresse.landkode.toLowerCase()).name.toUpperCase()
        }
        return LanguageAlpha3Code.nor.name

    }

    private fun mapKontaktAdresse(it: HentPerson.Kontaktadresse): Adresse {
        return Adresse(
                fom = convertToLocalDate(it.gyldigFraOgMed),
                tom = convertToLocalDate(it.gyldigTilOgMed),
                landkode = mapLandkodeForKontaktadresse(it)
        )
    }

    private fun mapLandkodeForKontaktadresse(kontaktadresse: HentPerson.Kontaktadresse): String {
        if (kontaktadresse.utenlandskAdresse  != null){
            return LanguageAlpha3Code.getByCode(kontaktadresse.utenlandskAdresse.landkode.toLowerCase()).name.toUpperCase()
        }
        if(kontaktadresse.utenlandskAdresseIFrittFormat != null){
            return LanguageAlpha3Code.getByCode(kontaktadresse.utenlandskAdresseIFrittFormat.landkode.toLowerCase()).name.toUpperCase()
        }
        return LanguageAlpha3Code.nor.name.toUpperCase()
    }

    private fun mapBostedsadresse(bostedsadresse: HentPerson.Bostedsadresse): Adresse {
        return Adresse(
                landkode = "NOR",
                fom = convertToLocalDate(bostedsadresse.folkeregistermetadata?.gyldighetstidspunkt),
                tom = convertToLocalDate(bostedsadresse.folkeregistermetadata?.opphoerstidspunkt)
        )
    }

    fun mapPersonhistorikkTilEktefelle(fnr: String, person: HentPerson.Person): PersonhistorikkEktefelle {
        val barn = person.familierelasjoner
                .filter { it.minRolleForPerson ==  HentPerson.Familierelasjonsrolle.BARN }
                .map {
                    PersonhistorikkBarn(
                            it.relatertPersonsIdent)
                }
        return PersonhistorikkEktefelle(fnr, barn)
    }

    fun mapStatsborgerskap(it: HentPerson.Statsborgerskap): Statsborgerskap {
        return Statsborgerskap(
                landkode = it.land,
                fom = convertToLocalDate(it.gyldigFraOgMed),
                tom = convertToLocalDate(it.gyldigTilOgMed)
        )
    }

    fun mapStatsborgerskap(it: HentNasjonalitet.Statsborgerskap): Statsborgerskap {
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

    fun mapFolkeregisterMetadata2(folkeregistermetadata: HentPerson.Folkeregistermetadata2?): Folkeregistermetadata?
    {
        return folkeregistermetadata?.let {
            Folkeregistermetadata(
                    ajourholdstidspunkt = convertToLocalDateTime(it.ajourholdstidspunkt),
                    gyldighetstidspunkt = convertToLocalDateTime(it.gyldighetstidspunkt),
                    opphoerstidspunkt = convertToLocalDateTime(it.opphoerstidspunkt)
            )
        }
    }

    fun mapFolkeregisterMetadata(folkeregistermetadata: HentPerson.Folkeregistermetadata?): Folkeregistermetadata?
    {
        return folkeregistermetadata?.let {
            Folkeregistermetadata(
                    ajourholdstidspunkt = convertToLocalDateTime(it.ajourholdstidspunkt),
                    gyldighetstidspunkt = convertToLocalDateTime(it.gyldighetstidspunkt),
                    opphoerstidspunkt = convertToLocalDateTime(it.opphoerstidspunkt)
            )
        }
    }

    private fun convertToLocalDateTime(dateTimeToConvert: String?): LocalDateTime? {
        return LocalDateTime.parse(dateTimeToConvert, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    fun convertToLocalDate(dateToConvert: String?): LocalDate? {
        return LocalDate.parse(dateToConvert, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    //Vi velger det høyeste årstallet, da blir personen yngst og det er mest sannsynlig at vi må vurdere bosted
    fun mapTilFoedselsaar(foedsel: List<HentFoedselsaar.Foedsel>?): Int =
            foedsel?.map { it.foedselsaar }?.sortedBy { it }?.last() ?: throw PersonIkkeFunnet("PDL")
}




