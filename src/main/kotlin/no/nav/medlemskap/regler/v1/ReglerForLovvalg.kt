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
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentFnrTilBarnUnder25
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentFnrTilEktefellerEllerPartnerIPeriode
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentRelatertSomFinnesITPS
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.hentStatsborgerskapVedSluttAvKontrollperiode
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.hentStatsborgerskapVedStartAvKontrollperiode

class ReglerForLovvalg(
        val personhistorikk: Personhistorikk,
        val arbeidsforhold: List<Arbeidsforhold>,
        val periode: InputPeriode,
        val ytelse: Ytelse,
        val arbeidUtenforNorge: Boolean,
        val personhistorikkRelatertPerson: List<PersonhistorikkRelatertPerson>,
        val pdlPersonhistorikk: Personhistorikk?
) : Regler() {
    val statsborgerskap = personhistorikk.statsborgerskap
    val postadresser = personhistorikk.postadresser
    val bostedsadresser = personhistorikk.bostedsadresser
    val midlertidigAdresser = personhistorikk.midlertidigAdresser
    val sivilstand = pdlPersonhistorikk?.sivilstand
    val familierelasjon = pdlPersonhistorikk?.familierelasjoner

    private val datohjelper = Datohjelper(periode, ytelse)
    private val kontrollPeriodeForPersonhistorikk = datohjelper.kontrollPeriodeForPersonhistorikk()
    private val kontrollPeriodeForArbeidsforhold = datohjelper.kontrollPeriodeForArbeidsforhold()
    private val ektefelle = sivilstand?.hentFnrTilEktefellerEllerPartnerIPeriode(kontrollPeriodeForPersonhistorikk)
    private val ektefellerITps = personhistorikkRelatertPerson.hentRelatertSomFinnesITPS(ektefelle)
    private val barn = familierelasjon?.hentFnrTilBarnUnder25()
    private val barnITps = personhistorikkRelatertPerson.hentRelatertSomFinnesITPS(barn)

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
                        sjekkRegel {
                            harBrukerEktefelle
                        } hvisJa {
                            sjekkRegel {
                                harBrukerEktefelleOgBarn
                            } hvisJa {
                                sjekkRegel {
                                    erBrukerMedBarnSittEktefelleBosattINorge
                                } hvisJa {
                                    sjekkRegel {
                                        erBrukerMedFolkeregistrertEktefelleSittBarnFolkeregistrert
                                    } hvisJa {
                                        sjekkRegel {
                                            harBrukerMedFolkeregistrerteRelasjonerJobbetMerEnn80Prosent
                                        } hvisJa {
                                            jaKonklusjon(ytelse)
                                        } hvisNei {
                                            uavklartKonklusjon(ytelse)
                                        }
                                    } hvisNei {
                                        uavklartKonklusjon(ytelse)
                                    }
                                } hvisNei {
                                    sjekkRegel {
                                        erBrukerUtenFolkeregistrertEktefelleSittBarnFolkeregistrert
                                    } hvisJa {
                                        uavklartKonklusjon(ytelse)
                                    } hvisNei {
                                        sjekkRegel {
                                            harBrukerMedRelasjonerUtenFolkeregistreringJobbetMerEnn100Prosent
                                        } hvisJa {
                                            jaKonklusjon(ytelse)
                                        } hvisNei {
                                            uavklartKonklusjon(ytelse)
                                        }
                                    }
                                }
                            } hvisNei {
                                sjekkRegel {
                                    erBarnloesBrukersEktefelleBosattINorge
                                } hvisJa {
                                    sjekkRegel {
                                        harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn80Prosent
                                    } hvisJa {
                                        jaKonklusjon(ytelse)
                                    } hvisNei {
                                        uavklartKonklusjon(ytelse)
                                    }
                                }hvisNei {
                                    sjekkRegel {
                                        harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn100Prosent
                                    } hvisJa {
                                        jaKonklusjon(ytelse)
                                    } hvisNei {
                                        uavklartKonklusjon(ytelse)
                                    }
                                }
                            }
                        } hvisNei {
                            sjekkRegel {
                                harBrukerBarnUtenEktefelle
                            } hvisJa {
                                sjekkRegel{
                                    harBrukerUtenEktefelleBarnSomErFolkeregistrert
                                } hvisJa {
                                    sjekkRegel {
                                        harBrukerMedFolkeregistrerteBarnJobbetMerEnn80Prosent
                                    } hvisJa {
                                        jaKonklusjon(ytelse)
                                    } hvisNei {
                                        uavklartKonklusjon(ytelse)
                                    }
                                } hvisNei {
                                    sjekkRegel {
                                        harBrukerUtenFolkeregistrerteBarnJobbetMerEnn100Prosent
                                    } hvisJa {
                                         jaKonklusjon(ytelse)
                                    } hvisNei {
                                         uavklartKonklusjon(ytelse)
                                    }
                                }
                            } hvisNei {
                                    sjekkRegel {
                                        harBrukerUtenEktefelleOgBarnJobbetMerEnn100Prosent
                                    } hvisJa {
                                        jaKonklusjon(ytelse)
                                    } hvisNei {
                                        uavklartKonklusjon(ytelse)
                                    }
                                }
                        }
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

    val erBrukerBosattINorge = Regel(
            regelId = REGEL_10,
            ytelse = ytelse,
            operasjon = { sjekkLandkode() }
    )

    val harBrukerNorskStatsborgerskap = Regel(
            regelId = REGEL_11,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukerErNorskStatsborger() }
    )

    val harBrukerJobbet25ProsentEllerMer = Regel(
            regelId = REGEL_12,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukersStillingsprosentErMerEnn(25.0) }
    )

    private val harBrukerEktefelle = Regel (
            regelId = REGEL_11_2,
            ytelse = ytelse,
            operasjon = {sjekkOmBrukerHarEktefelle()}
    )

    private val harBrukerMedFolkeregistrerteBarnJobbetMerEnn80Prosent = Regel(
            regelId = REGEL_11_2_3,
            ytelse = ytelse,
            operasjon = {sjekkOmBrukersStillingsprosentErMerEnn(80.0)}
    )

    private val harBrukerMedRelasjonerUtenFolkeregistreringJobbetMerEnn100Prosent = Regel(
            regelId = REGEL_11_2_2_1,
            ytelse = ytelse,
            operasjon = {sjekkOmBrukersStillingsprosentErMerEnn(100.0)}
    )

    private val harBrukerUtenFolkeregistrerteBarnJobbetMerEnn100Prosent = Regel(
            regelId = REGEL_11_2_2_1,
            ytelse = ytelse,
            operasjon = {sjekkOmBrukersStillingsprosentErMerEnn(100.0)}
    )

    private val harBrukerUtenEktefelleOgBarnJobbetMerEnn100Prosent = Regel(
            regelId = REGEL_11_2_2_1,
            ytelse = ytelse,
            operasjon = {sjekkOmBrukersStillingsprosentErMerEnn(100.0)}
    )

    private val harBrukerUtenEktefelleBarnSomErFolkeregistrert = Regel (
            regelId = REGEL_11_2_2,
            ytelse = ytelse,
            operasjon = {sjekkOmBrukersBarnErBosattINorge()}
    )

    private val harBrukerBarnUtenEktefelle = Regel (
            regelId = REGEL_11_2_1,
            ytelse = ytelse,
            operasjon = {sjekkOmBrukerHarBarn()}
    )

    private val harBrukerEktefelleOgBarn = Regel (
            regelId = REGEL_11_3,
            ytelse = ytelse,
            operasjon = {sjekkOmBrukerHarBarn()}
    )

    private val erBarnloesBrukersEktefelleBosattINorge = Regel (
            regelId = REGEL_11_3_1,
            ytelse = ytelse,
            operasjon = {sjekkOmBrukersEktefelleErBosattINorge()}
    )

    private val harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn80Prosent= Regel(
            regelId = REGEL_11_6,
            ytelse = ytelse,
            operasjon = {sjekkOmBrukersStillingsprosentErMerEnn(80.0)}
    )

    private val harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn100Prosent= Regel(
            regelId = REGEL_11_3_1_1,
            ytelse = ytelse,
            operasjon = {sjekkOmBrukersStillingsprosentErMerEnn(100.0)}
    )

    private val erBrukerMedBarnSittEktefelleBosattINorge = Regel (
            regelId = REGEL_11_4,
            ytelse = ytelse,
            operasjon = {sjekkOmBrukersEktefelleErBosattINorge()}
    )

    private val erBrukerUtenFolkeregistrertEktefelleSittBarnFolkeregistrert = Regel (
            regelId = REGEL_11_4_1,
            ytelse = ytelse,
            operasjon = {sjekkOmBrukersBarnErBosattINorge()}
    )

    private val erBrukerMedFolkeregistrertEktefelleSittBarnFolkeregistrert = Regel (
            regelId = REGEL_11_5,
            ytelse = ytelse,
            operasjon = {sjekkOmBrukersBarnErBosattINorge()}
    )

    private val harBrukerMedFolkeregistrerteRelasjonerJobbetMerEnn80Prosent = Regel(
            regelId = REGEL_11_6,
            ytelse = ytelse,
            operasjon = {sjekkOmBrukersStillingsprosentErMerEnn(80.0)}
    )

    /* private val harBrukerNorsdiskStatsborgerskap = Regel (
        identifikator = "",
        avklaring = "Har bruker nordisk statsborgerskap?",
        beskrivelse = "",
        ytelse = ytelse,
        operasjon = {sjekkOmBrukerHarNordiskStatsborgerskap() }
        )
    */

    private fun sjekkOmBrukerHarBarn(): Resultat {
        return when {
            barnITps.erIkkeTom() -> ja()
            else -> nei("Bruker har ikke barn i tps")
        }
     }

    private fun sjekkOmBrukerHarEktefelle(): Resultat {
      return when {
        ektefellerITps.erIkkeTom() -> ja()
        else -> nei("Bruker har ikke ektefelle i tps")
     }
    }

    private fun sjekkOmBrukersEktefelleErBosattINorge(): Resultat {
        val bostedsadresserTilEktefelle = ektefellerITps.flatMap { it.bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk) }
        val postAdresseTilEktefelle = ektefellerITps.flatMap { it.postadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk) }
        val midlertidigPostadresseTilEktefelle = ektefellerITps.flatMap { it.midlertidigAdresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk) }
             return when {
                erPersonBosattINorge(bostedsadresserTilEktefelle, postAdresseTilEktefelle, midlertidigPostadresseTilEktefelle) -> ja()
                else -> nei("Ikke alle adressene til ektefelle er norske, eller ektefelle mangler bostedsadresse")
        }
    }

    private fun sjekkOmBrukersBarnErBosattINorge(): Resultat {
        val bostedsadresseTilBarn = barnITps.flatMap { it.bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk) }
        val postAdresseTilBarn = barnITps.flatMap { it.postadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk) }
        val midlertidigPostadresseTilBarn = barnITps.flatMap { it.midlertidigAdresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk) }
             return when {
                erPersonBosattINorge(bostedsadresseTilBarn, postAdresseTilBarn, midlertidigPostadresseTilBarn) -> ja()
                 else -> nei("Ikke alle adressene til ektefelle er norske, eller ektefelle mangler bostedsadresse")
              }
    }

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

    /*
    private fun sjekkOmBrukerHarNordiskStatsborgerskap(): Resultat {
        val førsteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedStartAvKontrollperiode(kontrollPeriodeForPersonhistorikk)
        val sisteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedSluttAvKontrollperiode(kontrollPeriodeForPersonhistorikk)
        return when {
            nordiskeLand finnesI førsteStatsborgerskap && nordiskeLand finnesI sisteStatsborgerskap -> ja()
            else -> nei("Brukeren er ikke statsborger i et nordisk land.")
        }

    }
    */

    private fun sjekkOmBrukersStillingsprosentErMerEnn(stillingsprosent: Double): Resultat =
            when {
                arbeidsforhold.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(stillingsprosent, kontrollPeriodeForArbeidsforhold, ytelse) -> ja()
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
            erPersonBosattINorge(bostedsadresser, postadresserLandkoder, midlertidigadresserLandkoder) -> ja()
            else -> nei("Ikke alle adressene til bruker er norske, eller bruker mangler bostedsadresse")
        }
    }

    private fun List<Adresse>.brukerHarNorskBostedsadresse() : Boolean {
        return this.erIkkeTom()
    }

    private fun personHarIngenEllerNorskPostadresse(postadresseLandkode: List<String>): Boolean {
        return postadresseLandkode alleEr NorskLandkode.NOR.name || postadresseLandkode.erTom()
    }

    private fun personHarIngenEllerNorskMidlertidigadresse(midlertidigadresserLandkoder: List<String>): Boolean {
        return midlertidigadresserLandkoder alleEr NorskLandkode.NOR.name || midlertidigadresserLandkoder.erTom()
    }

    private fun erPersonBosattINorge(boadadresse: List<Adresse>, postadresseLandkoder: List<String>, midlertidigAdresseLandkoder: List<String>): Boolean {
        return boadadresse.brukerHarNorskBostedsadresse()
                && personHarIngenEllerNorskPostadresse(postadresseLandkoder)
                && personHarIngenEllerNorskMidlertidigadresse(midlertidigAdresseLandkoder)
    }

    enum class NorskLandkode {
        NOR
    }

    private val nordiskeLand = mapOf(
            "DNK" to "DANMARK",
            "FIN" to "FINLAND",
            "ISL" to "ISLAND",
            "SWE" to "SVERIGE",
            "NOR" to "NORGE",
            "FRO" to "FÆRØYENE",
            "GRL" to "GRØNNLAND",
            "ALA" to "ÅLAND"
    )

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForLovvalg {
            with(datagrunnlag) {
                return ReglerForLovvalg(personhistorikk, arbeidsforhold, periode, ytelse, brukerinput.arbeidUtenforNorge, personHistorikkRelatertePersoner, pdlpersonhistorikk)
            }
        }
    }
}
