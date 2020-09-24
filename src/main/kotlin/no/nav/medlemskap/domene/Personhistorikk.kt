package no.nav.medlemskap.domene
import java.time.LocalDate
import java.time.LocalDateTime

data class Personhistorikk(
    val statsborgerskap: List<Statsborgerskap>,
    val bostedsadresser: List<Adresse>,
    val kontaktadresser: List<Adresse>,
    val oppholdsadresser: List<Adresse>,
    val sivilstand: List<Sivilstand>,
    val familierelasjoner: List<Familierelasjon>
)

data class Statsborgerskap(
    val landkode: String,
    val fom: LocalDate?,
    val tom: LocalDate?
) {
    private val periode = Periode(fom, tom)

    fun overlapper(annenPeriode: Periode): Boolean {
        return periode.overlapper(annenPeriode)
    }

    fun overlapper(dato: LocalDate): Boolean {
        return periode.overlapper(dato)
    }

    fun encloses(annenPeriode: Periode): Boolean {
        return periode.encloses(annenPeriode)
    }

    fun encloses(dato: LocalDate): Boolean {
        return periode.encloses(dato)
    }
}

data class Adresse(
    val landkode: String,
    val fom: LocalDate?,
    val tom: LocalDate?
) {
    private val periode = Periode(fom, tom)

    fun overlapper(annenPeriode: Periode): Boolean {
        return periode.overlapper(annenPeriode)
    }
}

data class Sivilstand(
    val type: Sivilstandstype,
    val gyldigFraOgMed: LocalDate?,
    val gyldigTilOgMed: LocalDate?,
    val relatertVedSivilstand: String?,
    val folkeregistermetadata: Folkeregistermetadata?
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
    val folkeregistermetadata: Folkeregistermetadata?
)

enum class Familierelasjonsrolle {
    BARN, MOR, FAR, MEDMOR
}

data class Folkeregistermetadata(
    val ajourholdstidspunkt: LocalDateTime?,
    val gyldighetstidspunkt: LocalDateTime?,
    val opphoerstidspunkt: LocalDateTime?
)
