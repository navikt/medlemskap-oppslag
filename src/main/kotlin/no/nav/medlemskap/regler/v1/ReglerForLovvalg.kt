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
                    jaKonklusjon
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
            identifikator = "",
            avklaring = "Er bruker registrert som bosatt i Norge og har vært det i 12 mnd?",
            beskrivelse = "",
            operasjon = { sjekkLandskode() }
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

}
