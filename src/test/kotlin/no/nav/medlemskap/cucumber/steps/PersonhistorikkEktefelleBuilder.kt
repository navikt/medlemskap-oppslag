package no.nav.medlemskap.cucumber.steps

import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle

class PersonhistorikkEktefelleBuilder {

    var ident = String()
    var barn = mutableListOf<String>()
    val bostedsadresse = mutableListOf<Adresse>()
    val kontaktadresse = mutableListOf<Adresse>()
    val oppholdsadresse = mutableListOf<Adresse>()


    fun build(): PersonhistorikkEktefelle {
        return PersonhistorikkEktefelle(
                ident = ident,
                barn = barn,
                bostedsadresser = bostedsadresse,
                kontaktadresser = kontaktadresse,
                oppholdsadresser = oppholdsadresse
        )
    }
}
