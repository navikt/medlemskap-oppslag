package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.Svar.JA
import no.nav.medlemskap.regler.common.Svar.NEI

abstract class Regler(val ytelse: Ytelse, val regelMap: Map<RegelId, Regel> = emptyMap()) {

    abstract fun hentRegelflyter(): List<Regelflyt>

    open fun kjørRegelflyter(): List<Resultat> {
        return hentRegelflyter().map { kjørRegelflyt(it) }
    }

    protected fun kjørRegelflyt(regelflyt: Regelflyt): Resultat {
        return regelflyt.utfør()
    }

    protected fun lagRegelflyt(
        regel: Regel,
        hvisJa: Regelflyt? = null,
        hvisNei: Regelflyt? = null,
        hvisUavklart: Regelflyt = konklusjonUavklart(ytelse),
        regelIdForSammensattResultat: RegelId? = null
    ): Regelflyt {
        return Regelflyt(
            regel = regel,
            ytelse = ytelse,
            hvisJa = hvisJa,
            hvisNei = hvisNei,
            hvisUavklart = hvisUavklart,
            regelIdForSammensattResultat = regelIdForSammensattResultat
        )
    }

    protected fun hentRegel(regelId: RegelId): Regel {
        val regel = regelMap[regelId]

        return regel ?: throw RuntimeException("Fant ikke regel med regelId $regelId")
    }

    companion object {
        private fun utledResultat(resultater: Map<RegelId, Resultat>): Resultat {

            return when {
                resultater[RegelId.REGEL_A]?.svar == JA && resultater[RegelId.REGEL_B]?.svar == NEI -> ja()
                resultater.values.any { it.svar == JA } -> uavklart()
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
