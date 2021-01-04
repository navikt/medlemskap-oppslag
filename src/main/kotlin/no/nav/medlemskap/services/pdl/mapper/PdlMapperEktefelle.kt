package no.nav.medlemskap.services.pdl.mapper

import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.Familierelasjon.Companion.erBarnUnder25Aar
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import no.nav.medlemskap.services.pdl.mapper.PdlMapper.mapBostedsadresser
import no.nav.medlemskap.services.pdl.mapper.PdlMapper.mapKontaktAdresser
import java.time.LocalDate

object PdlMapperEktefelle {

    fun mapPersonhistorikkTilEktefelle(fnr: String, ektefelle: HentPerson.Person, førsteDatoForYtelse: LocalDate): PersonhistorikkEktefelle {
        val barn = mapFnrBarnTilBrukersEktefelle(ektefelle.familierelasjoner, førsteDatoForYtelse)
        val oppholdsadresse = PdlMapper.mapOppholdsadresser(ektefelle.oppholdsadresse)
        val bostedsadresser: List<Adresse> = mapBostedsadresser(ektefelle.bostedsadresse)
        val kontaktadresser: List<Adresse> = mapKontaktAdresser(ektefelle.kontaktadresse)

        return PersonhistorikkEktefelle(
            ident = fnr,
            barn = barn,
            oppholdsadresser = oppholdsadresse,
            bostedsadresser = bostedsadresser,
            kontaktadresser = kontaktadresser
        )
    }

    private fun mapFnrBarnTilBrukersEktefelle(familierelasjoner: List<HentPerson.Familierelasjon>, førsteDatoForYtelse: LocalDate): List<String> {
        return familierelasjoner
            .filter { it.relatertPersonsRolle == HentPerson.Familierelasjonsrolle.BARN }
            .filter { FodselsnummerValidator.isValid(it.relatertPersonsIdent) }
            .filter { it.relatertPersonsIdent.erBarnUnder25Aar(førsteDatoForYtelse) }
            .map { it.relatertPersonsIdent }
    }
}
