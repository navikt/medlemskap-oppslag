package no.nav.medlemskap.regler.v2.common

abstract class Regler {

    abstract fun hentHovedRegel(): Regel

    protected fun minstEnAvDisse(vararg regler: Regel): Resultat {
        val delresultat = regler.map { it.utfør() }

        return utledResultat(delresultat).copy(
                delresultat = delresultat
        )
    }

    protected fun sjekkRegel(metode: () -> Regel) = metode.invoke()

    private fun utledResultat(resultater: List<Resultat>): Resultat {
        return when {
            resultater.any { it.svar == Svar.UAVKLART } -> uavklart("Minst en av de følgende ble UAVKLART")
            resultater.any { it.svar == Svar.JA } -> ja("Minst en av de følgende ble JA")
            else -> nei("Alle de følgende ble NEI")
        }
    }
}
