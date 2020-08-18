package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Ytelse

abstract class Regler(val ytelse: Ytelse) {
    private val resultatliste: MutableList<Resultat> = mutableListOf()

    abstract fun hentRegelflyt(): Regelflyt

    fun kjørRegelflyt(): Resultat {
        return hentRegelflyt().utfør(resultatliste)
    }

    fun kjørRegelflyt(resultatliste: MutableList<Resultat>): Resultat {
        return hentRegelflyt().utfør(resultatliste)
    }

    protected fun minstEnAvDisse(vararg regler: Regel): Resultat {
        val delresultat = regler.map { it.utfør() }

        return utledResultat(delresultat).copy(
                delresultat = delresultat
        )
    }

    private fun utledResultat(resultater: List<Resultat>): Resultat {
        return when {
            resultater.any { it.svar == Svar.UAVKLART } -> uavklart("Minst en av de følgende ble UAVKLART")
            resultater.any { it.svar == Svar.JA } -> ja("Minst en av de følgende ble JA")
            else -> nei("Alle de følgende ble NEI")
        }
    }

    protected fun lagRegelflyt(regel: Regel, hvisJa: Regelflyt? = null, hvisNei: Regelflyt? = null, hvisUavklart: Regelflyt = regelFlytUavklart(ytelse)): Regelflyt {
        return Regelflyt(regel = regel, ytelse = ytelse, hvisJa = hvisJa, hvisNei = hvisNei, hvisUavklart = hvisUavklart)
    }

}
