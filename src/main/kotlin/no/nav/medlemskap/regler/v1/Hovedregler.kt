package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Svar.NEI

class Hovedregler(datagrunnlag: Datagrunnlag) {
    private val reglerForStatsborgerskap = ReglerForStatsborgerskap.fraDatagrunnlag(datagrunnlag)
    private val reglerForMedl = ReglerForMedl.fraDatagrunnlag(datagrunnlag)
    private val reglerForNorskeStatsborgere = ReglerForNorskeStatsborgere.fraDatagrunnlag(datagrunnlag)
    private val reglerForEøsBorgere = ReglerForEøsBorgere.fraDatagrunnlag(datagrunnlag)
    private val reglerForAndreStatsborgere = ReglerForAndreStatsborgere.fraDatagrunnlag(datagrunnlag)
    private val reglerForArbeidsforhold = ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag)

    fun kjørHovedregler(): Resultat {
        val ytelse = reglerForMedl.ytelse
        val resultater = mutableListOf<Resultat>()

        resultater.addAll(reglerForMedl.kjørRegelflyter())
        resultater.addAll(reglerForArbeidsforhold.kjørRegelflyter())

        val resultatStatsborgerskap = reglerForStatsborgerskap.kjørRegelflyter()
        resultater.addAll(resultatStatsborgerskap)
        resultater.addAll(
            bestemReglerForStatsborgerskap(resultatStatsborgerskap)
                .kjørRegelflyter()
        )

        return utledResultat(ytelse, resultater)
    }

    private fun bestemReglerForStatsborgerskap(resultatStatsborgerskap: List<Resultat>): Regler {
        val resultatEøsStatsborgerskap = resultatStatsborgerskap
            .flatMap { it.delresultat }
            .first { it.regelId == RegelId.REGEL_2 }
        val erEøsBorger = resultatEøsStatsborgerskap.svar == Svar.JA
        if (!erEøsBorger) {
            return reglerForAndreStatsborgere
        }

        val resultatNorskStatsborgerskap = resultatStatsborgerskap
            .flatMap { it.delresultat }
            .first { it.regelId == RegelId.REGEL_11 }
        val erNorskstatsborger = resultatNorskStatsborgerskap.svar == Svar.JA

        return if (erNorskstatsborger) {
            reglerForNorskeStatsborgere
        } else {
            reglerForEøsBorgere
        }
    }

    companion object {
        private fun utledResultat(ytelse: Ytelse, resultater: List<Resultat>): Resultat {

            val medlemskonklusjon = resultater.find { it.erMedlemskonklusjon() }
            if (medlemskonklusjon != null) {
                return lagKonklusjon(medlemskonklusjon, resultater)
            }

            if (resultater.all { it.svar == Svar.JA }) {
                return lagKonklusjon(jaResultat(ytelse), resultater)
            }

            val førsteNei = resultater.find { it.svar == NEI }
            if (førsteNei != null) {
                return lagKonklusjon(neiResultat(ytelse), resultater)
            }

            return lagKonklusjon(uavklartResultat(ytelse), resultater)
        }

        private fun lagKonklusjon(konklusjon: Resultat, resultater: List<Resultat>): Resultat {
            return konklusjon.copy(delresultat = lagDelresultat(resultater))
        }

        private fun lagDelresultat(resultater: List<Resultat>): List<Resultat> {
            return resultater.map { if (it.regelId == RegelId.REGEL_FLYT_KONKLUSJON || it.erMedlemskonklusjon() && it.delresultat.isNotEmpty()) it.delresultat.first() else it }
        }

        private fun uavklartResultat(ytelse: Ytelse): Resultat {
            return uavklartKonklusjon(ytelse).utfør()
        }

        private fun neiResultat(ytelse: Ytelse): Resultat {
            return neiKonklusjon(ytelse).utfør()
        }

        private fun jaResultat(ytelse: Ytelse): Resultat {
            return jaKonklusjon(ytelse).utfør()
        }
    }
}
