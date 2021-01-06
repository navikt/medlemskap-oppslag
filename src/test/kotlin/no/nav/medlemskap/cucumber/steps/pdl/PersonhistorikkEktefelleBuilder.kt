package no.nav.medlemskap.cucumber.steps.pdl

import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import no.nav.medlemskap.domene.personhistorikk.Adresse

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
