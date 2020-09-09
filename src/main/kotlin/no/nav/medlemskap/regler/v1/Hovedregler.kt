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
                return medlemskonklusjon.copy(delresultat = lagDelResultater(resultater))
            }

            if (resultater.all { it.svar == Svar.JA }) {
                return jaResultat(ytelse).copy(delresultat = lagDelResultater(resultater))
            }

            val førsteNei = resultater.find { it.svar == NEI }
            if (førsteNei != null) {
                return neiResultat(ytelse).copy(delresultat = lagDelResultater(resultater))
            }

            return uavklartResultat(ytelse).copy(delresultat = lagDelResultater(resultater))
        }

        private fun lagDelResultater(resultater: List<Resultat>): List<Resultat> {
            return resultater.map { if (it.erRegelflytKonklusjon()) it.delresultat.filterNot { delresultat -> delresultat.erRegelflytKonklusjon() }.first() else it }
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
