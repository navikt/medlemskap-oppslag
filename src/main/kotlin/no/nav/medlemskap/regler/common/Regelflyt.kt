package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.Konklusjonstype.REGELFLYT
import no.nav.medlemskap.regler.common.Regel.Companion.jaKonklusjon
import no.nav.medlemskap.regler.common.Regel.Companion.neiKonklusjon
import no.nav.medlemskap.regler.common.Regel.Companion.regelJa
import no.nav.medlemskap.regler.common.Regel.Companion.regelNei
import no.nav.medlemskap.regler.common.Regel.Companion.regelUavklart
import no.nav.medlemskap.regler.common.Regel.Companion.uavklartKonklusjon
import no.nav.medlemskap.regler.common.Resultat.Companion.utenKonklusjon

class Regelflyt(
    val regel: Regel,
    val ytelse: Ytelse,
    val hvisJa: Regelflyt? = null,
    val hvisNei: Regelflyt? = null,
    val hvisUavklart: Regelflyt? = null,
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

            val begrunnelse = when (overstyrtSvar) {
                Svar.JA -> regel.regelId.jaBegrunnelse
                Svar.NEI -> regel.regelId.neiBegrunnelse
                else -> regel.regelId.uavklartBegrunnelse
            }
            regelResultat.copy(svar = overstyrtSvar, begrunnelse = begrunnelse)
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

        return konklusjon
    }

    companion object {

        fun konklusjonJa(ytelse: Ytelse): Regelflyt {
            return Regelflyt(jaKonklusjon(ytelse), ytelse)
        }

        fun konklusjonNei(ytelse: Ytelse): Regelflyt {
            return Regelflyt(neiKonklusjon(ytelse), ytelse)
        }

        fun konklusjonUavklart(ytelse: Ytelse, regelId: RegelId = RegelId.REGEL_MEDLEM_KONKLUSJON): Regelflyt {
            return Regelflyt(uavklartKonklusjon(ytelse, regelId), ytelse)
        }

        fun regelflytJa(ytelse: Ytelse, regelId: RegelId = RegelId.REGEL_FLYT_KONKLUSJON, konklusjonstype: Konklusjonstype = REGELFLYT): Regelflyt {
            return Regelflyt(regelJa(ytelse, regelId, konklusjonstype), ytelse)
        }

        fun regelflytNei(ytelse: Ytelse, regelId: RegelId = RegelId.REGEL_FLYT_KONKLUSJON, konklusjonstype: Konklusjonstype = REGELFLYT): Regelflyt {
            return Regelflyt(regelNei(ytelse, regelId, konklusjonstype), ytelse)
        }

        fun regelflytUavklart(ytelse: Ytelse, regelId: RegelId = RegelId.REGEL_FLYT_KONKLUSJON, konklusjonstype: Konklusjonstype = REGELFLYT): Regelflyt {
            return Regelflyt(regelUavklart(ytelse, regelId, konklusjonstype), ytelse)
        }
    }
}
