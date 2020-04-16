package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.finnesI

class ReglerForLovvalg(val personfakta: Personfakta) : Regler() {

    override fun hentHovedRegel() =
            sjekkRegel {
                harBrukerJobbetUtenforNorge
            } hvisNei {
                jaKonklusjon
            } hvisJa {
                neiKonklusjon
            }

    private val harBrukerJobbetUtenforNorge = Regel(
            identifikator = "LOV-1",
            avklaring = "Har bruker oppgitt å ha jobbet utenfor Norge?",
            beskrivelse = "",
            operasjon = { sjekkOmBrukerHarJobbetUtenforNorge() }
    )

    private fun sjekkOmBrukerHarJobbetUtenforNorge(): Resultat =
            when {
                personfakta.hentBrukerinputArbeidUtenforNorge() -> ja()
                else -> nei()
            }


    private fun sjekkStatsborgerskapTilBruker(): Resultat =
             val førsteStatsborgerskap = personfakta.hentStatsborgerskapVedStartAvKontrollperiode()
             val sisteStatsborgerskap = personfakta.hentStatsborgerskapVedSluttAvKontrollperiode()
         /*       return when {
                    eøsLand finnesI førsteStatsborgerskap && eøsLand finnesI sisteStatsborgerskap -> ja()
                    else -> nei("Brukeren er ikke statsborger i et EØS-land.")
                }*/

}
