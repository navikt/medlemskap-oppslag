package no.nav.medlemskap.regler.common

enum class Svar {
    JA, NEI, UAVKLART
}

data class Resultat(
        val identifikator: String = "",
        val avklaring: String = "",
        val begrunnelse: String = "",
        val svar: Svar,
        val delresultat: List<Resultat> = listOf()
)
