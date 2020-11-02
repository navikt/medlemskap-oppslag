package no.nav.medlemskap.domene

import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Svar
import java.time.LocalDate

data class Request(
    val fnr: String,
    val førsteDagForYtelse: LocalDate?,
    val periode: InputPeriode,
    val brukerinput: Brukerinput,
    val ytelse: Ytelse?,
    val overstyrteRegler: Map<RegelId, Svar> = mapOf()
)

data class Brukerinput(
    val arbeidUtenforNorge: Boolean
)

data class InputPeriode(
    val fom: LocalDate,
    val tom: LocalDate
)
