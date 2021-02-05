package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.ReglerForArbeidstaker
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
        val resultatForOverstyring = reglerForOverstyring.kjørHovedflyt()
        val reglerSomSkalOverstyres = reglerForOverstyring.reglerSomSkalOverstyres(resultatForOverstyring)

        resultater.add(resultatForOverstyring)
        resultater.addAll(kjørFellesRegler())

        val resultatStatsborgerskap = reglerForStatsborgerskap.kjørHovedflyt()
        resultater.add(resultatStatsborgerskap)

        val reglerForStatsborgerskap = bestemReglerForStatsborgerskap(resultatStatsborgerskap, reglerSomSkalOverstyres)
        reglerForStatsborgerskap.forEach {
            resultater.add(it.kjørHovedflyt())
        }

        return utledResultat(ytelse, resultater)
    }

    private fun kjørFellesRegler(): List<Resultat> {
        val fellesRegler = listOf(
            ReglerForMedl.fraDatagrunnlag(datagrunnlag),
            ReglerForBosatt.fraDatagrunnlag(datagrunnlag),
            ReglerForDoedsfall.fraDatagrunnlag(datagrunnlag),
            ReglerForFellesArbeidsforhold.fraDatagrunnlag(datagrunnlag)
        )

        return fellesRegler.map(Regler::kjørHovedflyt)
    }

    private fun bestemReglerForStatsborgerskap(resultatStatsborgerskap: Resultat, overstyrteRegler: Map<RegelId, Svar>): List<Regler> {
        val resultatEøsStatsborgerskap = resultatStatsborgerskap
            .delresultat
            .first { it.regelId == RegelId.REGEL_2 }
        val erEøsBorger = resultatEøsStatsborgerskap.svar == JA
        if (!erEøsBorger) {
            return listOf(
                ReglerForArbeidstaker.fraDatagrunnlag(datagrunnlag),
                ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag, overstyrteRegler),
                ReglerForAndreStatsborgere.fraDatagrunnlag(datagrunnlag)
            )
        }

        val resultatNorskStatsborgerskap = resultatStatsborgerskap
            .delresultat
            .first { it.regelId == RegelId.REGEL_11 }
        val erNorskBorger = resultatNorskStatsborgerskap.svar == JA

        return if (erNorskBorger) {
            listOf(
                ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag, overstyrteRegler),
                ReglerForNorskeStatsborgere.fraDatagrunnlag(datagrunnlag, overstyrteRegler)
            )
        } else {
            listOf(
                ReglerForArbeidstaker.fraDatagrunnlag(datagrunnlag),
                ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag, overstyrteRegler),
                ReglerForEøsBorgere.fraDatagrunnlag(datagrunnlag)
            )
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
