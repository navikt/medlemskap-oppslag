package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Ytelse

class Regelflyt(
        val regel: Regel,
        val ytelse: Ytelse,
        val hvisJa: Regelflyt? = null,
        val hvisNei: Regelflyt? = null,
        val hvisUavklart: Regelflyt? = null
) {
    fun utfør(resultatliste: MutableList<Resultat>, harDekning: Svar? = null, dekning: String = ""): Resultat {
        val resultat = regel.utfør()

        resultatliste.add(resultat)
        if (resultat.svar == Svar.JA && hvisJa != null) {
            return hvisJa.utfør(resultatliste, resultat.harDekning, resultat.dekning)
        }

        if (resultat.svar == Svar.NEI && hvisNei != null) {
            return hvisNei.utfør(resultatliste, resultat.harDekning, resultat.dekning)
        }

        if (resultat.svar == Svar.UAVKLART && hvisUavklart != null) {
            return hvisUavklart.utfør(resultatliste, resultat.harDekning, resultat.dekning)
        }

        resultat.harDekning = harDekning
        resultat.dekning = dekning

        return resultat
    }
}