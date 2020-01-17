package no.nav.medlemskap.modell.inntekt

import no.nav.tjeneste.virksomhet.person.v3.informasjon.Aktoer
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
        val ident : Aktoer,
        val opplysningspliktig: Aktoer,
        val virksomhet: Aktoer,
        val avvikPeriode: String,
        val tekst: String

)


data class Ident (
    val identifikator: String,
    val aktoerTypde: String
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

data class Forskuddstrekk(
    val beloep: Int,
    val beskrivelse: String,
    val leveringstidspunkt: LocalDateTime,
    val opplysningspliktig: Aktoer,
    val utbetaler: Aktoer,
    val forskuddstrekkGjelder: Aktoer
)

data class Fradrag (
    val beloep: BigDecimal,
    val beskrivelse: String,
    val fradragsperiode: String,
    val leveringstidspunkt: LocalDateTime,
    val inntektspliktig: Aktoer,
    val utbetaler: Aktoer,
    val fradragGjelder: Aktoer
)

//val versjon: String