package no.nav.medlemskap.regler.common

enum class Svar {
    JA, NEI, UAVKLART
}

data class Resultat(
        val identifikator: String = "",
        val avklaring: String = "",
        val begrunnelse: String = "",
        val svar: Svar,
        var harDekning: Svar? = null,
        var dekning: List<String> = listOf(),
        val delresultat: List<Resultat> = listOf()
)
