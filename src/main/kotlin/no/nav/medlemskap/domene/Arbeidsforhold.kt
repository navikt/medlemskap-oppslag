package no.nav.medlemskap.domene

import no.nav.medlemskap.services.ereg.Ansatte
import java.time.YearMonth

data class Arbeidsforhold (
        val periode: Periode,
        val utenlandsopphold: List<Utenlandsopphold>?,
        val arbeidsgiver: Arbeidsgiver,
        val arbeidsfolholdstype: Arbeidsforholdstype,
        val arbeidsavtaler: List<Arbeidsavtale>
) : Comparable<Arbeidsforhold> {

    /**
     * Comparator som sorterer arbeidsforhold etter periode.
     * Null-verdier regnes som høyere, slik at aktive arbeidsforhold vil havne sist i listen.
     */
    override fun compareTo(other: Arbeidsforhold): Int {

        if (this.periode.tom == null && other.periode.tom == null) {
            return this.periode.fom?.compareTo(other.periode.fom)!! //En gyldig periode har alltid minst én dato
        }

        if (this.periode.tom == null) return 1
        if (other.periode.tom == null) return -1

        return this.periode.tom.compareTo(other.periode.tom)
    }

}

data class Arbeidsavtale (
        val periode: Periode,
        val yrkeskode: String,
        val skipsregister: Skipsregister?,
        val stillingsprosent: Double?
)

data class Arbeidsgiver(
        val type: String?,
        val landkode: String?,
        val ansatte: List<Ansatte>?,
        val konkursStatus: List<String?>?
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

enum class LuftfartYrkeskoder(val beskrivelse: String){
    PILOT("13143107"),
    KABINPERSONALE("5111105"),
    KABINSJEF("5111117")
}



