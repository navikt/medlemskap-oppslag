package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Svar.NEI
import no.nav.medlemskap.regler.common.neiKonklusjon
import no.nav.medlemskap.regler.common.uavklartKonklusjon
import no.nav.medlemskap.regler.common.utenKonklusjon

class Hovedregler(datagrunnlag: Datagrunnlag) {
    private val reglerForRegistrerteOpplysninger = ReglerForRegistrerteOpplysninger.fraDatagrunnlag(datagrunnlag)
    private val reglerForMedl = ReglerForMedl.fraDatagrunnlag(datagrunnlag)
    private val reglerForArbeidsforhold = ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag)
    private val reglerForLovvalg = ReglerForLovvalg.fraDatagrunnlag(datagrunnlag)

    fun kjørHovedregler(): Resultat {
        val ytelse = reglerForRegistrerteOpplysninger.ytelse

        val resultater = listOf(
                reglerForRegistrerteOpplysninger,
                reglerForArbeidsforhold,
                reglerForLovvalg
        ).map { it.kjørRegelflyt() }

        val resultat = utledResultat(ytelse, resultater)

        return resultat
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
