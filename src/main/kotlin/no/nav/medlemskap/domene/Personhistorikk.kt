package no.nav.medlemskap.domene

import java.time.LocalDate
import java.time.LocalDateTime

data class Personhistorikk(
        val statsborgerskap: List<Statsborgerskap>,
        val personstatuser: List<FolkeregisterPersonstatus>,
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

data class FolkeregisterPersonstatus(
        val personstatus: PersonStatus,
        val fom: LocalDate?,
        val tom: LocalDate?
)

enum class PersonStatus(s: String) {
    ABNR("Aktivt BOSTNR"),
    ADNR("Aktivt"),
    BOSA("Bosatt"),
    DØD("Død"),
    DØDD("Død"),
    FØDR("Fødselsregistrert"),
    FOSV("Forsvunnet/savnet"),
    UFUL("Ufullstendig fødselsnr"),
    UREG("Uregistrert person"),
    UTAN("Utgått person annullert tilgang Fnr"),
    UTPE("Utgått person"),
    UTVA("Utvandret"),
    UKJENT("Ukjent verdi fra register")
}

data class Adresse(
        val landkode: String,
        val fom: LocalDate?,
        val tom: LocalDate?
)

data class Sivilstand(
        val type: Sivilstandstype,
        val gyldigFraOgMed: LocalDate?,
        val relatertVedSivilstand: String,
        val folkeregisterMetadata: Folkeregistermetadata?
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
        val relatertPersonsIdent: String,
        val relatertPersonsRolle: Familierelasjonsrolle,
        val minRolleForPerson: Familierelasjonsrolle?,
        val folkeregisterMetadata: Folkeregistermetadata?
)

enum class Familierelasjonsrolle {
    BARN, MOR, FAR, MEDMOR
}

data class Folkeregistermetadata(
        val ajourholdstidspunkt: LocalDateTime?,
        val gyldighetstidspunkt: LocalDateTime?,
        val opphoerstidspunkt: LocalDateTime?
)

