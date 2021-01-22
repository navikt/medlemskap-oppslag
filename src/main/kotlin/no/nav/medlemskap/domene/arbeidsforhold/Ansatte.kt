package no.nav.medlemskap.domene.arbeidsforhold

import no.nav.medlemskap.domene.Periode

data class Ansatte(
    val antall: Int?,
    val bruksperiode: Periode?,
    val gyldighetsperiode: Periode?
)
