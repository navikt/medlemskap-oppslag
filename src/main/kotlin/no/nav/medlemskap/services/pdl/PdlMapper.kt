package no.nav.medlemskap.services.pdl

import no.nav.medlemskap.client.generated.pdl.HentPerson
import no.nav.medlemskap.clients.pdl.Familierelasjonsrolle
import no.nav.medlemskap.clients.pdl.HentFoedselsaarResponse
import no.nav.medlemskap.clients.pdl.HentPdlPersonResponse
import no.nav.medlemskap.common.exceptions.DetteSkalAldriSkje
import no.nav.medlemskap.common.exceptions.PersonIkkeFunnet
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import no.nav.medlemskap.services.pdl.PdlSivilstandMapper.mapSivilstander
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


object PdlMapper {
    fun mapTilPersonHistorikk(person: HentPerson.Person): Personhistorikk {

        val statsborgerskap: List<Statsborgerskap> = person.statsborgerskap
                .map { mapStatsborgerskap(it) }

        val personstatuser: List<FolkeregisterPersonstatus> = emptyList()

        val bostedsadresser: List<Adresse> = person.bostedsadresse.map {
            Adresse(
                    landkode = "NOR",
                    fom = convertToLocalDate(it.folkeregistermetadata!!.gyldighetstidspunkt),
                    tom = convertToLocalDate(it.folkeregistermetadata.opphoerstidspunkt)
            )
        }

        val postadresser: List<Adresse> = emptyList()
        val midlertidigAdresser: List<Adresse> = emptyList()
        val sivilstand: List<Sivilstand> = mapSivilstander(person.sivilstand)

        val familierelasjoner: List<Familierelasjon> = person.familierelasjoner
                .filter { it.relatertPersonsRolle == Familierelasjonsrolle.BARN }
                .map {
                    Familierelasjon(
                            relatertPersonsIdent = it.relatertPersonsIdent,
                            relatertPersonsRolle = mapFamileRelasjonsrolle(it.relatertPersonsRolle)!!,
                            minRolleForPerson = mapFamileRelasjonsrolle(it.minRolleForPerson),
                            folkeregistermetadata = mapFolkeregisterMetadata(it.folkeregistermetadata)
                    )
                }

        return Personhistorikk(statsborgerskap, personstatuser, bostedsadresser, postadresser, midlertidigAdresser, sivilstand, familierelasjoner)
    }
    
    fun mapPersonhistorikkTilEktefelle(fnr: String, person: HentPdlPersonResponse): PersonhistorikkEktefelle {
        val barn = person.data?.hentPerson?.familierelasjoner
                ?.filter { it.minRolleForPerson == Familierelasjonsrolle.BARN }
                ?.map {
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

    private fun mapFamileRelasjonsrolle(rolle: HentPerson.Familierelasjonsrolle?): no.nav.medlemskap.domene.Familierelasjonsrolle? {
        return rolle.let {
            when (it) {
                Familierelasjonsrolle.BARN -> no.nav.medlemskap.domene.Familierelasjonsrolle.BARN
                Familierelasjonsrolle.MOR -> no.nav.medlemskap.domene.Familierelasjonsrolle.MOR
                Familierelasjonsrolle.FAR -> no.nav.medlemskap.domene.Familierelasjonsrolle.FAR
                Familierelasjonsrolle.MEDMOR -> no.nav.medlemskap.domene.Familierelasjonsrolle.MEDMOR
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
    fun mapTilFoedselsaar(response: HentFoedselsaarResponse): Int =
            response.data?.hentPerson?.foedsel?.map { it.foedselsaar }?.sorted()?.last()
                    ?: throw PersonIkkeFunnet("PDL")

}




