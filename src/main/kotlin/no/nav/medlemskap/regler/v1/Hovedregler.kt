package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Statsborgerskapskategori
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.ReglerForHovedsakligArbeidstaker
import no.nav.medlemskap.regler.common.Regel.Companion.jaKonklusjon
import no.nav.medlemskap.regler.common.Regel.Companion.neiKonklusjon
import no.nav.medlemskap.regler.common.Regel.Companion.uavklartKonklusjon
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Regler
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.finnÅrsaker
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.common.Svar.*

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

        val resultaterForStatsborger = kjørReglerForStatsborgerskap(resultatStatsborgerskap, reglerSomSkalOverstyres)
        resultater.addAll(resultaterForStatsborger)

        return utledResultat(ytelse, resultater)
    }

    private fun kjørReglerForStatsborgerskap(resultatStatsborgerskap: Resultat, reglerSomSkalOverstyres: Map<RegelId, Svar>): List<Resultat> {
        val statsborgerskapskategori = bestemStatsborgerskapskategori(resultatStatsborgerskap)

        return when (statsborgerskapskategori) {
            Statsborgerskapskategori.TREDJELANDSBORGER -> kjørReglerForTredjelandsborgere()
            Statsborgerskapskategori.EØS_BORGER -> kjørReglerForEøsBorgere(reglerSomSkalOverstyres)
            Statsborgerskapskategori.NORSK_BORGER -> kjørReglerForNorskeBorgere(reglerSomSkalOverstyres)
        }
    }

    private fun kjørReglerForTredjelandsborgere(): List<Resultat> {
        val resultater = mutableListOf<Resultat>()

        resultater.add(ReglerForHovedsakligArbeidstaker.fraDatagrunnlag(datagrunnlag).kjørHovedflyt())
        resultater.add(ReglerForOppholdstillatelse.fraDatagrunnlag(datagrunnlag).kjørHovedflyt())

        val resultatMedl = ReglerForMedl.fraDatagrunnlag(datagrunnlag).kjørHovedflyt()
        resultater.add(resultatMedl)

        if (resultatMedl.erRegelflytKonklusjon() && resultatMedl.svar == JA || resultatMedl.svar == UAVKLART) {
            resultater.add(ReglerForAndreStatsborgere.fraDatagrunnlag(datagrunnlag).kjørHovedflyt())
        }

        return resultater
    }

    private fun kjørReglerForNorskeBorgere(overstyrteRegler: Map<RegelId, Svar>): List<Resultat> {
        return listOf(
            ReglerForMedl.fraDatagrunnlag(datagrunnlag),
            ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag, overstyrteRegler),
            ReglerForBosatt.fraDatagrunnlag(datagrunnlag),
            ReglerForNorskeStatsborgere.fraDatagrunnlag(datagrunnlag, overstyrteRegler)
        ).map { it.kjørHovedflyt() }
    }

    private fun kjørReglerForEøsBorgere(overstyrteRegler: Map<RegelId, Svar>): List<Resultat> {
        return listOf(
            ReglerForMedl.fraDatagrunnlag(datagrunnlag),
            ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag, overstyrteRegler),
            ReglerForBosatt.fraDatagrunnlag(datagrunnlag),
            ReglerForEøsBorgere.fraDatagrunnlag(datagrunnlag)
        ).map { it.kjørHovedflyt() }
    }

    private fun kjørFellesRegler(): List<Resultat> {
        val fellesRegler = listOf(
            ReglerForDoedsfall.fraDatagrunnlag(datagrunnlag),
            ReglerForFellesArbeidsforhold.fraDatagrunnlag(datagrunnlag)
        )

        return fellesRegler.map(Regler::kjørHovedflyt)
    }

    private fun bestemStatsborgerskapskategori(resultatStatsborgerskap: Resultat): Statsborgerskapskategori {
        if (!resultatStatsborgerskap.erEøsBorger()) {
            return Statsborgerskapskategori.TREDJELANDSBORGER
        }

        return if (resultatStatsborgerskap.erNorskBorger()) {
            Statsborgerskapskategori.NORSK_BORGER
        } else {
            Statsborgerskapskategori.EØS_BORGER
        }
    }

    companion object {
        private fun utledResultat(ytelse: Ytelse, resultater: List<Resultat>): Resultat {

            val medlemskonklusjon = resultater.find { it.erMedlemskonklusjon() }
            if (medlemskonklusjon != null) {
                return lagKonklusjon(konklusjon(ytelse, medlemskonklusjon), resultater)
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
