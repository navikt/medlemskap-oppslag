package no.nav.medlemskap.domene.arbeidsforhold

import java.time.LocalDate

data class Gyldighetsperiode(
    val fom: LocalDate?,
    val tom: LocalDate?
)
