package no.nav.medlemskap.domene

import java.time.LocalDate

data class PersonhistorikkRelatertPerson(
        val personstatuser: List<RelatertPersonstatus>,
        val bostedsadresser: List<PersonAdresse>,
        val postadresser: List<PersonAdresse>,
        val midlertidigAdresser: List<PersonAdresse>
)

data class PersonAdresse(
        val adresselinje: String,
        val landkode: String,
        val fom: LocalDate?,
        val tom: LocalDate?
)

data class RelatertPersonstatus(
        val personstatus: String,
        val fom: LocalDate?,
        val tom: LocalDate?
)

