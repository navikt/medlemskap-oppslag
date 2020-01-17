package no.nav.medlemskap.modell.inntekt

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.YearMonth


data class InntektskomponentResponse (
        val arbeidsInntektMaaned: List<ArbeidsinntektMaaned>
)

data class ArbeidsinntektMaaned (
        val avvikListe: List<Avvik>,
        val arbeidsInntektInformasjon: ArbeidsInntektInformasjon
)

data class Avvik (
        val ident : Ident,
        val opplysningspliktig: Ident,
        val virksomhet: Ident,
        val avvikPeriode: String,
        val tekst: String

)

data class Ident (
    val identifikator: String,
    val aktoerType: String
)

data class ArbeidsInntektInformasjon (
    val inntektListe: List<Inntekt>,
    val forskuddstrekkListe: List<Forskuddstrekk>,
    val fradragListe: List<Fradrag>
)

data class Inntekt (
    val inntektType: InntektType,
    val arbeidsforholdREF: String?,
    val beloep: Double
)

enum class InntektType {
    LOENNSINNTEKT,
    NAERINGSINNTEKT,
    PENSJON_ELLER_TRYGD,
    YTELSE_FRA_OFFENTLIGE
}

data class Forskuddstrekk(
    val beloep: Int,
    val beskrivelse: String?,
    val leveringstidspunkt: LocalDateTime,
    val opplysningspliktig: Ident,
    val utbetaler: Ident?,
    val forskuddstrekkGjelder: Ident?
)

data class Fradrag (
    val beloep: BigDecimal,
    val beskrivelse: String?,
    val fradragsperiode: String,
    val leveringstidspunkt: LocalDateTime,
    val inntektspliktig: Ident,
    val utbetaler: Ident,
    val fradragGjelder: Ident
)

//val versjon: String
