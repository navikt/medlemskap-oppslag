package no.nav.medlemskap.regler.common

enum class Svar {
    JA, NEI, UAVKLART
}

data class Resultat(
    val regelId: RegelId,
    val avklaring: String = "",
    val begrunnelse: String = "",
    val svar: Svar,
    var harDekning: Svar? = null,
    var dekning: String = "",
    val delresultat: List<Resultat> = listOf()
) {
    val årsaker = finnÅrsaker()

    fun erMedlemskonklusjon(): Boolean {
        return regelId == RegelId.REGEL_MEDLEM_KONKLUSJON
    }

    fun erRegelflytKonklusjon(): Boolean {
        return regelId.erRegelflytKonklusjon
    }

    fun erKonklusjon(): Boolean {
        return erMedlemskonklusjon() || erRegelflytKonklusjon()
    }

    fun finnRegelResultat(regelId: RegelId): Resultat? {
        return finnRegelResultat(this, regelId)
    }

    fun årsaksTekst(): String {
        val årsak = finnÅrsak(this)

        return årsak?.beskrivelse ?: ""
    }

    fun finnÅrsaker(): List<Årsak> {
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

        fun finnÅrsak(resultat: Resultat): Årsak? {
            if (resultat.svar == Svar.JA && resultat.erKonklusjon()) {
                return null
            }

            if (resultat.delresultat.isNotEmpty()) {
                val muligÅrsak = resultat.delresultat.filter { it.begrunnelse != "" }.firstOrNull()

                return if (muligÅrsak == null) {
                    finnÅrsak(resultat.delresultat.last())
                } else {
                    finnÅrsak(muligÅrsak)
                }
            }

            return Årsak.fraResultat(resultat)
        }

        fun finnÅrsaker(resultat: Resultat): List<Årsak> {
            if (resultat.svar == Svar.JA && resultat.erKonklusjon()) {
                return emptyList()
            }

            if (resultat.erMedlemskonklusjon()) {
                return resultat
                    .delresultat.filter { !it.erKonklusjon() || it.erKonklusjon() && it.svar != Svar.JA }
                    .mapNotNull { finnÅrsak(it) }
            }

            if (resultat.erRegelflytKonklusjon() && resultat.delresultat.isNotEmpty()) {
                val sisteResultat = resultat.delresultat.last()

                return listOf(finnÅrsak(sisteResultat)).filterNotNull()
            }

            return emptyList()
        }

        fun ja(regelId: RegelId) = Resultat(
            regelId = regelId,
            begrunnelse = regelId.jaBegrunnelse,
            svar = Svar.JA
        )

        fun ja(regelId: RegelId, dekning: String) = Resultat(
            regelId = regelId,
            begrunnelse = regelId.jaBegrunnelse,
            svar = Svar.JA,
            harDekning = Svar.JA,
            dekning = dekning
        )

        fun nei(regelId: RegelId, dekning: String) = Resultat(
            regelId = regelId,
            begrunnelse = regelId.neiBegrunnelse,
            svar = Svar.NEI,
            harDekning = Svar.NEI,
            dekning = dekning
        )

        fun nei(regelId: RegelId) = Resultat(
            regelId,
            begrunnelse = regelId.neiBegrunnelse,
            svar = Svar.NEI
        )

        fun uavklart(regelId: RegelId) = Resultat(
            regelId,
            begrunnelse = regelId.uavklartBegrunnelse,
            svar = Svar.UAVKLART
        )

        fun uavklartKonklusjon(regelId: RegelId? = null) = Resultat(
            regelId = RegelId.REGEL_MEDLEM_KONKLUSJON,
            begrunnelse = RegelId.REGEL_MEDLEM_KONKLUSJON.neiBegrunnelse,
            svar = Svar.UAVKLART,
            delresultat = if (regelId == null) emptyList() else listOf(Resultat(regelId = regelId, svar = Svar.UAVKLART))
        )

        fun List<Resultat>.utenKonklusjon(): List<Resultat> {
            return this.filterNot { it.erKonklusjon() }
        }
    }
}
