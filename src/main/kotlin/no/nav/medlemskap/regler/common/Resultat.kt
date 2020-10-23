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
        return finnRegelResultat(this, regelId)
    }

    fun finnÅrsak(): String {
        return finnÅrsak(this)?.begrunnelse ?: ""
    }

    fun finnÅrsaker(): List<Resultat> {
        return finnÅrsaker(this)
    }

    companion object {
        private fun finnRegelResultat(resultat: Resultat, regelId: RegelId): Resultat? {
            var regelResultat = finnDelresultat(resultat, regelId)
            if (regelResultat != null) {
                return regelResultat
            }

            resultat.delresultat.forEach { delresultat ->
                regelResultat = finnRegelResultat(delresultat, regelId)
                if (regelResultat != null) {
                    return regelResultat
                }
            }

            return regelResultat
        }

        fun finnDelresultat(resultat: Resultat, regelId: RegelId): Resultat? {
            return resultat.delresultat.find { it.regelId == regelId }
        }

        fun finnÅrsak(resultat: Resultat): Resultat? {
            if (resultat.svar == Svar.JA && resultat.erMedlemskonklusjon()) {
                return null
            }

            if (resultat.delresultat.isNotEmpty()) {
                return finnÅrsak(resultat.delresultat.last())
            }

            return resultat
        }

        fun finnÅrsaker(resultat: Resultat): List<Resultat> {
            if (resultat.svar == Svar.JA && resultat.erKonklusjon()) {
                return emptyList()
            }

            return resultat.delresultat.filter { !it.erKonklusjon() || it.erKonklusjon() && it.svar != Svar.JA }.mapNotNull { finnÅrsak(it) }
        }
    }
}
