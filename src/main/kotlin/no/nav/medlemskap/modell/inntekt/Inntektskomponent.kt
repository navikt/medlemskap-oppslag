package no.nav.medlemskap.modell.inntekt

import java.math.BigDecimal
import java.time.LocalDate
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
    val arbeidsforholdListe: List<Arbeidsforhold>,
    val inntektListe: List<Inntekt>,
    val forskuddstrekkListe: List<Forskuddstrekk>,
    val fradragListe: List<Fradrag>
)

data class Arbeidsforhold (
        val antallTimerPerUkeSomEnFullStillingTilsvarer: Double?,
        val arbeidstidsordning: String?,
        val avloenningstype: String?,
        val sisteDatoForStillingsprosentendring: LocalDate?,
        val sisteLoennsendring: LocalDate?,
        val frilansperiodeFom: LocalDate?,
        val frilansperiodeTom: LocalDate?,
        val stillingsprosent: Double?,
        val yrke: String?,
        val arbeidsforholdID: String?,
        val arbeidsforholdIDnav: String?,
        val arbeidsforholdType: String?,
        val arbeidsgiver: Ident,
        val arbeidstaker: Ident
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
