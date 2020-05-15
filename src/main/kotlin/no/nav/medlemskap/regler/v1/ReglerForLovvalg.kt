package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.inneholder

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
                        harBrukerNorskStatsborgerskap
                    } hvisJa {
                        sjekkRegel {
                            harBrukerJobbet25ProsentEllerMer
                        } hvisJa {
                            jaKonklusjon
                        } hvisNei {
                            uavklartKonklusjon
                        }
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

    private val harBrukerNorskStatsborgerskap = Regel(
            identifikator = "LOV-3",
            avklaring = "Har bruker norsk statsborgeskap",
            beskrivelse = "",
            operasjon = { sjekkOmBrukerErNorskStatsborger() }
    )

    private val harBrukerJobbet25ProsentEllerMer = Regel(
            identifikator = "LOV-4",
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
                personfakta.sjekkBrukersPostadresseOgBostedsadresseLandskode() -> ja()
                else -> nei("Bruker har enten ikke norsk bostedsadresse eller postadresse")
            }

    private fun sjekkOmBrukerHarJobbet25ProsentEllerMer(): Resultat =
            when {
                personfakta.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTidIKontrollperiode(25.0) -> ja()
                else -> nei("Bruker har ikke jobbet 25% eller mer i løpet av periode.")
            }


    private fun sjekkOmBrukerErNorskStatsborger(): Resultat {
        val førsteStatsborgerskap = personfakta.hentStatsborgerskapVedSluttAvKontrollperiodeNorskStatsborger()
        val sisteStatsborgerskap = personfakta.hentStatsborgerskapVedSluttAvKontrollperiodeNorskStatsborger()
        return when {
            førsteStatsborgerskap inneholder "NOR" && sisteStatsborgerskap inneholder "NOR" -> ja()
            else -> nei("Brukeren er ikke norsk statsborger")
        }
    }
}
