package no.nav.medlemskap.cucumber.steps

import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.Familierelasjon
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn

class DataOmBarnBuilder {
    var personhistorikkBarn = PersonhistorikkBarn(
            ident = String(),
            bostedsadresser = mutableListOf<Adresse>(),
            kontaktadresser = mutableListOf<Adresse>(),
            oppholdsadresser = mutableListOf<Adresse>(),
            familierelasjoner = mutableListOf<Familierelasjon>()

    )


    fun build(): DataOmBarn {
        return DataOmBarn(
                personhistorikkBarn = personhistorikkBarn
        )
    }
}

