package no.nav.medlemskap.domene.arbeidsforhold

data class Ansatte(
    val antall: Int?,
    val bruksperiode: Bruksperiode?,
    val gyldighetsperiode: Gyldighetsperiode?
)
