package no.nav.medlemskap.domene

import java.time.LocalDateTime

data class PersonhistorikkRelatertPerson(
        val ident: String,
        val personstatuser: List<FolkeregisterPersonstatus>,
        val bostedsadresser: List<Adresse>,
        val postadresser: List<Adresse>,
        val midlertidigAdresser: List<Adresse>
)


