package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.common.*

class ReglerForLovvalg(val personfakta: Personfakta) : Regler() {

    override fun hentHovedRegel() =
            sjekkRegel {
                harBrukerJobbetUtenforNorge
            } hvisNei {
                sjekkRegel {
                    erBrukerBosattINorge
                } hvisNei {
                    uavklartKonklusjon
                } hvisJa {
                    sjekkRegel {
                        harBrukerJobbet25ProsentEllerMer
                    } hvisJa {
                        jaKonklusjon
                    } hvisNei {
                        uavklartKonklusjon
                    }
                }
            } hvisJa {
                neiKonklusjon
            }

    private val harBrukerJobbetUtenforNorge = Regel(
            identifikator = "LOV-1",
            avklaring = "Har bruker oppgitt å ha jobbet utenfor Norge?",
            beskrivelse = "",
            operasjon = { sjekkOmBrukerHarJobbetUtenforNorge() }
    )

    private val erBrukerBosattINorge = Regel(
            identifikator = "LOV-2",
            avklaring = "Er bruker registrert som bosatt i Norge og har vært det i 12 mnd?",
            beskrivelse = "",
            operasjon = { sjekkLandskode() }
    )

    private val harBrukerJobbet25ProsentEllerMer = Regel(
            identifikator = "LOV-3",
            avklaring = "Har bruker vært i minst 25% stilling?",
            beskrivelse = "",
            operasjon = { sjekkOmBrukerHarJobbet25ProsentEllerMer() }
    )

    private fun sjekkOmBrukerHarJobbetUtenforNorge(): Resultat =
            when {
                personfakta.hentBrukerinputArbeidUtenforNorge() -> ja()
                else -> nei()
            }

    private fun sjekkLandskode(): Resultat =
            when {
                personfakta.hentBrukerLandskodeInnenfor12Mnd().isNotEmpty() -> ja()
                else -> nei()
            }

    private fun sjekkOmBrukerHarJobbet25ProsentEllerMer(): Resultat =
            when {
                personfakta.harBrukerJobberMerEnnGittStillingsprosent(25.0) -> ja()
                else -> nei("Bruker har ikke jobbet 25% eller mer i løpet av periode.")
            }
}
