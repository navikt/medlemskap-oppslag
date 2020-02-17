package no.nav.medlemskap.domene

import java.time.LocalDate

data class Request(
        val fnr: String,
        val periode: InputPeriode,
        val brukerinput: Brukerinput
)

data class Brukerinput(
        val arbeidUtenforNorge: Boolean
)

data class InputPeriode(
        val fom: LocalDate,
        val tom: LocalDate
)
