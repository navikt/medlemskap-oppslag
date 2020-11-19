package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.Regel.Companion.regelJaKonklusjon
import no.nav.medlemskap.regler.common.Regel.Companion.regelNeiKonklusjon
import no.nav.medlemskap.regler.common.Regel.Companion.regelUavklartKonklusjon
import no.nav.medlemskap.regler.common.Resultat.Companion.jaKonklusjon
import no.nav.medlemskap.regler.common.Resultat.Companion.neiKonklusjon
import no.nav.medlemskap.regler.common.Resultat.Companion.uavklartKonklusjon
import no.nav.medlemskap.regler.common.Resultat.Companion.utenKonklusjon

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

    companion object {

        fun konklusjonJa(ytelse: Ytelse): Regelflyt {
            return Regelflyt(jaKonklusjon(ytelse), ytelse)
        }

        fun konklusjonNei(ytelse: Ytelse): Regelflyt {
            return Regelflyt(neiKonklusjon(ytelse), ytelse)
        }

        fun konklusjonUavklart(ytelse: Ytelse, regelId: RegelId? = null): Regelflyt {
            return Regelflyt(uavklartKonklusjon(ytelse, regelId), ytelse)
        }

        fun regelflytJa(ytelse: Ytelse, regelId: RegelId = RegelId.REGEL_FLYT_KONKLUSJON): Regelflyt {
            return Regelflyt(regelJaKonklusjon(ytelse, regelId), ytelse)
        }

        fun regelflytNei(ytelse: Ytelse, regelId: RegelId = RegelId.REGEL_FLYT_KONKLUSJON): Regelflyt {
            return Regelflyt(regelNeiKonklusjon(ytelse, regelId), ytelse)
        }

        fun regelflytUavklart(ytelse: Ytelse, regelId: RegelId = RegelId.REGEL_FLYT_KONKLUSJON): Regelflyt {
            return Regelflyt(regelUavklartKonklusjon(ytelse, regelId), ytelse)
        }
    }
}
