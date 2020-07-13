package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.alleEr
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.Funksjoner.erTom
import no.nav.medlemskap.regler.common.Funksjoner.inneholder
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.adresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.landkodeTilAdresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.hentStatsborgerskapVedSluttAvKontrollperiode
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.hentStatsborgerskapVedStartAvKontrollperiode

class ReglerForLovvalg(
        val personhistorikk: Personhistorikk,
        val arbeidsforhold: List<Arbeidsforhold>,
        val periode: InputPeriode,
        val ytelse: Ytelse,
        val arbeidUtenforNorge: Boolean
) : Regler() {
    val statsborgerskap = personhistorikk.statsborgerskap
    val postadresser = personhistorikk.postadresser
    val bostedsadresser = personhistorikk.bostedsadresser
    val midlertidigAdresser = personhistorikk.midlertidigAdresser

    private val datohjelper = Datohjelper(periode, ytelse)
    private val kontrollPeriodeForPersonhistorikk = datohjelper.kontrollPeriodeForPersonhistorikk()
    private val kontrollPeriodeForArbeidsforhold = datohjelper.kontrollPeriodeForArbeidsforhold()

    override fun hentHovedRegel() =
            sjekkRegel {
                harBrukerJobbetUtenforNorge
            } hvisNei {
                sjekkRegel {
                    erBrukerBosattINorge
                } hvisNei {
                    uavklartKonklusjon(ytelse)
                } hvisJa {
                    sjekkRegel {
                        harBrukerNorskStatsborgerskap
                    } hvisJa {
                        sjekkRegel {
                            harBrukerJobbet25ProsentEllerMer
                        } hvisJa {
                            jaKonklusjon(ytelse)
                        } hvisNei {
                            uavklartKonklusjon(ytelse)
                        }
                    } hvisNei {
                        uavklartKonklusjon(ytelse)
                    }
                }
            } hvisJa {
                neiKonklusjon(ytelse)
            }


    val harBrukerJobbetUtenforNorge = Regel(
            regelId = REGEL_9,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukerHarJobbetUtenforNorge() }
    )

    val erBrukerBosattINorge = ErBrukerBosattINorgeRegel(
            postadresser = postadresser,
            bostedsadresser = bostedsadresser,
            midlertidigAdresser = midlertidigAdresser,
            ytelse = ytelse,
            periode = periode
    ).regel

    val harBrukerNorskStatsborgerskap = Regel(
            REGEL_11,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukerErNorskStatsborger() }
    )

    val harBrukerJobbet25ProsentEllerMer = Regel(
            REGEL_12,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukerHarJobbet25ProsentEllerMer() }
    )

    private fun sjekkOmBrukerHarJobbetUtenforNorge(): Resultat =
            when {
                arbeidUtenforNorge -> ja()
                else -> nei()
            }

    private fun sjekkOmBrukerErNorskStatsborger(): Resultat {
        val førsteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedStartAvKontrollperiode(kontrollPeriodeForPersonhistorikk)
        val sisteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedSluttAvKontrollperiode(kontrollPeriodeForPersonhistorikk)

        return when {
            førsteStatsborgerskap inneholder NorskLandkode.NOR.name
                    && sisteStatsborgerskap inneholder NorskLandkode.NOR.name -> ja()
            else -> nei("Brukeren er ikke norsk statsborger")
        }
    }

    private fun sjekkOmBrukerHarJobbet25ProsentEllerMer(): Resultat =
            when {
                arbeidsforhold.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(25.0, kontrollPeriodeForArbeidsforhold, ytelse) -> ja()
                else -> nei("Bruker har ikke jobbet 25% eller mer i løpet av periode.")
            }


    private fun sjekkLandkode(): Resultat {
        val bostedsadresser = bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)
        val postadresserLandkoder = postadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)
        val midlertidigadresserLandkoder = midlertidigAdresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)

        return when {
            bostedsadresser.erIkkeTom()
                    && (postadresserLandkoder alleEr NorskLandkode.NOR.name || postadresserLandkoder.erTom())
                    && (midlertidigadresserLandkoder alleEr NorskLandkode.NOR.name || midlertidigadresserLandkoder.erTom()) -> ja()
            else -> nei("Ikke alle adressene til bruker er norske, eller bruker mangler bostedsadresse")
        }
    }

    enum class NorskLandkode {
        NOR
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForLovvalg {
            with(datagrunnlag) {
                return ReglerForLovvalg(personhistorikk, arbeidsforhold, periode, ytelse, brukerinput.arbeidUtenforNorge)
            }
        }
    }
}
