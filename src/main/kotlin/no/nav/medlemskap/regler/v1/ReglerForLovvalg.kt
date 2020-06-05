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
            identifikator = "9",
            avklaring = "Har bruker utført arbeid utenfor Norge?",
            beskrivelse = "",
            operasjon = { sjekkOmBrukerHarJobbetUtenforNorge() }
    )

    private val erBrukerBosattINorge = Regel(
            identifikator = "10",
            avklaring = "Er bruker folkeregistrert som bosatt i Norge og har vært det i 12 mnd?",
            beskrivelse = "",
            operasjon = { sjekkLandskode() }
    )

    private val harBrukerNorskStatsborgerskap = Regel(
            identifikator = "11",
            avklaring = "Er bruker norsk statsborger?",
            beskrivelse = "",
            operasjon = { sjekkOmBrukerErNorskStatsborger() }
    )

    private val harBrukerJobbet25ProsentEllerMer = Regel(
            identifikator = "12",
            avklaring = "Har bruker vært i minst 25% stilling de siste 12 mnd?",
            beskrivelse = "",
            operasjon = { sjekkOmBrukerHarJobbet25ProsentEllerMer() }
    )

    private fun sjekkOmBrukerHarJobbetUtenforNorge(): Resultat =
            when {
                personfakta.hentBrukerinputArbeidUtenforNorge() -> ja()
                else -> nei()
            }

    private fun sjekkOmBrukerErNorskStatsborger(): Resultat {
        val førsteStatsborgerskap = personfakta.hentStatsborgerskapVedStartAvKontrollperiode()
        val sisteStatsborgerskap = personfakta.hentStatsborgerskapVedSluttAvKontrollperiode()
        return when {
            førsteStatsborgerskap inneholder "NOR" && sisteStatsborgerskap inneholder "NOR" -> ja()
            else -> nei("Brukeren er ikke norsk statsborger")
        }
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



}
