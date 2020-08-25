package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.*

class Hovedregler(datagrunnlag: Datagrunnlag) {
//    private val resultatliste: MutableList<Resultat> = mutableListOf()
    private val reglerForRegistrerteOpplysninger = ReglerForRegistrerteOpplysninger.fraDatagrunnlag(datagrunnlag)
    private val reglerForMedl = ReglerForMedl.fraDatagrunnlag(datagrunnlag)
    private val reglerForArbeidsforhold = ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag)
    private val reglerForLovvalg = ReglerForLovvalg.fraDatagrunnlag(datagrunnlag)

    fun kjørHovedregler_slettmeg(): Resultat {
        return kjørRegelflyt(reglerForRegistrerteOpplysninger)
    }

    fun kjørHovedregler(): Resultat {
        val ytelse = reglerForRegistrerteOpplysninger.ytelse

        val res1 = kjørRegelflyt(reglerForRegistrerteOpplysninger)
        val res2 = kjørRegelflyt(reglerForMedl)
        val res3 = kjørRegelflyt(reglerForArbeidsforhold)
        val res4 = kjørRegelflyt(reglerForLovvalg)

        val resultater = listOf(
                reglerForRegistrerteOpplysninger,
//                reglerForMedl,
                reglerForArbeidsforhold,
                reglerForLovvalg
                ).map { kjørRegelflyt(it) }

        val medlemskonklusjon = resultater.find { it.erMedlemskonklusjon() }
        if (medlemskonklusjon != null) {
            return medlemskonklusjon.copy(delresultat = resultater.flatMap { it.delresultat }.utenKonklusjon())
        }

        val førsteNei = resultater.find { it.svar == Svar.NEI }
        if (førsteNei != null) {
            return neiKonklusjon(ytelse).utfør().copy(delresultat = resultater.flatMap { it.delresultat }.utenKonklusjon())
        }

        val førsteUavklart = resultater.find { it.svar == Svar.UAVKLART}
        if (førsteUavklart != null) {
            return uavklartKonklusjon(ytelse).utfør().copy(delresultat = resultater.flatMap { it.delresultat }.utenKonklusjon())
        }

        return jaKonklusjon(ytelse).utfør().copy(delresultat = resultater.flatMap { it.delresultat }.utenKonklusjon())
    }

    fun kjørRegelflyt(regler: Regler): Resultat {
        val resultatliste: MutableList<Resultat> = mutableListOf()
        regler.kjørRegelflyt(resultatliste)

        val konklusjon = resultatliste.hentUtKonklusjon()
        return konklusjon.copy(delresultat = resultatliste.utenKonklusjon())
    }

    private fun List<Resultat>.utenKonklusjon(): List<Resultat> {
        return this.filter { it.regelId != RegelId.REGEL_MEDLEM_KONKLUSJON && it.regelId != RegelId.REGEL_FLYT_KONKLUSJON  }
    }

    private fun List<Resultat>.hentUtKonklusjon(): Resultat {
        return this.find { it.regelId == RegelId.REGEL_MEDLEM_KONKLUSJON || it.regelId == RegelId.REGEL_FLYT_KONKLUSJON}
                ?: throw RuntimeException("Klarte ikke finne konklusjon")
    }
}
