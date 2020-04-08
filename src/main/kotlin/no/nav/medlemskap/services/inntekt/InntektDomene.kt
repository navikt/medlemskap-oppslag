package no.nav.medlemskap.services.inntekt
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

data class HentInntektListeRequest(
        val ident: Ident,
        val ainntektsfilter: String,
        val maanedFom: String?,
        val maanedTom: String?,
        val formaal: String
)

data class InntektskomponentResponse (
        val arbeidsInntektMaaned: List<ArbeidsinntektMaaned>?,

        val ident: Ident?
)

data class ArbeidsinntektMaaned (
        val aarMaaned: YearMonth,
        val avvikListe: List<Avvik>?,
        val arbeidsInntektInformasjon: ArbeidsInntektInformasjon
)

data class Avvik (
        val ident: Ident,
        val opplysningspliktig: Ident,
        val virksomhet: Ident,
        val avvikPeriode: YearMonth,
        val tekst: String

)

data class Ident (
    val identifikator: String,
    val aktoerType: String
)


data class ArbeidsInntektInformasjon (
        val arbeidsforholdListe: List<ArbeidsforholdFrilanser>?,
        val inntektListe: List<Inntekt>,
        val forskuddstrekkListe: List<Forskuddstrekk>?,
        val fradragListe: List<Fradrag>?

)

data class ArbeidsforholdFrilanser (
        val antallTimerPerUkeSomEnFullStillingTilsvarer: Double?,
        val arbeidstidsordning: String?,
        val avloenningstype: String?,
        val sisteDatoForStillingsprosentendring: LocalDate?,
        val sisteLoennsendring: LocalDate?,
        val frilansPeriodeFom: LocalDate?,
        val frilansPeriodeTom: LocalDate?,
        val stillingsprosent: Double?,
        val yrke: String?,
        val arbeidsforholdID: String?,
        val arbeidsforholdIDnav: String?,
        val arbeidsforholdstype: String?,
        val arbeidsgiver: Ident?,
        val arbeidstaker: Ident?
)

data class Arbeidsforhold (
        val antallTimerPerUkeSomEnFullStillingTilsvarer: Double?,
        val arbeidstidsordning: String?,
        val avloenningstype: String?,
        val sisteDatoForStillingsprosentendring: YearMonth?,
        val sisteLoennsendring: YearMonth?,
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
        val inntektType: String,
        val beloep: Double,
        val fordel: String,
        val inntektskilde: String,
        val inntektsperiodetype: String,
        val inntektsstatus: String,
        val leveringstidspunkt: YearMonth,
        val utbetaltIMaaned: YearMonth,
        val arbeidsforholdREF: String?,
        val opplysningspliktig: Ident,
        val virksomhet: Ident,
        val inntektsmottaker: Ident,
        val inngaarIGrunnlagForTrekk: Boolean,
        val utloeserArbeidsgiveravgift: Boolean,
        val informasjonsstatus: String,
        val beskrivelse: String,
        val skatteOgAvgiftsregel: String?,
        val opptjeningsland: String?,
        val opptjeningsperiodeFom: LocalDate?,
        val opptjeningsperiodeTom: LocalDate?,
        val skattemessigBosattLand: String?,
        val tilleggsinformasjon: Tilleggsinformasjon?


)

enum class InntektType {
    LOENNSINNTEKT,
    NAERINGSINNTEKT,
    PENSJON_ELLER_TRYGD,
    YTELSE_FRA_OFFENTLIGE
}

enum class TilleggsinformasjonDetaljerType {
    ALDERSUFOEREETTERLATTEAVTALEFESTETOGKRIGSPENSJON,
    BARNEPENSJONOGUNDERHOLDSBIDRAG,
    BONUSFRAFORSVARET,
    ETTERBETALINGSPERIODE,
    INNTJENINGSFORHOLD,
    REISEKOSTOGLOSJI,
    SVALBARDINNTEKT
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


data class Tilleggsinformasjon(
        val kategori: String,
        val tilleggsinformasjonDetaljer: TilleggsinformasjonDetaljer
)

data class TilleggsinformasjonDetaljer(
        val detaljerType: TilleggsinformasjonDetaljerType
)

//val versjon: String
