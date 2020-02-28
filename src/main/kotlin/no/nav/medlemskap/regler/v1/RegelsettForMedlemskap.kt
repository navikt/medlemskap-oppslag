package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.common.Regelsett
import no.nav.medlemskap.regler.common.Resultat

class RegelsettForMedlemskap : Regelsett("Regelsett for medlemskap") {

    private val registrerteOpplysninger = RegelsettForRegistrerteOpplysninger()
    private val grunnforordningen = RegelsettForGrunnforordningen()
    private val lovvalgNorge = RegelsettForNorskLovvalg()

    override val KONKLUSJON_IDENTIFIKATOR: String get() = "LOVME"

    override val KONKLUSJON_AVKLARING: String get() = "Er brukeren medlem i folketrygden"

    override fun evaluer(personfakta: Personfakta): Resultat {
        val resultat =
                avklar {
                    personfakta oppfyller registrerteOpplysninger
                } hvisJa {
                    konkluderMed(uavklart("Brukeren har registrerte og uregistrerte opplysninger på medlemskapsområdet"))
                } hvisUavklart {
                    konkluderMed(uavklart("Kan ikke vurdere opplysninger på grunn av mangelfulle data"))
                } hvisNei {
                    avklar {
                        personfakta oppfyller grunnforordningen
                    } hvisNei {
                        konkluderMed(uavklart("Brukeren er ikke omfattet av grunnordningen, men kan være omfattet av en annen trygdeavtale"))
                    } hvisUavklart {
                        konkluderMed(uavklart("Kan ikke vurdere om brukeren er omfattet av grunnforordningen på grunn av mangelfulle data"))
                    } hvisJa {
                        avklar {
                            personfakta oppfyller lovvalgNorge
                        } hvisNei {
                            konkluderMed(uavklart("Lovvalg er ikke Norge"))
                        } hvisUavklart {
                            konkluderMed(uavklart("Kan ikke vurdere lovvalg på grunn av mangelfulle data"))
                        } hvisJa {
                            konkluderMed(ja("Brukeren er omfattet av norsk lovvalg, og dermed medlem"))
                        }
                    }
                }

        return hentUtKonklusjon(resultat)
    }

}
