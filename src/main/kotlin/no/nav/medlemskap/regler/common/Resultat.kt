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

    fun erRegelflytKonklusjon(): Boolean {
        return regelId?.erRegelflytKonklusjon ?: false
    }

    fun erKonklusjon(): Boolean {
        return erMedlemskonklusjon() || erRegelflytKonklusjon()
    }

    fun finnRegelResultat(regelId: RegelId): Resultat? {
        val regelResultat = delresultat.find { it.regelId == regelId }
        if (regelResultat != null) {
            return regelResultat
        }

        return regelResultat
    }

    private fun finnRegelResultat(resultat: Resultat, regelId: RegelId): Resultat? {
        var regelResultat = resultat.delresultat.find { it.regelId == regelId }
        if (regelResultat != null) {
            return regelResultat
        }

        resultat.delresultat.forEach{ delresultat ->
            regelResultat = delresultat.delresultat.find { it.regelId == regelId }
            if (regelResultat != null) {
                return regelResultat
            }
        }

        return regelResultat
    }
}
