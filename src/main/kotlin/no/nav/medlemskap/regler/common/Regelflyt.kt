package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Ytelse

class Regelflyt(
    val regel: Regel,
    val ytelse: Ytelse,
    val hvisJa: Regelflyt? = null,
    val hvisNei: Regelflyt? = null,
    val hvisUavklart: Regelflyt? = null,
    val regelIdForSammensattResultat: RegelId? = null,
    val overstyrteRegler: Map<RegelId, Svar> = mapOf()
) {

    private fun utfør(
        resultatliste: MutableList<Resultat>,
        harDekning: Svar? = null,
        dekning: String = ""
    ): Resultat {
        val regelResultat = regel.utfør()

        val overstyrtSvar = overstyrteRegler[regel.regelId]
        val resultat = if (overstyrtSvar != null) {
            regelResultat.copy(svar = overstyrtSvar, begrunnelse = "Overstyrt svar")
        } else {
            regelResultat
        }

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

    fun utfør(harDekning: Svar? = null, dekning: String = ""): Resultat {
        val resultatliste = mutableListOf<Resultat>()

        val resultat = utfør(resultatliste, harDekning, dekning)
        val delresultater = if (resultat.delresultat.isNotEmpty()) resultat.delresultat else resultatliste.utenKonklusjon()
        val konklusjon = resultat.copy(delresultat = delresultater)
        if (regelIdForSammensattResultat != null) {
            return konklusjon.copy(
                delresultat = listOf(
                    Resultat(
                        regelId = regelIdForSammensattResultat,
                        svar = konklusjon.svar,
                        avklaring = regelIdForSammensattResultat.avklaring,
                        delresultat = delresultater
                    )
                )
            )
        }

        return konklusjon
    }
}
