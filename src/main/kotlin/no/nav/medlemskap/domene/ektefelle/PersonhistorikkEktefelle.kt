package no.nav.medlemskap.domene.ektefelle

import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn

data class PersonhistorikkEktefelle(
        val ident: String,
        val barn: List<String>?,
        val bostedsadresser: List<Adresse>,
        val kontaktadresser: List<Adresse>,
        val oppholdsadresser: List<Adresse>
)