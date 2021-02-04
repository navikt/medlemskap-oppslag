package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonUavklart
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.common.Resultat.Companion.uavklart
import no.nav.medlemskap.regler.common.Svar.JA
import no.nav.medlemskap.regler.common.Svar.NEI

abstract class Regler(
    val ytelse: Ytelse,
    val regelMap: Map<RegelId, Regel> = emptyMap(),
    val overstyrteRegler: Map<RegelId, Svar>
) {

    abstract fun hentHovedflyt(): Regelflyt

    fun kjørHovedflyt(): Resultat {
        return kjørRegelflyt(hentHovedflyt())
    }

    protected fun kjørRegelflyt(regelflyt: Regelflyt): Resultat {
        return regelflyt.utfør()
    }

    protected fun lagRegelflyt(
        regel: Regel,
        hvisJa: Regelflyt? = null,
        hvisNei: Regelflyt? = null,
        hvisUavklart: Regelflyt = konklusjonUavklart(ytelse)
    ): Regelflyt {
        return Regelflyt(
            regel = regel,
            ytelse = ytelse,
            hvisJa = hvisJa,
            hvisNei = hvisNei,
            hvisUavklart = hvisUavklart,
            overstyrteRegler = overstyrteRegler
        )
    }

    protected fun hentRegel(regelId: RegelId): Regel {
        val regel = regelMap[regelId]

        return regel ?: throw RuntimeException("Fant ikke regel med regelId $regelId")
    }

    companion object {
        private fun utledResultat(resultater: Map<RegelId, Resultat>): Resultat {

            return when {
                resultater[RegelId.REGEL_A]?.svar == JA && resultater[RegelId.REGEL_B]?.svar == NEI -> ja(RegelId.REGEL_OPPLYSNINGER)
                resultater.values.any { it.svar == JA } -> uavklart(RegelId.REGEL_OPPLYSNINGER)
                else -> nei(RegelId.REGEL_OPPLYSNINGER)
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
