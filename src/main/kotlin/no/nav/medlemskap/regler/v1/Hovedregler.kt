package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Svar.NEI

class Hovedregler(datagrunnlag: Datagrunnlag) {
    private val reglerForEøsStatsborgerskap = ReglerForEøsStatsborgerskap.fraDatagrunnlag(datagrunnlag)
    private val reglerForNorskStatsborgerskap = ReglerForNorskStatsborgerskap.fraDatagrunnlag(datagrunnlag)
    private val reglerForRegistrerteOpplysninger = ReglerForRegistrerteOpplysninger.fraDatagrunnlag(datagrunnlag)
    private val reglerForNorskeStatsborgere = ReglerForNorskeStatsborgere.fraDatagrunnlag(datagrunnlag)
    private val reglerForEøsBorgere = ReglerForEøsBorgere.fraDatagrunnlag(datagrunnlag)
    private val reglerForAndreStatsborgere = ReglerForAndreStatsborgere.fraDatagrunnlag(datagrunnlag)
    private val reglerForArbeidsforhold = ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag)

    fun kjørHovedregler(): Resultat {
        val ytelse = reglerForRegistrerteOpplysninger.ytelse

        val resultatRegistrertOpplysninger = reglerForRegistrerteOpplysninger.kjørRegelflyt()
        val resultatNorskStatsborgerskap = reglerForNorskStatsborgerskap.kjørRegelflyt()
        val resultatEøsStatsborgerskap = reglerForEøsStatsborgerskap.kjørRegelflyt()
        val resultatForStatborgerskap = bestemReglerForStatsborgerskap(resultatEøsStatsborgerskap, resultatNorskStatsborgerskap)
                .kjørRegelflyt()
        val resultatForArbeidsforhold = reglerForArbeidsforhold.kjørRegelflyt()

        val resultater = listOf(
                resultatRegistrertOpplysninger,
                resultatEøsStatsborgerskap,
                resultatNorskStatsborgerskap,
                resultatForArbeidsforhold,
                resultatForStatborgerskap
        )

        return utledResultat(ytelse, resultater)
    }

    private fun bestemReglerForStatsborgerskap(resultatEøsStatsborgerskap: Resultat, resultatNorskStatsborgerskap: Resultat): Regler {
        val erEøsBorger= resultatEøsStatsborgerskap.svar == Svar.JA
        val erNorskstatsborger = resultatNorskStatsborgerskap.svar == Svar.JA

        if (!erEøsBorger) {
            return reglerForAndreStatsborgere
        }

        return if (erNorskstatsborger) {
            reglerForNorskeStatsborgere
        } else {
            reglerForEøsBorgere
        }
    }

    private fun utledResultat(ytelse: Ytelse, resultater: List<Resultat>): Resultat {
        val medlemskonklusjon = resultater.find { it.erMedlemskonklusjon() }
        if (medlemskonklusjon != null) {
            return medlemskonklusjon.copy(delresultat = resultater.flatMap { it.delresultat }.utenKonklusjon())
        }

        val førsteNei = resultater.find { it.svar == NEI }
        if (førsteNei != null) {
            return neiResultat(ytelse).copy(delresultat = resultater.flatMap { it.delresultat }.utenKonklusjon())
        }

        return uavklartResultat(ytelse).copy(delresultat = resultater.flatMap { it.delresultat }.utenKonklusjon())
    }

    private fun uavklartResultat(ytelse: Ytelse): Resultat {
        return uavklartKonklusjon(ytelse).utfør()
    }

    private fun neiResultat(ytelse: Ytelse): Resultat {
        return neiKonklusjon(ytelse).utfør()
    }
}
