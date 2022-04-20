package no.nav.medlemskap.cucumber.steps.pdl

import no.nav.medlemskap.domene.personhistorikk.*
import java.time.LocalDate

class PersonhistorikkBuilder {
    val statsborgerskap = mutableListOf<Statsborgerskap>()
    val bostedsadresser = mutableListOf<Adresse>()
    val sivilstand = mutableListOf<Sivilstand>()
    val familierelasjoner = mutableListOf<ForelderBarnRelasjon>()
    val kontaktadresse = mutableListOf<Adresse>()
    val oppholdsadresse = mutableListOf<Adresse>()
    val doedsfall = mutableListOf<LocalDate>()
    val navn = mutableListOf<Navn>()

    fun build(): Personhistorikk {
        return Personhistorikk(
            statsborgerskap = statsborgerskap,
            bostedsadresser = bostedsadresser,
            kontaktadresser = kontaktadresse,
            oppholdsadresser = oppholdsadresse,
            sivilstand = sivilstand,
            forelderBarnRelasjon = familierelasjoner,
            doedsfall = doedsfall,
            navn = navn
        )
    }
}
