package no.nav.medlemskap.domene.barn

import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.Familierelasjon
import no.nav.medlemskap.domene.FolkeregisterPersonstatus

data class PersonhistorikkBarn(
        val ident: String,
        val bostedsadresser: List<Adresse>,
        val kontaktadresser: List<Adresse>,
        val oppholdsadresser: List<Adresse>,
        val familierelasjoner: List<Familierelasjon?>
)