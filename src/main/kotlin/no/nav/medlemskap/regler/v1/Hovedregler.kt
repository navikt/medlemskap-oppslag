package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat

class Hovedregler(datagrunnlag: Datagrunnlag) {
    private val resultatliste: MutableList<Resultat> = mutableListOf()
    private val reglerForRegistrerteOpplysninger = ReglerForRegistrerteOpplysninger.fraDatagrunnlag(datagrunnlag)

    fun kjørHovedregler(): Resultat {
        return kjørRegelflyt()
    }

    fun kjørRegelflyt(): Resultat {
        reglerForRegistrerteOpplysninger.hentRegelflyt().utfør(resultatliste)

        val konklusjon = resultatliste.hentUtKonklusjon()
        return konklusjon.copy(delresultat = resultatliste.utenKonklusjon())
    }

    private fun List<Resultat>.utenKonklusjon(): List<Resultat> {
        return this.filter { it.regelId != RegelId.REGEL_MEDLEM_KONKLUSJON }
    }

    private fun List<Resultat>.hentUtKonklusjon(): Resultat {
        return this.find { it.regelId == RegelId.REGEL_MEDLEM_KONKLUSJON }
                ?: throw RuntimeException("Klarte ikke finne konklusjon")
    }
}
