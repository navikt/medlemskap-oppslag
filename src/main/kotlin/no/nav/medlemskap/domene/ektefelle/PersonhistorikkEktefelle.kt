package no.nav.medlemskap.domene.ektefelle

import no.nav.medlemskap.domene.barn.PersonhistorikkBarn

data class PersonhistorikkEktefelle(
    val ident: String,
    val barn: List<PersonhistorikkBarn>?
)
