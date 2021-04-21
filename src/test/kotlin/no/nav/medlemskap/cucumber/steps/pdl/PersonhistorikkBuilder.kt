package no.nav.medlemskap.cucumber.steps.pdl

import no.nav.medlemskap.domene.personhistorikk.*
import java.time.LocalDate

class PersonhistorikkBuilder {
    val statsborgerskap = mutableListOf<Statsborgerskap>()
    val adressebeskyttelse = mutableListOf<Adressebeskyttelse>()
    val bostedsadresser = mutableListOf<Adresse>()
    val sivilstand = mutableListOf<Sivilstand>()
    val familierelasjoner = mutableListOf<ForelderBarnRelasjon>()
    val kontaktadresse = mutableListOf<Adresse>()
    val oppholdsadresse = mutableListOf<Adresse>()
    val doedsfall = mutableListOf<LocalDate>()

    fun build(): Personhistorikk {
        return Personhistorikk(
            statsborgerskap = statsborgerskap,
            adressebeskyttelse = adressebeskyttelse,
            bostedsadresser = bostedsadresser,
            kontaktadresser = kontaktadresse,
            oppholdsadresser = oppholdsadresse,
            sivilstand = sivilstand,
            forelderBarnRelasjon = familierelasjoner,
            doedsfall = doedsfall
        )
    }
}
