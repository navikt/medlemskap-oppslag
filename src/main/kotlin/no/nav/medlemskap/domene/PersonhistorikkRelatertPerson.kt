package no.nav.medlemskap.domene

data class PersonhistorikkRelatertPerson(
        val personstatuser: List<FolkeregisterPersonstatus>,
        val bostedsadresser: List<Adresse>,
        val postadresser: List<Adresse>,
        val midlertidigAdresser: List<Adresse>
)

