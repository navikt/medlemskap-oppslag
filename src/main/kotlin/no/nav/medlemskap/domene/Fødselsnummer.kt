package no.nav.medlemskap.domene

data class Fødselsnummer(val fnr: String) {

    fun hentBursdagsAar(): String {
        return hentAarHundre() + hent2DigitBursdagsAar()
    }

    private fun hentAarHundre(): String? {
        val individnummer: Int = getIndividnummer().toInt()
        val birthYear: Int = hent2DigitBursdagsAar().toInt()

        return when {
            individnummer <= 499 -> "19"
            individnummer >= 500 && birthYear < 40 -> "20"
            individnummer in 500..749 && birthYear >= 54 -> "18"
            individnummer >= 900 && birthYear > 39 -> "19"
            else -> null
        }
    }

    private fun hent2DigitBursdagsAar() = fnr.substring(4, 6)

    private fun getIndividnummer() = fnr.substring(6, 9)

    companion object {
        fun String.hentBursdagsAar(): String {
            val fødselsnummer = Fødselsnummer(this)
            return fødselsnummer.hentAarHundre() + fødselsnummer.hent2DigitBursdagsAar()
        }
    }
}
