package no.nav.medlemskap.regler.common

enum class Svar {
    JA, NEI, UAVKLART
}

data class Resultat(
        val regelId: RegelId? = null,
        val avklaring: String = "",
        val begrunnelse: String = "",
        val svar: Svar,
        var harDekning: Svar? = null,
        var dekning: String = "",
        val delresultat: List<Resultat> = listOf()
) {
    fun erMedlemskonklusjon(): Boolean {
        return regelId == RegelId.REGEL_MEDLEM_KONKLUSJON
    }

}
