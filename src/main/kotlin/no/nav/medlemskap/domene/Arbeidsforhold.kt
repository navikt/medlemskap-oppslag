package no.nav.medlemskap.domene
import java.time.YearMonth

data class Arbeidsforhold(
    val periode: Periode,
    val utenlandsopphold: List<Utenlandsopphold>?,
    val arbeidsgivertype: OpplysningspliktigArbeidsgiverType,
    val arbeidsgiver: Arbeidsgiver,
    val arbeidsforholdstype: Arbeidsforholdstype,
    var arbeidsavtaler: List<Arbeidsavtale>,
    val permisjonPermittering: List<PermisjonPermittering>?
) : Comparable<Arbeidsforhold> {

    /**
     * Comparator som sorterer arbeidsforhold etter periode.
     * Null-verdier regnes som høyere, slik at aktive arbeidsforhold vil havne sist i listen.
     */
    override fun compareTo(other: Arbeidsforhold): Int {

        if (this.periode.fom != null && other.periode.fom != null && this.periode.fom != other.periode.fom) {
            return this.periode.fom.compareTo(other.periode.fom)
        }

        if (this.periode.tom == null && other.periode.tom == null) {
            return this.periode.fom?.compareTo(other.periode.fom)!! // En gyldig periode har alltid minst én dato
        }

        if (this.periode.tom == null) return 1
        if (other.periode.tom == null) return -1

        return this.periode.tom.compareTo(other.periode.tom)
    }
}

data class Arbeidsavtale(
    var periode: Periode,
    val gyldighetsperiode: Periode,
    val yrkeskode: String,
    val skipsregister: Skipsregister?,
    val stillingsprosent: Double?,
    val beregnetAntallTimerPrUke: Double?
) {
    fun getStillingsprosent(): Double {
        if (stillingsprosent == 0.0 && beregnetAntallTimerPrUke != null && beregnetAntallTimerPrUke > 0) {
            val beregnetStillingsprosent = (beregnetAntallTimerPrUke / 37.5) * 100
            return Math.round(beregnetStillingsprosent * 10.0) / 10.0
        }

        return stillingsprosent ?: 100.0
    }
}

data class Arbeidsgiver(
    val organisasjonsnummer: String?,
    val ansatte: List<Ansatte>?,
    val konkursStatus: List<String?>?,
    val juridiskeEnheter: List<JuridiskEnhet?>?
)

data class PermisjonPermittering(
    val periode: Periode,
    val permisjonPermitteringId: String,
    val prosent: Double?,
    val type: PermisjonPermitteringType,
    val varslingskode: String?
)

data class JuridiskEnhet(
    val organisasjonsnummer: String?,
    val enhetstype: String?,
    val antallAnsatte: Int?
)

data class Utenlandsopphold(
    val landkode: String,
    val periode: Periode?,
    val rapporteringsperiode: YearMonth
)

enum class Arbeidsforholdstype(val kodeverdi: String) {
    FRILANSER("frilanserOppdragstakerHonorarPersonerMm"),
    MARITIMT("maritimtArbeidsforhold"),
    NORMALT("ordinaertArbeidsforhold"),
    FORENKLET("forenkletOppgjoersordning"),
    ANDRE("pensjonOgAndreTyperYtelserUtenAnsettelsesforhold");

    companion object {
        fun fraArbeidsforholdtypeVerdi(arbeidsforholdstypeVerdi: String): Arbeidsforholdstype {
            return values().first { it.kodeverdi == arbeidsforholdstypeVerdi }
        }
    }
}

enum class PermisjonPermitteringType(val kodeverdi: String) {
    PERMISJON("permisjon"),
    PERMISJON_MED_FORELDREPENGER("permisjonMedForeldrepenger"),
    PERMISJON_VED_MILITAERTJENESTE("permisjonVedMilitaertjeneste"),
    PERMITTERING("permittering"),
    UTDANNINGSPERMISJON("utdanningspermisjon"),
    VELFERDSPERMISJON("velferdspermisjon"),
    ANNET("Annet")
    ;

    companion object {
        fun fraPermisjonPermitteringVerdi(permisjonPermittering: String): PermisjonPermitteringType {
            return PermisjonPermitteringType.values().first { it.kodeverdi == permisjonPermittering }
        }
    }
}

enum class Skipsregister(val beskrivelse: String) {
    NIS("Norsk InternasjonaltSkipsregister"),
    NOR("Norsk Ordinært Skipsregister"),
    UTL("Utenlandsk skipsregister");

    companion object {
        fun fraSkipsregisterVerdi(skipsregisterValue: String?): Skipsregister? {
            if (skipsregisterValue.isNullOrEmpty()) return null
            return valueOf(skipsregisterValue.toUpperCase())
        }
    }
}

enum class YrkeskoderForLuftFart(val styrk: String) {
    // 5111-rangen:
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

    // 3143-rangen:
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
