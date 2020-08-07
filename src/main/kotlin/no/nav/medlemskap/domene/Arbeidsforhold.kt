package no.nav.medlemskap.domene

import no.nav.medlemskap.services.aareg.AaRegOpplysningspliktigArbeidsgiverType
import no.nav.medlemskap.services.ereg.Ansatte
import java.time.YearMonth

data class Arbeidsforhold(
        val periode: Periode,
        val utenlandsopphold: List<Utenlandsopphold>?,
        val arbeidsgivertype: AaRegOpplysningspliktigArbeidsgiverType,
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

data class Arbeidsavtale(
        val periode: Periode,
        val yrkeskode: String,
        val skipsregister: Skipsregister?,
        val stillingsprosent: Double?
)

data class Arbeidsgiver(
        val type: String?,
        val identifikator: String?,
        val statsborgerskap: List<Statsborgerskap>?,
        val ansatte: List<Ansatte>?,
        val konkursStatus: List<String?>?
)

data class Utenlandsopphold(
        val landkode: String,
        val periode: Periode?,
        val rapporteringsperiode: YearMonth
)

enum class Arbeidsforholdstype(val navn: String) {
    FRILANSER("frilanserOppdragstakerHonorarPersonerMm"),
    MARITIM("maritimtArbeidsforhold"),
    NORMALT("ordinaertArbeidsforhold"),
    FORENKLET("forenkletOppgjoersordning"),
    ANDRE("pensjonOgAndreTyperYtelserUtenAnsettelsesforhold")
}

enum class Skipsregister(val beskrivelse: String) {
    NIS("Norsk InternasjonaltSkipsregister"),
    NOR("Norsk Ordinært Skipsregister"),
    UTL("Utenlandsk skipsregister"),
    UKJENT("Ingen verdi eller ukjent verdi")
}

enum class YrkeskoderForLuftFart(val styrk: String) {
    //5111-rangen:
    DAGLIG_LEDER_VERTER_VERTINNER_PÅ_FLY("5111013"),
    FLY_OG_TOGVERT("5111100"),
    FLYVERT("5111103"),
    FLYVERTINNE("5111104"),
    KABINPERSONALE("5111105"),
    AIRPURSER("5111106"),
    HOST_LUFTFARTØY("5111107"),
    CABIN_ATTENDANT("5111108"),
    STEWARDESS_LUFTFARTØY("5111109"),
    PURSERASSISTENT_TRANSPORT("5111110"),
    PURSER_TRANSPORT("5111114"),
    CABIN_CHIEF("5111115"),
    AIRSTEWARD("5111116"),
    KABINSJEF("5111117"),
    STEWARD_LUFTFARTØY("5111119"),
    HOSTESS_LUFTFARTØY("5111122"),
    FLYVERTLÆRLING("5111124"),

    //3143-rangen:
    FLYGER_OVERORDNET("3143100"),
    LEDER_FLYGERE("3143001"),
    DAGLIG_LEDER_FLYGERE("3143013"),
    ANNENFLYGER("3143101"),
    FLYGER("3143102"),
    FLYNAVIGATØR("3143103"),
    NAVIGATØR_FLY("3143104"),
    SJEKKFLYGER("3143105"),
    KONTROLLFLYGER("3143106"),
    PILOT("3143107"),
    FLYKAPTEIN("3143108"),
    FLYSTYRMANN("3143109"),
    TRAFIKKFLYGER("3143110")

}



