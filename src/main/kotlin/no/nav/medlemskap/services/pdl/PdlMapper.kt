package no.nav.medlemskap.services.pdl

import no.nav.medlemskap.clients.pdl.Familierelasjonsrolle
import no.nav.medlemskap.clients.pdl.HentFoedselsaarResponse
import no.nav.medlemskap.clients.pdl.HentPdlPersonResponse
import no.nav.medlemskap.common.exceptions.PersonIkkeFunnet
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import no.nav.medlemskap.services.pdl.PdlSivilstandMapper.mapSivilstander

object PdlMapper {
    fun mapTilPersonHistorikk(person: HentPdlPersonResponse): Personhistorikk {

        val statsborgerskap: List<Statsborgerskap> = person.data?.hentPerson?.statsborgerskap?.map {
            mapStatsborgerskap(it)
        } ?: throw PersonIkkeFunnet("PDL")

        val personstatuser: List<FolkeregisterPersonstatus> = emptyList()

        val bostedsadresser: List<Adresse> = person.data.hentPerson.bostedsadresse.map {
            Adresse(
                    landkode = "NOR",
                    fom = it.folkeregistermetadata.gyldighetstidspunkt?.toLocalDate(),
                    tom = it.folkeregistermetadata.opphoerstidspunkt?.toLocalDate()
            )
        }

        val postadresser: List<Adresse> = emptyList()
        val midlertidigAdresser: List<Adresse> = emptyList()
        val sivilstand: List<Sivilstand> = mapSivilstander(person.data.hentPerson.sivilstand)

        val familierelasjoner: List<Familierelasjon> = person.data.hentPerson.familierelasjoner.filter { it.relatertPersonsRolle == Familierelasjonsrolle.BARN }
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


    fun mapStatsborgerskap(it: no.nav.medlemskap.clients.pdl.Statsborgerskap): Statsborgerskap {
        return Statsborgerskap(
                landkode = it.land,
                fom = it.gyldigFraOgMed,
                tom = it.gyldigTilOgMed
        )
    }

    private fun mapFamileRelasjonsrolle(rolle: Familierelasjonsrolle?): no.nav.medlemskap.domene.Familierelasjonsrolle? {
        return rolle?.let {
            when (it) {
                Familierelasjonsrolle.BARN -> no.nav.medlemskap.domene.Familierelasjonsrolle.BARN
                Familierelasjonsrolle.MOR -> no.nav.medlemskap.domene.Familierelasjonsrolle.MOR
                Familierelasjonsrolle.FAR -> no.nav.medlemskap.domene.Familierelasjonsrolle.FAR
                Familierelasjonsrolle.MEDMOR -> no.nav.medlemskap.domene.Familierelasjonsrolle.MEDMOR
            }
        }
    }

    fun mapFolkeregisterMetadata(folkeregistermetadata: no.nav.medlemskap.clients.pdl.Folkeregistermetadata?): Folkeregistermetadata? {
        return folkeregistermetadata?.let {
            Folkeregistermetadata(
                    ajourholdstidspunkt = it.ajourholdstidspunkt,
                    gyldighetstidspunkt = it.gyldighetstidspunkt,
                    opphoerstidspunkt = it.opphoerstidspunkt
            )
        }
    }

    //Vi velger det høyeste årstallet, da blir personen yngst og det er mest sannsynlig at vi må vurdere bosted
    fun mapTilFoedselsaar(response: HentFoedselsaarResponse): Int =
            response.data?.hentPerson?.foedsel?.map { it.foedselsaar }?.sorted()?.last()
                    ?: throw PersonIkkeFunnet("PDL")

}




