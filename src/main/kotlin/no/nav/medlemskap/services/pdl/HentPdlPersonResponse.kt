package no.nav.medlemskap.services.pdl
import java.time.LocalDate
import java.time.LocalDateTime


data class HentPdlPersonResponse (val data: HentPerson?, val errors: List<PdlError>?)

data class HentPerson(val hentPerson: Person)
data class Person(
        val adressebeskyttelse: List<Adressebeskyttelse>,
        val kjoenn: List<Kjoenn>,
        val navn: List<Navn>,
        val statsborgerskap: List<Statsborgerskap>,
        val bostedsadresse: List<Bostedsadresse>)

data class Statsborgerskap(
        val land: String?,
        val gyldigFraOgMed: LocalDate,
        val gyldigTilOgMed: LocalDate
)

data class Adressebeskyttelse(
        val gradering: AdressebeskyttelseGradering
)


data class Kjoenn(
        val kjoenn: KjoennType
)

enum class KjoennType{
    MANN,
    KVINNE,
    UKJENT,
}


enum class AdressebeskyttelseGradering {
    STRENGT_FORTROLIG,
    FORTROLIG,
    UGRADERT,
}


data class Navn (
        val fornavn: String,
        val mellomnavn: String?,
        val etternavn: String? )

data class Bostedsadresse(
        val angittFlyttedato: LocalDate?,
        val coAdressenavn: String?,
        val vegAdresse: VegAdresse?,
        val adresse: String?,
        val matrikkeladresse: Matrikkeladresse?,
        val ukjentBosted: UkjentBosted?,
        val folkeregisterMetadata: Folkeregistermetadata,
        val metadata: Metadata)


data class Metadata(
        val opplysningsId: String?,
        val master: String,
        val endringer: List<Endring>
)

enum class Endringstype {
    OPPRETT,
    KORRIGER,
    OPPHOER,
}

data class Endring(
        val type: Endringstype?,
        val registrert: LocalDateTime?,
        val registrertAv: String?,
        val systemKilde: String?,
        val kilde: String?)

data class Folkeregistermetadata(
        val ajourholdstidspunkt: LocalDateTime?,
        val gyldighetstidspunkt: LocalDateTime?,
        val opphoerstidspunkt: LocalDateTime?,
        val kilde:  String?,
        val aarsak: String?,
        val sekvens:  Int?
)

data class Matrikkeladresse(
        val matrikkelId: Int?,
        val bruksenhetsnummer: String?,
        val tilleggsnavn: String?,
        val postnummer: String?,
        val kommunenummer: String?,
        val koordinater: Koordinater?
)

data class VegAdresse(
        val matrikkelId: Int?,
        val husNummer: String?,
        val husBokstav: String?,
        val bruksenhetnummer: String?,
        val adressenavn: String?,
        val kommunenummer: String?,
        val tilleggsnavn: String?,
        val postnummer: String?,
        val koordinater: Koordinater?
)

data class UkjentBosted(
        val bostedskommune: String?
)

data class Koordinater(
        val x: Float?,
        val y: Float?,
        val z: Float?,
        val kvalitet: Int?
)
