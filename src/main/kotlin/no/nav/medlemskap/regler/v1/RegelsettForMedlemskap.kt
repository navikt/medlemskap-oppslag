package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.common.Regelsett
import no.nav.medlemskap.regler.common.Resultat

class RegelsettForMedlemskap : Regelsett("Regelsett for medlemskap") {

    private val manuelleVedtakFraNav = RegelsettForManuelleVedtak()
    private val grunnforordningen = RegelsettForGrunnforordningen()
    private val lovvalgNorge = RegelsettForNorskLovvalg()

    override val KONKLUSJON_IDENTIFIKATOR: String get() = "LOVME"

    override val KONKLUSJON_AVKLARING: String get() = "Er personen medlem av folketrygden?"

    override fun evaluer(personfakta: Personfakta): Resultat {
        val resultat =
                avklar {
                    personfakta oppfyller manuelleVedtakFraNav
                } hvisJa {
                    konkluderMed(uavklart("Personen har et manuelt vedtak om medlemskap fra NAV"))
                } hvisUavklart {
                    konkluderMed(uavklart("Kan ikke vurdere manuelle vedtak på grunn av mangelfulle data"))
                } hvisNei {
                    avklar {
                        personfakta oppfyller grunnforordningen
                    } hvisNei {
                        konkluderMed(uavklart("Personen er ikke omfattet av grunnordningen"))
                    } hvisUavklart {
                        konkluderMed(uavklart("Kan ikke vurdere grunnforordningen på grunn av mangelfulle data"))
                    } hvisJa {
                        avklar {
                            personfakta oppfyller lovvalgNorge
                        } hvisNei {
                            konkluderMed(uavklart("Lovvalg er ikke Norge"))
                        } hvisUavklart {
                            konkluderMed(uavklart("Kan ikke vurdere lovvalg på grunn av mangelfulle data"))
                        } hvisJa {
                            konkluderMed(ja("Personen er omfattet av norsk lovvalg, og dermed medlem"))
                        }
                    }
                }

        return hentUtKonklusjon(resultat)
    }

}
