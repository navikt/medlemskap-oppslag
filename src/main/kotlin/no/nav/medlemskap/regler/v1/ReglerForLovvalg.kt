package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.common.*

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
}
