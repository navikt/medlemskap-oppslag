package no.nav.medlemskap.regler.v2

data class Regelkjede(
        val spørsmål: Spørsmål,
        val nesteHvisJa: Regelkjede? = null,
        val nesteHvisNei: Regelkjede? = null
) {
    fun utfør(): Spørsmål {
        val svar = spørsmål.utfør()

        return svar
    }
}
