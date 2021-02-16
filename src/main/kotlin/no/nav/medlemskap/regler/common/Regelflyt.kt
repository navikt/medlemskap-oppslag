package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.Konklusjonstype.MEDLEM
import no.nav.medlemskap.regler.common.Konklusjonstype.REGELFLYT
import no.nav.medlemskap.regler.common.Regel.Companion.regelJa
import no.nav.medlemskap.regler.common.Regel.Companion.regelNei
import no.nav.medlemskap.regler.common.Regel.Companion.regelUavklart
import no.nav.medlemskap.regler.common.Regel.Companion.uavklartKonklusjon
import no.nav.medlemskap.regler.common.Resultat.Companion.finnÅrsaker
import no.nav.medlemskap.regler.common.Resultat.Companion.utenKonklusjon

class Regelflyt(
    val regel: Regel,
    val hvisJa: Regelflyt? = null,
    val hvisNei: Regelflyt? = null,
    val hvisUavklart: Regelflyt? = null,
    val overstyrteRegler: Map<RegelId, Svar> = mapOf(),
    val årsak: Årsak? = null
) {
    val ytelse = regel.ytelse

    private fun utfør(
        resultatliste: MutableList<Resultat>,
        harDekning: Svar? = null,
        dekning: String = ""
    ): Resultat {
        val regelResultat = regel.utfør()

        val overstyrtSvar = overstyrteRegler[regel.regelId]
        val resultat = if (overstyrtSvar != null) {
            regelResultat.copy(svar = overstyrtSvar, begrunnelse = regel.regelId.begrunnelse(overstyrtSvar))
        } else {
            if (regelResultat.hentÅrsak() == null && regelResultat.erKonklusjon() && regelResultat.svar != Svar.JA) {
                regelResultat.copy(
                    årsak = if (årsak == null) {
                        Årsak.fraResultat(resultatliste.last())
                    } else {
                        årsak
                    }
                )
            } else {
                if (årsak != null) {
                    regelResultat.copy(årsak = årsak)
                } else {
                    regelResultat
                }
            }
        }

        resultatliste.add(resultat)

        val nesteRegel =
            if (resultat.svar == Svar.JA && hvisJa != null) {
                hvisJa
            } else {
                if (resultat.svar == Svar.NEI && hvisNei != null) {
                    hvisNei
                } else
                    hvisUavklart
            }

        val nesteResultat = nesteRegel?.utfør(resultatliste, resultat.harDekning, resultat.dekning)

        if (nesteResultat != null) {
            if (resultat.hentÅrsak() != null) {
                return nesteResultat.copy(årsak = årsak, årsaker = resultatliste.finnÅrsaker())
            }

            val årsaken = if (nesteResultat.erKonklusjon() &&
                nesteResultat.svar != Svar.JA &&
                nesteRegel.regel.regelId.erRegelflytKonklusjon &&
                nesteResultat.hentÅrsak() == null
            ) {
                Årsak.fraResultat(regelResultat)
            } else {
                nesteResultat.hentÅrsak()
            }

            return nesteResultat.copy(årsak = årsaken, årsaker = resultatliste.finnÅrsaker())
        }

        if (resultat.hentÅrsak() == null && resultat.erKonklusjon()) {
            return resultat.copy(
                harDekning = harDekning,
                dekning = dekning,
                årsak = resultatliste.mapNotNull { it.hentÅrsak() }.firstOrNull(),
                årsaker = resultatliste.finnÅrsaker()
            )
        }

        return resultat.copy(harDekning = harDekning, dekning = dekning, årsaker = resultatliste.finnÅrsaker())
    }

    fun utfør(harDekning: Svar? = null, dekning: String = ""): Resultat {
        val resultatliste = mutableListOf<Resultat>()

        val resultat = utfør(resultatliste, harDekning, dekning)
        val delresultater =
            if (resultat.delresultat.isNotEmpty()) resultat.delresultat else resultatliste.utenKonklusjon()

        val konklusjon = resultat.copy(delresultat = delresultater)

        return konklusjon
    }

    companion object {
        fun medlemskonklusjonUavklart(ytelse: Ytelse): Regelflyt {
            return Regelflyt(uavklartKonklusjon(ytelse, RegelId.REGEL_MEDLEM_KONKLUSJON))
        }

        fun regelflytJa(ytelse: Ytelse, regelId: RegelId = RegelId.REGEL_FLYT_KONKLUSJON): Regelflyt {
            return Regelflyt(regelJa(ytelse, regelId, REGELFLYT))
        }

        fun regelflytNei(ytelse: Ytelse, regelId: RegelId = RegelId.REGEL_FLYT_KONKLUSJON): Regelflyt {
            return Regelflyt(regelNei(ytelse, regelId, REGELFLYT))
        }

        fun regelflytUavklart(ytelse: Ytelse, regelId: RegelId = RegelId.REGEL_FLYT_KONKLUSJON): Regelflyt {
            return Regelflyt(regelUavklart(ytelse, regelId, REGELFLYT))
        }

        fun konklusjonJa(ytelse: Ytelse, regelId: RegelId = RegelId.REGEL_MEDLEM_KONKLUSJON): Regelflyt {
            return Regelflyt(regelJa(ytelse, regelId, MEDLEM))
        }

        fun konklusjonNei(ytelse: Ytelse, regelId: RegelId = RegelId.REGEL_MEDLEM_KONKLUSJON): Regelflyt {
            return Regelflyt(regelNei(ytelse, regelId, MEDLEM))
        }

        fun konklusjonUavklart(ytelse: Ytelse, regelId: RegelId = RegelId.REGEL_MEDLEM_KONKLUSJON): Regelflyt {
            return Regelflyt(regelUavklart(ytelse, regelId, MEDLEM))
        }
    }
}
