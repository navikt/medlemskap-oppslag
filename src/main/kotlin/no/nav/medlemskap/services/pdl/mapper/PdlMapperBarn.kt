package no.nav.medlemskap.services.pdl.mapper

import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.Familierelasjon
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.services.pdl.mapper.PdlMapper.mapBostedsadresser
import no.nav.medlemskap.services.pdl.mapper.PdlMapper.mapKontaktAdresser

object PdlMapperBarn {

    fun mapPersonhistorikkTilBarn(fnr: String, barn: HentPerson.Person): PersonhistorikkBarn {
        val oppholdsadresse = PdlMapper.mapOppholdsadresser(barn.oppholdsadresse)
        val bostedsadresser: List<Adresse> = mapBostedsadresser(barn.bostedsadresse)
        val kontaktadresser: List<Adresse> = mapKontaktAdresser(barn.kontaktadresse)
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
