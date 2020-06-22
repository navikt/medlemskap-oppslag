package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.inneholder
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.harBrukerNorskAdresseInnenforSiste12Mnd
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.hentStatsborgerskapVedSluttAvKontrollperiode
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.hentStatsborgerskapVedStartAvKontrollperiode

class ReglerForLovvalg(val datagrunnlag: Datagrunnlag) : Regler() {

    val statsborgerskap = datagrunnlag.personhistorikk.statsborgerskap
    val postadresser = datagrunnlag.personhistorikk.postadresser
    val bostedsadresser = datagrunnlag.personhistorikk.bostedsadresser
    val arbeidsforhold = datagrunnlag.arbeidsforhold

    private val datohjelper = Datohjelper(datagrunnlag.periode)
    private val kontrollPeriodeForPersonhistorikk = datohjelper.kontrollPeriodeForPersonhistorikk()
    private val kontrollPeriodeForArbeidsforhold = datohjelper.kontrollPeriodeForArbeidsforhold()

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
                datagrunnlag.brukerinput.arbeidUtenforNorge -> ja()
                else -> nei()
            }

    private fun sjekkOmBrukerErNorskStatsborger(): Resultat {
        val førsteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedStartAvKontrollperiode(kontrollPeriodeForPersonhistorikk)
        val sisteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedSluttAvKontrollperiode(kontrollPeriodeForPersonhistorikk)

        return when {
            førsteStatsborgerskap inneholder "NOR" && sisteStatsborgerskap inneholder "NOR" -> ja()
            else -> nei("Brukeren er ikke norsk statsborger")
        }
    }

    private fun sjekkLandskode(): Resultat =
            when {
                sjekkBrukersPostadresseOgBostedsadresseLandskode() -> ja()
                else -> nei("Bruker har enten ikke norsk bostedsadresse eller postadresse")
            }

    private fun sjekkOmBrukerHarJobbet25ProsentEllerMer(): Resultat =
            when {
                 arbeidsforhold.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(25.0, kontrollPeriodeForArbeidsforhold) -> ja()
                else -> nei("Bruker har ikke jobbet 25% eller mer i løpet av periode.")
            }

    fun sjekkBrukersPostadresseOgBostedsadresseLandskode(): Boolean {
        val harIngenPostadresse = datagrunnlag.personhistorikk.postadresser.isEmpty()
        return (datagrunnlag.personhistorikk.postadresser.harBrukerNorskAdresseInnenforSiste12Mnd(kontrollPeriodeForPersonhistorikk) || harIngenPostadresse) &&
                datagrunnlag.personhistorikk.bostedsadresser.harBrukerNorskAdresseInnenforSiste12Mnd(kontrollPeriodeForPersonhistorikk)
    }
}
