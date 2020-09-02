package no.nav.medlemskap.cucumber.steps

import no.nav.medlemskap.domene.*

class PersonhistorikkBuilder {
    val statsborgerskap = mutableListOf<Statsborgerskap>()
    val personstatuser = mutableListOf<FolkeregisterPersonstatus>()
    val bostedsadresser = mutableListOf<Adresse>()
    val postadresser = mutableListOf<Adresse>()
    val midlertidigAdresser= mutableListOf<Adresse>()
    val sivilstand = mutableListOf<Sivilstand>()
    val familierelasjoner = mutableListOf<Familierelasjon>()
    val kontaktadresse = mutableListOf<Kontaktadresse>()
    val oppholdsadresse = mutableListOf<Oppholdsadresse>()

    fun build(): Personhistorikk {
        return Personhistorikk(
                statsborgerskap,
                personstatuser,
                bostedsadresser,
                postadresser,
                midlertidigAdresser,
                sivilstand,
                familierelasjoner,
                kontaktadresse,
                oppholdsadresse
        )
    }
}