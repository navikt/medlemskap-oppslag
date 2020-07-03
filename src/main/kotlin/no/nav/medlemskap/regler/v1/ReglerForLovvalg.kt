package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.alleEr
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.Funksjoner.erTom
import no.nav.medlemskap.regler.common.Funksjoner.finnesI
import no.nav.medlemskap.regler.common.Funksjoner.inneholder
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.adresserSiste12Mnd
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.landkodeTilAdresseSiste12Mnd
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.hentStatsborgerskapVedSluttAvKontrollperiode
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.hentStatsborgerskapVedStartAvKontrollperiode
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentEktefellerEllerPartnereIKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentFnrTilBarn


class ReglerForLovvalg(
        val personhistorikk: Personhistorikk,
        val arbeidsforhold: List<Arbeidsforhold>,
        val periode: InputPeriode,
        val ytelse: Ytelse,
        val arbeidUtenforNorge: Boolean,
        val personhistorikkRelatertPerson: List<PersonhistorikkRelatertPerson>
) : Regler() {
    val statsborgerskap = personhistorikk.statsborgerskap
    val postadresser = personhistorikk.postadresser
    val bostedsadresser = personhistorikk.bostedsadresser
    val midlertidigAdresser = personhistorikk.midlertidigAdresser
    val sivilstand = personhistorikk.sivilstand
    val familierelasjon = personhistorikk.familierelasjoner

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
                        sjekkRegel {
                           harBrukerNorsdiskStatsborgerskap
                        } hvisJa {
                           sjekkRegel {
                              harBrukerEktefelleEventuellebarn
                           } hvisJa {
                               jaKonklusjon(ytelse)
                           } hvisNei {
                               uavklartKonklusjon(ytelse)
                           }
                        } hvisNei {
                            uavklartKonklusjon(ytelse)
                        }
                    }
                }
            } hvisJa {
                neiKonklusjon(ytelse)
            }


    private val harBrukerJobbetUtenforNorge = Regel(
            identifikator = "9",
            avklaring = "Har bruker utført arbeid utenfor Norge?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { sjekkOmBrukerHarJobbetUtenforNorge() }
    )

    private val erBrukerBosattINorge = Regel(
            identifikator = "10",
            avklaring = "Er bruker folkeregistrert som bosatt i Norge og har vært det i 12 mnd?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { sjekkLandkode() }
    )

    private val harBrukerNorskStatsborgerskap = Regel(
            identifikator = "11",
            avklaring = "Er bruker norsk statsborger?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { sjekkOmBrukerErNorskStatsborger() }
    )

    private val harBrukerJobbet25ProsentEllerMer = Regel(
            identifikator = "12",
            avklaring = "Har bruker vært i minst 25% stilling de siste 12 mnd?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { sjekkOmBrukerHarJobbet25ProsentEllerMer() }
    )

    private val harBrukerNorsdiskStatsborgerskap = Regel (
            identifikator = "",
            avklaring = "Har bruker nordisk statsborgerskap?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = {sjekkOmBrukerHarNordiskStatsborgerskap() }
    )

    private val harBrukerEktefelleEventuellebarn = Regel (
            identifikator = "",
            avklaring = "Er brukers ektefelle og eventuelle barn folkeregistrert som bosatt i Norge?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = {sjekkOmBrukersBarnEventuelleEktefelleErBosattINorge()}
    )
//    datagrunnlag har også et felt personHistorikkRelatertePersoner,
//    som er personhistorikk fra TPS for de vi fant gjennom sivilstand og familierelasjoner.
    private fun sjekkOmBrukersBarnEventuelleEktefelleErBosattINorge(): Resultat {
  /*  Hvem skal vi ta med som "ektefelle"?
    Ektefelle
    Registrerte partnere*/
    //    val ektefeller = personhistorikkRelatertPerson.hentEkte
      //  val sivilstander = sivilstand.hentEktefellerEllerPartnereIKontrollPeriode(kontrollPeriodeForPersonhistorikk)
        val fnrTilBarn = familierelasjon.hentFnrTilBarn()
        val relatertePersoner = personhistorikkRelatertPerson
        return when {

        }
    }
    /*
    Vi henter nå data om sivilstand og familierelasjoner fra PDL.
    Dette lagres i datagrunnlag under pdlpersonhistorikk.
    Det er bare feltene sivilstand og familierelasjoner som kan brukes derfra.
    (pdlPersonhistorikk har også adresse-felter, men enn så lenge så bruker vi de fra TPS!)
    datagrunnlag har også et felt personHistorikkRelatertePersoner,
    som er personhistorikk fra TPS for de vi fant gjennom sivilstand og familierelasjoner.
    Det er mulig å ha registrert en sivilstand,
    men uten at det er registrert hvem sivilstanden er med.
    Da vil vi ha et innslag i sivilstand,
    men ikke et korresponderende innslag i personHistorikkRelatertePersoner.
    Det er også mulig å ha sivilstand og/eller familierelasjoner til noen som ikke har et ekte fnr.
    Dette kan gjelde dødfødte barn, barn født i utlandet og ektefeller i utlandet.
    De har heller ikke et korresponderende innslag i personHistorikkRelatertePersoner.
    For å løse denne oppgaven og sjekke brukers ektefelle og barn og deres bosatt-status,
    må man mao bruke alle disse datastrukturene jeg nå har nevnt.*/

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

    // Norden består av Danmark, Norge, Sverige, Finland og Island samt Færøyene, Grønland og Åland.
    // Her finner du nyttig informasjon om Norden og de enkelte nordiske landene.
    private fun sjekkOmBrukerHarNordiskStatsborgerskap(): Resultat {
        val førsteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedStartAvKontrollperiode(kontrollPeriodeForPersonhistorikk)
        val sisteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedSluttAvKontrollperiode(kontrollPeriodeForPersonhistorikk)
        return when {
            nordiskeLand finnesI førsteStatsborgerskap && nordiskeLand finnesI sisteStatsborgerskap -> ja()
            else -> nei("Brukeren er ikke statsborger i et nordisk land.")
        }

    }


    private fun sjekkOmBrukerHarJobbet25ProsentEllerMer(): Resultat =
            when {
                arbeidsforhold.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(25.0, kontrollPeriodeForArbeidsforhold, ytelse) -> ja()
                else -> nei("Bruker har ikke jobbet 25% eller mer i løpet av periode.")
            }


    private fun sjekkLandkode(): Resultat {
        val bostedsadresser = bostedsadresser.adresserSiste12Mnd(kontrollPeriodeForPersonhistorikk)
        val postadresserLandkoder = postadresser.landkodeTilAdresseSiste12Mnd(kontrollPeriodeForPersonhistorikk)
        val midlertidigadresserLandkoder = midlertidigAdresser.landkodeTilAdresseSiste12Mnd(kontrollPeriodeForPersonhistorikk)

        return when {
            bostedsadresser.erIkkeTom()
                    && (postadresserLandkoder alleEr NorskLandkode.NOR.name || postadresserLandkoder.erTom())
                    && (midlertidigadresserLandkoder alleEr NorskLandkode.NOR.name || midlertidigadresserLandkoder.erTom())-> ja()
            else -> nei("Ikke alle adressene til bruker er norske, eller bruker mangler bostedsadresse")
        }
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
                return ReglerForLovvalg(personhistorikk, arbeidsforhold, periode, ytelse, brukerinput.arbeidUtenforNorge)
            }
        }
    }
}
