package no.nav.medlemskap.cucumber.steps

import no.nav.medlemskap.domene.*

class PersonhistorikkBuilder {
    val statsborgerskap = mutableListOf<Statsborgerskap>()
    val personstatuser = mutableListOf<FolkeregisterPersonstatus>()
    val bostedsadresser = mutableListOf<Adresse>()
    val sivilstand = mutableListOf<Sivilstand>()
    val familierelasjoner = mutableListOf<Familierelasjon>()
    val kontaktadresse = mutableListOf<Adresse>()
    val oppholdsadresse = mutableListOf<Adresse>()

    fun build(): Personhistorikk {
        return Personhistorikk(
                statsborgerskap = statsborgerskap,
                personstatuser = personstatuser,
                bostedsadresser = bostedsadresser,
                kontaktadresser = kontaktadresse,
                oppholdsadresser = oppholdsadresse,
                sivilstand = sivilstand,
                familierelasjoner = familierelasjoner
        )
    }
}