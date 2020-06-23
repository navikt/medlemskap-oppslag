package no.nav.medlemskap.domene

import java.time.LocalDate

data class PersonhistorikkRelatertPerson(
        val personstatuser: List<RelatertPersonstatus>,
        val bostedsadresser: List<PersonAdresse>,
        val postadresser: List<PersonAdresse>,
        val midlertidigAdresser: List<PersonAdresse>
)

data class PersonAdresse(
        val landkode: String,
        val fom: LocalDate?,
        val tom: LocalDate?
)

data class RelatertPersonstatus(
        val folkeregisterPersonstatus: PersonstatusType,
        val fom: LocalDate?,
        val tom: LocalDate?
)

enum class PersonstatusType(val navn: String) {
    ABNR("Aktivt BOSTNR"),
    ADNR("Aktivt"),
    BOSA("Bosatt"),
    DØD("Død"),
    FØDR("Fødselregistrert"),
    FOSV("Forsvunnet/savnet"),
    UFUL("Ufullstendig fødselsnr"),
    UREG("Uregistrert person"),
    UTAN(" Utgått person annullert tilgang Fnr"),
    UTPE(" Utgått person"),
    UTVA("Utvandret"),
    UKJENT("Dette er en ukjent type")
}
