package no.nav.medlemskap.regler.common.uttrykk

import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultattype

class EllerUttrykk(val resultat: Resultat) {

    private val resultater: MutableList<Resultat> = mutableListOf(resultat)

    companion object {
        fun enten(uttrykk: () -> Resultat): EllerUttrykk = EllerUttrykk(uttrykk.invoke())
    }

    infix fun eller(uttrykk: () -> Resultat): EllerUttrykk {
        resultater.add(uttrykk.invoke())
        return this
    }

    infix fun resultatMedId(id: () -> String): Resultat {
        val avklaring = resultater.joinToString(" ELLER ") { it.avklaring }
        val beskrivelse = resultater.joinToString(" OG ") { it.beskrivelse }

        return Resultat(
                identifikator = id.invoke(),
                avklaring = avklaring,
                beskrivelse = beskrivelse,
                resultat = utledResultattype(resultater.map { it.resultat }),
                delresultat = resultater
        )
    }

    private fun utledResultattype(typer: List<Resultattype>): Resultattype = when {
        typer.contains(Resultattype.UAVKLART) -> {
            Resultattype.UAVKLART
        }
        typer.contains(Resultattype.JA) -> {
            Resultattype.JA
        }
        else -> {
            Resultattype.NEI
        }
    }
}
