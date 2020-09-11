package no.nav.medlemskap.services.pdl.mapper

import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.medlemskap.client.generated.pdl.HentPerson
import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.filtrerBarnUnder25Aar

object PdlMapperEktefelle {

    fun mapPersonhistorikkTilEktefelle(fnr: String, ektefelle: HentPerson.Person): PersonhistorikkEktefelle {
        val barn = mapFnrBarnTilBrukersEktefelle(ektefelle.familierelasjoner)
        val oppholdsadresse = PdlMapper.mapOppholdsAdresse(ektefelle.oppholdsadresse)
        val bostedsadresser: List<Adresse> = ektefelle.bostedsadresse.map { PdlMapper.mapBostedsadresse(it) }
        val kontaktadresser: List<Adresse> = ektefelle.kontaktadresse.map { PdlMapper.mapKontaktAdresse(it) }

        return PersonhistorikkEktefelle(
                ident = fnr,
                barn =  barn,
                oppholdsadresser = oppholdsadresse,
                bostedsadresser = bostedsadresser,
                kontaktadresser = kontaktadresser)
    }


    fun mapFnrBarnTilBrukersEktefelle(familierelasjoner: List<HentPerson.Familierelasjon>): List<String>{
       return familierelasjoner
               .filter { it.relatertPersonsRolle == HentPerson.Familierelasjonsrolle.BARN}
               .filter { FodselsnummerValidator.isValid(it.relatertPersonsIdent) }
               .filter { it.relatertPersonsIdent.filtrerBarnUnder25Aar() }
               .map { it.relatertPersonsIdent }
    }
}