package no.nav.medlemskap.domene

data class PersonhistorikkEktefelle(
        val ident: String,
        val barn: List<PersonhistorikkBarn>?
)