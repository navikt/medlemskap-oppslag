package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.konklusjonIdentifikator
import no.nav.medlemskap.regler.common.sjekkRegelsett
import no.nav.medlemskap.regler.common.uavklartKonklusjon

class Hovedregler(datagrunnlag: Datagrunnlag) {

    private val resultatliste: MutableList<Resultat> = mutableListOf()
    private val reglerForRegistrerteOpplysninger = ReglerForRegistrerteOpplysninger.fraDatagrunnlag(datagrunnlag)

    private val reglerForRegistrerteOpplysningerIMedl = ReglerForMedl.fraDatagrunnlag(datagrunnlag)
    private val reglerForGrunnforordningen = ReglerForGrunnforordningen.fraDatagrunnlag(datagrunnlag)
    private val reglerForArbeidsforhold = ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag)

    fun hentHovedRegel() =

    //sjekk Regelsett {
    //    reglerForRegistrerteOpplysningerIMedl
            //} hvisNei {
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
    //}

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
