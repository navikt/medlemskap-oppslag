package no.nav.medlemskap.services.pdl

import com.neovisionaries.i18n.LanguageAlpha3Code
import no.nav.medlemskap.clients.pdl.generated.HentFoedselsaar
import no.nav.medlemskap.clients.pdl.generated.HentNasjonalitet
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.common.exceptions.DetteSkalAldriSkje
import no.nav.medlemskap.common.exceptions.PersonIkkeFunnet
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import no.nav.medlemskap.services.pdl.PdlSivilstandMapper.mapSivilstander
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object PdlMapper {
    fun mapTilPersonHistorikk(person: HentPerson.Person): Personhistorikk {

        val statsborgerskap: List<Statsborgerskap> = person.statsborgerskap.map { mapStatsborgerskap(it) }
        val sivilstand: List<Sivilstand> = mapSivilstander(person.sivilstand)
        val bostedsadresser: List<Adresse> = person.bostedsadresse.map { mapBostedsadresse(it) }
        val kontaktadresser: List<Adresse> = person.kontaktadresse.map { mapKontaktAdresse(it) }

        val pdlOppholdsadresser: List<HentPerson.Oppholdsadresse> = person.oppholdsadresse.sortedBy { it.gyldigFraOgMed }

        val oppholdsadresser: List<Adresse> = pdlOppholdsadresser.mapIndexed { index, oppholdsadresse ->

            val opphoerstidspunkt = convertToLocalDateTime(oppholdsadresse.folkeregistermetadata?.opphoerstidspunkt)?.toLocalDate()

            if (pdlOppholdsadresser.size < 2) {
                mapOppholdsadresser(oppholdsadresse, opphoerstidspunkt)
            }
            if ((pdlOppholdsadresser.size) - 1 == index) {
                mapOppholdsadresser(oppholdsadresse, opphoerstidspunkt)
            } else {
                val tom = convertToLocalDateTime(pdlOppholdsadresser.get(index + 1).gyldigFraOgMed)?.toLocalDate()?.minusDays(1)
                mapOppholdsadresser(oppholdsadresse, tom)
            }
        }

        val postadresser: List<Adresse> = emptyList()
        val midlertidigAdresser: List<Adresse> = emptyList()

        val familierelasjoner: List<Familierelasjon> = person.familierelasjoner
            .filter { it.relatertPersonsRolle == HentPerson.Familierelasjonsrolle.BARN }
            .map { mapFamilierelasjon(it) }

        val personstatuser: List<FolkeregisterPersonstatus> = emptyList()

        return Personhistorikk(
            statsborgerskap = statsborgerskap,
            personstatuser = personstatuser,
            bostedsadresser = bostedsadresser,
            midlertidigAdresser = midlertidigAdresser,
            sivilstand = sivilstand,
            familierelasjoner = familierelasjoner,
            kontaktadresser = kontaktadresser,
            postadresser = postadresser,
            oppholdsadresser = oppholdsadresser
        )
    }

    private fun mapFamilierelasjon(familierelasjon: HentPerson.Familierelasjon): Familierelasjon {
        return Familierelasjon(
            relatertPersonsIdent = familierelasjon.relatertPersonsIdent,
            relatertPersonsRolle = mapFamileRelasjonsrolle(familierelasjon.relatertPersonsRolle)!!,
            minRolleForPerson = mapFamileRelasjonsrolle(familierelasjon.minRolleForPerson),
            folkeregistermetadata = mapFolkeregisterMetadata(familierelasjon.folkeregistermetadata)
        )
    }

    private fun mapOppholdsadresser(oppholdsadresse: HentPerson.Oppholdsadresse, tom: LocalDate?): Adresse {

        return Adresse(
            fom = convertToLocalDateTime(oppholdsadresse.gyldigFraOgMed)?.toLocalDate(),
            tom = convertToLocalDateTime(oppholdsadresse.folkeregistermetadata?.opphoerstidspunkt)?.toLocalDate(),
            landkode = mapLandkodeForOppholdsadresse(oppholdsadresse)
        )
    }

    private fun mapLandkodeForOppholdsadresse(oppholdsadresse: HentPerson.Oppholdsadresse): String {
        if (oppholdsadresse.utenlandskAdresse != null) {
            return LanguageAlpha3Code.getByCode(oppholdsadresse.utenlandskAdresse!!.landkode.toLowerCase()).name.toUpperCase()
        }
        return LanguageAlpha3Code.nor.name
    }

    private fun mapKontaktAdresse(it: HentPerson.Kontaktadresse): Adresse {
        return Adresse(
            fom = convertToLocalDateTime(it.gyldigFraOgMed)?.toLocalDate(),
            tom = convertToLocalDateTime(it.gyldigTilOgMed)?.toLocalDate(),
            landkode = mapLandkodeForKontaktadresse(it)
        )
    }

    private fun mapLandkodeForKontaktadresse(kontaktadresse: HentPerson.Kontaktadresse): String {
        if (kontaktadresse.utenlandskAdresse != null) {
            return LanguageAlpha3Code.getByCode(kontaktadresse.utenlandskAdresse!!.landkode.toLowerCase()).name.toUpperCase()
        }
        if (kontaktadresse.utenlandskAdresseIFrittFormat != null) {
            return LanguageAlpha3Code.getByCode(kontaktadresse.utenlandskAdresseIFrittFormat!!.landkode.toLowerCase()).name.toUpperCase()
        }
        return LanguageAlpha3Code.nor.name.toUpperCase()
    }

    private fun mapBostedsadresse(bostedsadresse: HentPerson.Bostedsadresse): Adresse {
        return Adresse(
            landkode = "NOR",
            fom = convertToLocalDateTime(bostedsadresse.folkeregistermetadata?.gyldighetstidspunkt)?.toLocalDate(),
            tom = convertToLocalDateTime(bostedsadresse.folkeregistermetadata?.opphoerstidspunkt)?.toLocalDate()
        )
    }

    fun mapPersonhistorikkTilEktefelle(fnr: String, person: HentPerson.Person): PersonhistorikkEktefelle {
        val barn = person.familierelasjoner
            .filter { it.minRolleForPerson == HentPerson.Familierelasjonsrolle.BARN }
            .map {
                PersonhistorikkBarn(
                    it.relatertPersonsIdent
                )
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
        return dateTimeToConvert?.let { LocalDateTime.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) }
    }

    fun convertToLocalDate(dateToConvert: String?): LocalDate? {
        return dateToConvert?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
    }

    // Vi velger det høyeste årstallet, da blir personen yngst og det er mest sannsynlig at vi må vurdere bosted
    fun mapTilFoedselsaar(foedsel: List<HentFoedselsaar.Foedsel>?): Int =
        foedsel?.map { it.foedselsaar }?.sortedBy { it }?.last() ?: throw PersonIkkeFunnet("PDL")
}
