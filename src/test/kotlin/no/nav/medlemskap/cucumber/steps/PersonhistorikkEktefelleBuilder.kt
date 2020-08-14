package no.nav.medlemskap.cucumber.steps

import no.nav.medlemskap.domene.*

class PersonhistorikkEktefelleBuilder {

    val ident = String()
    val barn = mutableListOf<String>()

    fun build(): PersonhistorikkEktefelle {
        return PersonhistorikkEktefelle(
                ident,
                barn
        )
    }
}