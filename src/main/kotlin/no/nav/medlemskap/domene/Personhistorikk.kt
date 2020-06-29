package no.nav.medlemskap.domene

import java.time.LocalDate

data class Personhistorikk(
        val statsborgerskap: List<Statsborgerskap>,
        val personstatuser: List<Personstatus>,
        val bostedsadresser: List<Adresse>,
        val postadresser: List<Adresse>,
        val midlertidigAdresser: List<Adresse>
)

data class Statsborgerskap(
        val landkode: String,
        val fom: LocalDate?,
        val tom: LocalDate?
)

data class Personstatus(
        val personstatus: String,
        val fom: LocalDate?,
        val tom: LocalDate?
)

data class Adresse(
        val adresselinje: String,
        val landkode: String,
        val fom: LocalDate?,
        val tom: LocalDate?
)
