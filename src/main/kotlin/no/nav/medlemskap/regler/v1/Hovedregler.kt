package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.common.*

class Hovedregler(personfakta: Personfakta) {

    private val resultatliste: MutableList<Resultat> = mutableListOf()
    private val reglerForRegistrerteOpplysninger = ReglerForRegistrerteOpplysninger(personfakta)
    private val reglerForRegistrerteOpplysningerIMedl = ReglerForMedl(personfakta)
    private val reglerForGrunnforordningen = ReglerForGrunnforordningen(personfakta)
    private val reglerForArbeidsforhold = ReglerForArbeidsforhold(personfakta)


    private fun hentHovedRegel() =

       //     sjekkRegelsett {
      //           reglerForRegistrerteOpplysningerIMedl
         //   } hvisNei {
                sjekkRegelsett {
                    reglerForRegistrerteOpplysninger
                } hvisJa {
                    sjekkRegelsett {
                        reglerForRegistrerteOpplysningerIMedl
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
                } hvisUavklart{
                        uavklartKonklusjon
                    }
            }

    fun kjørHovedregler(): Resultat {

        hentHovedRegel().utfør(resultatliste)
        val konklusjon = resultatliste.hentUtKonklusjon()
        return konklusjon.copy(delresultat = resultatliste.utenKonklusjon())
    }

    private fun List<Resultat>.utenKonklusjon(): List<Resultat> {
        return this.filter { it.identifikator != konklusjonIdentifikator }
    }

    private fun List<Resultat>.hentUtKonklusjon(): Resultat {
        return this.find { it.identifikator == konklusjonIdentifikator }
                ?: throw RuntimeException("Klarte ikke finne konklusjon")
    }
}
