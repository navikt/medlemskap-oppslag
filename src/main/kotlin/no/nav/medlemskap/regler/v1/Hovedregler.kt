package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.Regel.Companion.jaKonklusjon
import no.nav.medlemskap.regler.common.Regel.Companion.neiKonklusjon
import no.nav.medlemskap.regler.common.Regel.Companion.uavklartKonklusjon
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Regler
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.common.Svar.JA
import no.nav.medlemskap.regler.common.Svar.NEI

class Hovedregler(private val datagrunnlag: Datagrunnlag) {
    private val reglerForRequestValidering = ReglerForRequestValidering.fraDatagrunnlag(datagrunnlag)
    private val reglerForOverstyring = ReglerForOverstyring.fraDatagrunnlag(datagrunnlag)
    private val reglerForStatsborgerskap = ReglerForStatsborgerskap.fraDatagrunnlag(datagrunnlag)

    fun kjørHovedregler(): Resultat {

        val requestValideringResultat = reglerForRequestValidering.kjørRegel()
        if (requestValideringResultat.svar != JA) {
            return requestValideringResultat
        }

        val ytelse = reglerForRequestValidering.ytelse
        val resultater = mutableListOf<Resultat>()
        val resultaterForOverstyring = reglerForOverstyring.kjørRegelflyter()
        val reglerSomSkalOverstyres = reglerForOverstyring.reglerSomSkalOverstyres(resultaterForOverstyring)

        resultater.addAll(resultaterForOverstyring)
        resultater.addAll(kjørFellesRegler(reglerSomSkalOverstyres))

        val resultatStatsborgerskap = reglerForStatsborgerskap.kjørRegelflyter()
        resultater.addAll(resultatStatsborgerskap)
        resultater.addAll(
            bestemReglerForStatsborgerskap(resultatStatsborgerskap, reglerSomSkalOverstyres)
                .kjørRegelflyter()
        )

        return utledResultat(ytelse, resultater)
    }

    private fun bestemReglerForStatsborgerskap(resultatStatsborgerskap: List<Resultat>): Regler {
    private fun kjørValideringsregler(): Resultat {
        return utledResultat(reglerForRequestValidering.ytelse, reglerForRequestValidering.kjørRegelflyter())
    }

    private fun kjørFellesRegler(overstyrteRegler: Map<RegelId, Svar>): List<Resultat> {
        val fellesRegler = listOf(
            ReglerForMedl.fraDatagrunnlag(datagrunnlag),
            ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag, overstyrteRegler),
            ReglerForBosatt.fraDatagrunnlag(datagrunnlag),
            ReglerForDoedsfall.fraDatagrunnlag(datagrunnlag)
        )

        return fellesRegler.flatMap { it.kjørRegelflyter() }
    }

    private fun bestemReglerForStatsborgerskap(resultatStatsborgerskap: List<Resultat>, overstyrteRegler: Map<RegelId, Svar>): Regler {
        val resultatEøsStatsborgerskap = resultatStatsborgerskap
            .flatMap { it.delresultat }
            .first { it.regelId == RegelId.REGEL_2 }
        val erEøsBorger = resultatEøsStatsborgerskap.svar == JA
        if (!erEøsBorger) {
            return ReglerForAndreStatsborgere.fraDatagrunnlag(datagrunnlag)
        }

        val resultatNorskStatsborgerskap = resultatStatsborgerskap
            .flatMap { it.delresultat }
            .first { it.regelId == RegelId.REGEL_11 }
        val erNorskBorger = resultatNorskStatsborgerskap.svar == JA

        return if (erNorskBorger) {
            ReglerForNorskeStatsborgere.fraDatagrunnlag(datagrunnlag, overstyrteRegler)
        } else {
            ReglerForEøsBorgere.fraDatagrunnlag(datagrunnlag)
        }
    }

    companion object {
        private fun utledResultat(ytelse: Ytelse, resultater: List<Resultat>): Resultat {

            val medlemskonklusjon = resultater.find { it.erMedlemskonklusjon() }
            if (medlemskonklusjon != null) {
                return lagKonklusjon(medlemskonklusjon, resultater)
            }

            if (resultater.all { it.svar == JA }) {
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
