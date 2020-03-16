package no.nav.medlemskap.regler.v2

import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.v2.common.Resultat
import no.nav.medlemskap.regler.v2.common.sjekkRegelsett
import no.nav.medlemskap.regler.v2.common.uavklartKonklusjon
import no.nav.medlemskap.regler.v2.regler.ReglerForArbeidsforhold
import no.nav.medlemskap.regler.v2.regler.ReglerForGrunnforordningen
import no.nav.medlemskap.regler.v2.regler.ReglerForRegistrerteOpplysninger

class Hovedregler(personfakta: Personfakta) {

    private val resultatliste: MutableList<Resultat> = mutableListOf()
    private val reglerForRegistrerteOpplysninger = ReglerForRegistrerteOpplysninger(personfakta)
    private val reglerForGrunnforordningen = ReglerForGrunnforordningen(personfakta)
    private val reglerForArbeidsforhold = ReglerForArbeidsforhold(personfakta)

    fun kjørHovedregler(): List<Resultat> {
        val regel =
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

        regel.utfør(resultatliste)
        return resultatliste
    }
}
