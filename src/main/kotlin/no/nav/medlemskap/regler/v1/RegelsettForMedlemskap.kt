package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.common.Fakta
import no.nav.medlemskap.regler.common.Regelsett
import no.nav.medlemskap.regler.common.Resultat

class RegelsettForMedlemskap(fakta: Fakta) : Regelsett("Regelsett for medlemskap", fakta) {

    private val manuelleVedtakFraNav = RegelsettForManuelleVedtak(fakta)
    private val grunnforordningen = RegelsettForGrunnforordningen(fakta)
    private val lovvalgNorge = RegelsettForNorskLovvalg(fakta)

    override val KONKLUSJON_IDENTIFIKATOR: String get() = "LOVME"

    override val KONKLUSJON_AVKLARING: String get() = "Er personen medlem av folketrygden?"

    override fun evaluer(): Resultat {
        val resultat =
                avklar {
                    manuelleVedtakFraNav.evaluer()
                } hvisJa {
                    konkluderMed(uavklart("Personen har et manuelt vedtak om medlemskap fra NAV"))
                } hvisUavklart {
                    konkluderMed(uavklart("Kan ikke vurdere manuelle vedtak på grunn av mangelfulle data"))
                } hvisNei {
                    avklar {
                        grunnforordningen.evaluer()
                    } hvisNei {
                        konkluderMed(uavklart("Personen er ikke omfattet av grunnordningen"))
                    } hvisUavklart {
                        konkluderMed(uavklart("Kan ikke vurdere grunnforordningen på grunn av mangelfulle data"))
                    } hvisJa {
                        avklar {
                            lovvalgNorge.evaluer()
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
