package no.nav.medlemskap.regler.common

abstract class Regler {

    abstract fun hentHovedRegel(): Regel

    protected fun minstEnAvDisse(vararg regler: Regel): Resultat {
        val delresultatMap = regler.map { it.regelId to it.utfør() }.toMap()

        return utledResultat(delresultatMap).copy(
                delresultat = delresultatMap.values.toList()
        )
    }

    protected fun sjekkRegel(metode: () -> Regel) = metode.invoke()

    private fun utledResultat(resultater: Map<RegelId, Resultat>): Resultat {

        return when {
            resultater[RegelId.REGEL_A]?.svar == Svar.JA && resultater[RegelId.REGEL_B]?.svar == Svar.NEI -> ja()
            resultater.values.any { it.svar == Svar.JA } -> uavklart()
            else -> nei("Alle de følgende ble NEI")
        }
    }
}
