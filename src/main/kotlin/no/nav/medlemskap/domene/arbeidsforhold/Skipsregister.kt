package no.nav.medlemskap.domene.arbeidsforhold

enum class Skipsregister(val beskrivelse: String) {
    NIS("Norsk InternasjonaltSkipsregister"),
    NOR("Norsk Ordinært Skipsregister"),
    UTL("Utenlandsk skipsregister");

    companion object {
        fun fraSkipsregisterVerdi(skipsregisterValue: String?): Skipsregister? {
            if (skipsregisterValue.isNullOrEmpty()) return null
            return valueOf(skipsregisterValue.uppercase())
        }
    }
}
