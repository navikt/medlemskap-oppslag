package no.nav.medlemskap.domene

data class PersonhistorikkRelatertPerson(
        val ident: String,
        val personstatuser: List<FolkeregisterPersonstatus>,
        val bostedsadresser: List<Adresse>,
        val kontaktadresser: List<Adresse>,
        val oppholdsadresser: List<Adresse>

)


