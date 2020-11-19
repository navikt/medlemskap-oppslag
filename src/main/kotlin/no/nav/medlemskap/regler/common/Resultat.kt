package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Ytelse

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
    val årsaker = finnÅrsaker()

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
                return finnÅrsak(resultat.delresultat.last())
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

        fun ja(begrunnelse: String) = Resultat(
            begrunnelse = begrunnelse,
            svar = Svar.JA
        )

        fun ja() = Resultat(svar = Svar.JA)

        fun ja(begrunnelse: String, dekning: String) = Resultat(
            begrunnelse = begrunnelse,
            svar = Svar.JA,
            harDekning = Svar.JA,
            dekning = dekning
        )

        fun nei(begrunnelse: String, dekning: String) = Resultat(
            begrunnelse = begrunnelse,
            svar = Svar.NEI,
            harDekning = Svar.NEI,
            dekning = dekning
        )

        fun nei(begrunnelse: String) = Resultat(
            begrunnelse = begrunnelse,
            svar = Svar.NEI
        )

        fun nei() = Resultat(svar = Svar.NEI)

        fun uavklart(begrunnelse: String, regelId: RegelId? = null) = Resultat(
            begrunnelse = begrunnelse,
            svar = Svar.UAVKLART,
            delresultat = if (regelId == null) emptyList() else listOf(Resultat(regelId = regelId, svar = Svar.UAVKLART))
        )

        fun uavklart() = Resultat(svar = Svar.UAVKLART)

        fun uavklartKonklusjon(ytelse: Ytelse, regelId: RegelId? = null) = Regel(
            regelId = RegelId.REGEL_MEDLEM_KONKLUSJON,
            ytelse = ytelse,
            operasjon = { uavklart("Kan ikke konkludere med medlemskap", regelId) }
        )

        fun jaKonklusjon(ytelse: Ytelse) = Regel(
            regelId = RegelId.REGEL_MEDLEM_KONKLUSJON,
            ytelse = ytelse,
            operasjon = { ja("Bruker er medlem") }
        )

        fun neiKonklusjon(ytelse: Ytelse) = Regel(
            regelId = RegelId.REGEL_MEDLEM_KONKLUSJON,
            ytelse = ytelse,
            operasjon = { nei("Bruker er ikke medlem") }
        )

        fun List<Resultat>.utenKonklusjon(): List<Resultat> {
            return this.filterNot { it.erKonklusjon() }
        }
    }
}
