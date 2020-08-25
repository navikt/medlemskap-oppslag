package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Ytelse

abstract class Regler(val ytelse: Ytelse, val regelMap: Map<RegelId, Regel> = emptyMap()) {
    private val resultater: MutableList<Resultat> = mutableListOf()

    abstract fun hentRegelflyt(): Regelflyt

    fun kjørRegelflyt(): Resultat {
        return hentRegelflyt().utfør(resultater)
    }

    fun kjørRegelflyt(resultatliste: MutableList<Resultat>): Resultat {
        return hentRegelflyt().utfør(resultatliste)
    }


    protected fun lagRegelflyt(regel: Regel, hvisJa: Regelflyt? = null, hvisNei: Regelflyt? = null, hvisUavklart: Regelflyt = konklusjonUavklart(ytelse)): Regelflyt {
        return Regelflyt(regel = regel, ytelse = ytelse, hvisJa = hvisJa, hvisNei = hvisNei, hvisUavklart = hvisUavklart)
    }

    fun hentRegel(regelId: RegelId): Regel {
        val regel = regelMap[regelId]

        return regel ?: throw RuntimeException("Fant ikke regel med regelId $regelId")
    }

    companion object {
        private fun utledResultat(resultater: Map<RegelId, Resultat>): Resultat {

            return when {
                resultater[RegelId.REGEL_A]?.svar == Svar.JA && resultater[RegelId.REGEL_B]?.svar == Svar.NEI -> ja()
                resultater.values.any { it.svar == Svar.JA } -> uavklart()
                else -> nei("Alle de følgende ble NEI")
            }
        }

        fun minstEnAvDisse(vararg regler: Regel): Resultat {
            val delresultatMap = regler.map { it.regelId to it.utfør() }.toMap()

            return utledResultat(delresultatMap).copy(
                    delresultat = delresultatMap.values.toList()
            )
        }
    }
}
