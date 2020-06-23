package no.nav.medlemskap.domene

import java.time.LocalDate
import java.time.LocalDateTime

data class Personhistorikk(
        val statsborgerskap: List<Statsborgerskap>,
        val personstatuser: List<Personstatus>,
        val bostedsadresser: List<Adresse>,
        val postadresser: List<Adresse>,
        val midlertidigAdresser: List<Adresse>,
        val sivilstand: List<Sivilstand>,
        val familierelasjoner: List<Familierelasjon>
)

data class Statsborgerskap(
        val landkode: String,
        val fom: LocalDate?,
        val tom: LocalDate?
)

data class Personstatus(
        val folkeregisterPersonstatus: String,
        val fom: LocalDate?,
        val tom: LocalDate?
)

data class Adresse(
        val adresselinje: String,
        val landkode: String,
        val fom: LocalDate?,
        val tom: LocalDate?
)

data class Sivilstand(
        val type: Sivilstandstype,
        val gyldigFraOgMed: LocalDate?,
        val relatertVedSivilstand: String,
        val folkeregisterMetadata: Folkeregistermetadata
)

enum class Sivilstandstype {
    GIFT,
    ENKE_ELLER_ENKEMANN,
    SKILT,
    SEPARERT,
    REGISTRERT_PARTNER,
    SEPARERT_PARTNER,
    SKILT_PARTNER,
    GJENLEVENDE_PARTNER
}

data class Familierelasjon(
        val relatertPersonIdent: String,
        val relatertPersonsRolle: Familierelasjonsrolle,
        val minRolleForPerson: Familierelasjonsrolle,
        val folkeregisterMetadata: Folkeregistermetadata
)

enum class Familierelasjonsrolle {
    BARN
}

data class Folkeregistermetadata(
        val ajourholdstidspunkt: LocalDateTime?,
        val gyldighetstidspunkt: LocalDateTime?,
        val opphoerstidspunkt: LocalDateTime?
)

