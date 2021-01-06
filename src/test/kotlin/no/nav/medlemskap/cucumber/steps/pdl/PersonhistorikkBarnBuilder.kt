package no.nav.medlemskap.cucumber.steps.pdl

import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.personhistorikk.Adresse
import no.nav.medlemskap.domene.personhistorikk.Familierelasjon

class PersonhistorikkBarnBuilder {

    var ident = String()
    val bostedsadresse = mutableListOf<Adresse>()
    val kontaktadresse = mutableListOf<Adresse>()
    val oppholdsadresse = mutableListOf<Adresse>()
    val familierelasjon = mutableListOf<Familierelasjon>()

    fun build(): PersonhistorikkBarn {
        return PersonhistorikkBarn(
            ident = ident,
            bostedsadresser = bostedsadresse,
            kontaktadresser = kontaktadresse,
            oppholdsadresser = oppholdsadresse,
            familierelasjoner = familierelasjon
        )
    }
}
