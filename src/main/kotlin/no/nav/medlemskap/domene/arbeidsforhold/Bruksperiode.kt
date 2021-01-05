package no.nav.medlemskap.domene.arbeidsforhold

import java.time.LocalDate

data class Bruksperiode(
    val fom: LocalDate?,
    val tom: LocalDate?
)
