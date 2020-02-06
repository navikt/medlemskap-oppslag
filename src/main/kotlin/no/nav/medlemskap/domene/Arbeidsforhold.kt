package no.nav.medlemskap.domene

import java.time.YearMonth

data class Arbeidsforhold (
        val periode: Periode,
        val utenlandsopphold: List<Utenlandsopphold>,
        val arbeidsgiver: Arbeidsgiver,
        val arbeidsfolholdstype: Arbeidsforholdstype,
        val arbeidsavtaler: List<Arbeidsavtale>
)

data class Arbeidsavtale (
        val periode: Periode,
        val yrkeskode: String,
        val skipsregister: Skipsregister?,
        val stillingsprosent: Double?
)

data class Arbeidsgiver (
        val type: String,
        val identifikator: String,
        val landkode: String // Må kanskje hentes fra https://data.brreg.no/enhetsregisteret/api/docs/index.html#enheter-oppslag
)

data class Utenlandsopphold (
        val landkode: String,
        val periode: Periode?,
        val rapporteringsperiode: YearMonth
)

enum class Arbeidsforholdstype (val navn: String) {
    FRILANSER("frilanserOppdragstakerHonorarPersonerMm"),
    MARITIM("maritimtArbeidsforhold"),
    NORMALT("ordinaertArbeidsforhold"),
    FORENKLET("forenkletOppgjoersordning"),
    ANDRE("pensjonOgAndreTyperYtelserUtenAnsettelsesforhold")
}

enum class Skipsregister (val beskrivelse: String) {
    nis("Norsk InternasjonaltSkipsregister"),
    nor("Norsk Ordinært Skipsregister"),
    utl("Utenlandsk skipsregister")
}
