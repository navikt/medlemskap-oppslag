package no.nav.medlemskap.regler.v2

import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.v2.common.Resultat
import no.nav.medlemskap.regler.v2.common.sjekkRegelsett
import no.nav.medlemskap.regler.v2.common.uavklartKonklusjon
import no.nav.medlemskap.regler.v2.regler.ReglerForArbeidsforhold
import no.nav.medlemskap.regler.v2.regler.ReglerForGrunnforordningen
import no.nav.medlemskap.regler.v2.regler.ReglerForRegistrerteOpplysninger
import java.util.*

class Hovedregler(personfakta: Personfakta) {

    private val resultatliste: MutableList<Resultat> = mutableListOf()

    private val reglerForRegistrerteOpplysninger = ReglerForRegistrerteOpplysninger(personfakta)
    private val reglerForGrunnforordningen = ReglerForGrunnforordningen(personfakta)
    private val reglerForArbeidsforhold = ReglerForArbeidsforhold(personfakta)

    private fun hentHovedRegel() =
            sjekkRegelsett {
                reglerForRegistrerteOpplysninger
            } hvisJa {
                uavklartKonklusjon
            } hvisNei {
                sjekkRegelsett {
                    reglerForGrunnforordningen
                } hvisNei {
                    uavklartKonklusjon
                } hvisJa {
                    sjekkRegelsett {
                        reglerForArbeidsforhold
                    }
                }
            }

    fun kjørHovedregler(): List<Resultat> {
        hentHovedRegel().utfør(resultatliste)
        return resultatliste.medKonklusjonFørst()
    }

    private fun List<Resultat>.medKonklusjonFørst(): List<Resultat> {
        val mutableResultat = this.toMutableList()
        Collections.rotate(mutableResultat, 1)
        return mutableResultat
    }
}
