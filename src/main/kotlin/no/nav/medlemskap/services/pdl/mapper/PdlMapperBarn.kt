package no.nav.medlemskap.services.pdl.mapper

import no.nav.medlemskap.client.generated.pdl.HentPerson
import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.Familierelasjon
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.services.pdl.mapper.PdlMapper.mapKontaktAdresse

object PdlMapperBarn {

    fun mapPersonhistorikkTilBarn(fnr: String, barn: HentPerson.Person): PersonhistorikkBarn {
        val oppholdsadresse = PdlMapper.mapOppholdsAdresse(barn.oppholdsadresse)
        val bostedsadresser: List<Adresse> = barn.bostedsadresse.map { PdlMapper.mapBostedsadresse(it) }
        val kontaktadresser: List<Adresse> = barn.kontaktadresse.map { mapKontaktAdresse(it) }
        val familierelasjoner: List<Familierelasjon> = barn.familierelasjoner.map { PdlMapper.mapFamilierelasjon(it) }

        return PersonhistorikkBarn(
                ident = fnr,
                familierelasjoner = familierelasjoner,
                oppholdsadresser = oppholdsadresse,
                bostedsadresser = bostedsadresser,
                kontaktadresser = kontaktadresser
        )
    }
}