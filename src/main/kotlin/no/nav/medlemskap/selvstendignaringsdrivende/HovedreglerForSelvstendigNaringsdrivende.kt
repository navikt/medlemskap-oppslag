package no.nav.medlemskap.selvstendignaringsdrivende

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.Regel.Companion.jaKonklusjon
import no.nav.medlemskap.regler.common.Regel.Companion.neiKonklusjon
import no.nav.medlemskap.regler.common.Regel.Companion.uavklartKonklusjon
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Regler

import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.finnÅrsaker

import no.nav.medlemskap.regler.common.Svar.*
import no.nav.medlemskap.regler.v1.ReglerForDoedsfall
import no.nav.medlemskap.regler.v1.ReglerForMedl
import no.nav.medlemskap.regler.v1.ReglerForRequestValidering
import no.nav.medlemskap.regler.v1.ReglerForStatsborgerskap
import no.nav.medlemskap.regler.v1.ReglerForTredjelandsborgerFamilie


class HovedreglerForSelvstendigNaringsdrivende(private val datagrunnlag: Datagrunnlag,val brukerGruppeResultat: Resultat) {
    private val reglerForRequestValidering = ReglerForRequestValidering.fraDatagrunnlag(datagrunnlag)
    private val reglerForStatsborgerskap = ReglerForStatsborgerskap.fraDatagrunnlag(datagrunnlag)
    private val reglerForTredjelandsborgerFamlie = ReglerForTredjelandsborgerFamilie.fraDatagrunnlag(datagrunnlag)

    fun kjørHovedregler(): Resultat {

        /*
        kjør request validering
         */
        val requestValideringResultat = reglerForRequestValidering.kjørRegel()
        if (requestValideringResultat.svar != JA) {
            return requestValideringResultat
        }

        val ytelse = reglerForRequestValidering.ytelse

        val resultater = kotlin.collections.mutableListOf<Resultat>()

        //legg til brukeGruppe resultat
        resultater.add(brukerGruppeResultat)
        /*
        * kjør MEDL regler. Dersom
        * */

        val resultatStatsborgerskap = reglerForStatsborgerskap.kjørHovedflyt()
        resultater.add(resultatStatsborgerskap)
        val resultatMedl = ReglerForMedl.fraDatagrunnlag(datagrunnlag).kjørHovedflyt()
        resultater.add(resultatMedl)

        /*
        * kjør felles regler, men kun dersom MEDL er uavklart eller svarer JA. Svarer de NEI, skippes andre regler
        * */
        if (resultatMedl.erRegelflytKonklusjon() && resultatMedl.svar == JA || resultatMedl.svar == UAVKLART) {
            resultater.addAll(kjørFellesRegler())
        }

        /*
        *
        * */
        return utledResultat(ytelse, resultater)
    }


    private fun kjørFellesRegler(): List<Resultat> {
        val fellesRegler = listOf(
            ReglerForDoedsfall.fraDatagrunnlag(datagrunnlag)
        )

        return fellesRegler.map(Regler::kjørHovedflyt)
    }





    companion object {
        private fun utledResultat(ytelse: Ytelse, resultater: List<Resultat>): Resultat {

            val førsteNei = resultater.find { it.svar == NEI && it.regelId!= RegelId.REGEL_17 }
            if (førsteNei != null) {
                return lagKonklusjon(neiResultat(ytelse), resultater)
            }

            val medlemskonklusjon = resultater.find { it.erMedlemskonklusjon() }
            if (medlemskonklusjon != null) {
                return lagKonklusjon(konklusjon(ytelse, medlemskonklusjon), resultater)
            }

            if (resultater.all { it.svar == JA }) {
                return lagKonklusjon(jaResultat(ytelse), resultater)
            }

            return lagKonklusjon(uavklartResultat(ytelse), resultater)
        }

        private fun lagKonklusjon(konklusjon: Resultat, resultater: List<Resultat>): Resultat {
            return konklusjon.copy(delresultat = lagDelresultat(resultater), årsaker = resultater.finnÅrsaker())
        }

        private fun lagDelresultat(resultater: List<Resultat>): List<Resultat> {
            return resultater.map { if (it.regelId == RegelId.REGEL_FLYT_KONKLUSJON || it.regelId == RegelId.REGEL_MEDLEM_KONKLUSJON && it.delresultat.isNotEmpty()) it.delresultat.first() else it }
        }

        private fun konklusjon(ytelse: Ytelse, resultat: Resultat): Resultat {
            return when (resultat.svar) {
                JA -> jaKonklusjon(ytelse).utfør().copy(harDekning = resultat.harDekning, dekning = resultat.dekning)
                NEI -> neiKonklusjon(ytelse).utfør().copy(harDekning = resultat.harDekning, dekning = resultat.dekning)
                UAVKLART -> uavklartKonklusjon(ytelse).utfør().copy(harDekning = resultat.harDekning, dekning = resultat.dekning)
            }
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
