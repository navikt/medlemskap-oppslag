package no.nav.medlemskap.domene

data class Inntekt(
    val arbeidsforhold: List<InntektArbeidsforhold>
)

data class InntektArbeidsforhold(
    val yrke: String?
)
