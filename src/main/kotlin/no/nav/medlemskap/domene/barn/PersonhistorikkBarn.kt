package no.nav.medlemskap.domene.barn

import no.nav.medlemskap.domene.personhistorikk.Adresse
import no.nav.medlemskap.domene.personhistorikk.Familierelasjon

data class PersonhistorikkBarn(
    val ident: String,
    val bostedsadresser: List<Adresse>,
    val kontaktadresser: List<Adresse>,
    val oppholdsadresser: List<Adresse>,
    val familierelasjoner: List<Familierelasjon?>
)
