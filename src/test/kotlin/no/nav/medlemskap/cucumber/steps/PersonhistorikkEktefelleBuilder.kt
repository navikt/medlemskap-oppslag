package no.nav.medlemskap.cucumber.steps

import no.nav.medlemskap.domene.*

class PersonhistorikkEktefelleBuilder {

    var ident = String()
    var barn = mutableListOf<PersonhistorikkBarn>()

    fun build(): PersonhistorikkEktefelle {
        return PersonhistorikkEktefelle(
                ident,
                barn
        )
    }
}