package no.nav.medlemskap.modell.inntekt

data class InntektskomponentResponse (
        val arbeidsInntektMaaned: List<ArbeidsinntektMaaned>
)

data class ArbeidsinntektMaaned (
        val avvikListe: List<Avvik>,
        val arbeidsInntektInformasjon: ArbeidsInntektInformasjon
)

data class Avvik (

)

data class ArbeidsInntektInformasjon (
    val inntektListe: List<Inntekt>,
    val forskuddstrekkListe: List<Forskuddstrekk>,
    val fradragListe: List<Fradrag>
)

data class Inntekt (
    val inntektType: InntektType,
    val arbeidsforholdREF: String,
    val beloep: Double
)

enum class InntektType {
    LOENNSINNTEKT,
    NAERINGSINNTEKT,
    PENSJON_ELLER_TRYGD,
    YTELSE_FRA_OFFENTLIGE
}

data class Forskuddstrekk (

)

data class Fradrag (

)
