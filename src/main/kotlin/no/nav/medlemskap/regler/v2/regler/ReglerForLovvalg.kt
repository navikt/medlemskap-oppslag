package no.nav.medlemskap.regler.v2.regler

import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.v2.common.*

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
            identifikator = "LOV-5",
            avklaring = "Sjekk om personen har oppgitt å ha jobbet utenfor Norge",
            beskrivelse = "",
            operasjon = { sjekkOmBrukerHarJobbetUtenforNorge() }
    )

    private fun sjekkOmBrukerHarJobbetUtenforNorge(): Resultat =
            when {
                personfakta.hentBrukerinputArbeidUtenforNorge() -> ja("Bruker har oppgitt å ha jobbet utenfor Norge")
                else -> nei("Bruker har ikke jobbet utenfor Norge")
            }
}
