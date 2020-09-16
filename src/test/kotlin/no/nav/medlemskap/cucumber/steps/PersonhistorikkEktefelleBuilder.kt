package no.nav.medlemskap.cucumber.steps

import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle

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
