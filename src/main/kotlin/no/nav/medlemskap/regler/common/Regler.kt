package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.Svar.*

abstract class Regler(val ytelse: Ytelse, val regelMap: Map<RegelId, Regel> = emptyMap()) {

    abstract fun hentRegelflyter(): List<Regelflyt>

    open fun kjørRegelflyter(): List<Resultat> {
        return hentRegelflyter().map { kjørRegelflyt(it) }
    }

    protected fun kjørUavhengigeRegelflyterMedEttResultat(regelId: RegelId): Resultat {
        val resultater = hentRegelflyter().map { kjørRegelflyt(it) }

        if (resultater.all { it.svar == JA }) {
            return regelflytResultat(JA, regelId, resultater)
        }

        if (resultater.any { it.svar == NEI }) {
            return regelflytResultat(NEI, regelId, resultater)
        }

        return regelflytResultat(UAVKLART, regelId, resultater)
    }

    protected fun kjørRegelflyt(regelflyt: Regelflyt): Resultat {
        val resultater: MutableList<Resultat> = mutableListOf()

        val konklusjon = regelflyt.utfør(resultater)
        return konklusjon.copy(delresultat = resultater.utenKonklusjon())
    }

    protected fun lagRegelflyt(regel: Regel, hvisJa: Regelflyt? = null, hvisNei: Regelflyt? = null, hvisUavklart: Regelflyt = konklusjonUavklart(ytelse)): Regelflyt {
        return Regelflyt(regel = regel, ytelse = ytelse, hvisJa = hvisJa, hvisNei = hvisNei, hvisUavklart = hvisUavklart)
    }

    protected fun hentRegel(regelId: RegelId): Regel {
        val regel = regelMap[regelId]

        return regel ?: throw RuntimeException("Fant ikke regel med regelId $regelId")
    }

    private fun regelflytResultat(svar: Svar, regelId: RegelId, resultater: List<Resultat>): Resultat {
        val regel = when (svar) {
            JA -> regelflytJaKonklusjon(ytelse, regelId)
            NEI -> regelflytNeiKonklusjon(ytelse, regelId)
            else -> regelflytUavklartKonklusjon(ytelse, regelId)
        }

        return regel.utfør().copy(delresultat = resultater.flatMap { it.delresultat })
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
