package no.nav.medlemskap.cucumber.steps

import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.Familierelasjon
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle

class PersonhistorikkBarnBuilder {

    var ident = String()
    val bostedsadresse = mutableListOf<Adresse>()
    val kontaktadresse = mutableListOf<Adresse>()
    val oppholdsadresse = mutableListOf<Adresse>()
    val familierelasjon = mutableListOf<Familierelasjon>()


    fun build(): PersonhistorikkBarn{
        return PersonhistorikkBarn(
                ident = ident,
                bostedsadresser = bostedsadresse,
                kontaktadresser = kontaktadresse,
                oppholdsadresser = oppholdsadresse,
                familierelasjoner = familierelasjon
        )
    }
}