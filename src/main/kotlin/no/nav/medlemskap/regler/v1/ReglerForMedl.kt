package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.DekningForSykepenger
import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.inneholderNoe
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.harSammeAdressePaaGitteDatoer
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.arbeidsforholdForDato
import no.nav.medlemskap.regler.funksjoner.GsakFunksjoner.finnesAapneOppgaver
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.erMedlemskapsperioderOver12Mnd
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.finnesPersonIMedlForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.harGyldigeMedlemskapsperioder
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.harMedlPeriodeMedOgUtenMedlemskap
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.harPeriodeMedMedlemskap
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.medlemskapsPerioderOver12MndPeriodeDekning
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.tidligsteFraOgMedDatoForMedl

class ReglerForMedl(val datagrunnlag: Datagrunnlag) : Regler() {

    private val gyldigeDekningerForSykepenger = DekningForSykepenger.values().map { it.dekning }
    private val medlemskap: List<Medlemskap> = datagrunnlag.medlemskap
    private val arbeidsforhold: List<Arbeidsforhold> = datagrunnlag.arbeidsforhold
    private val kontrollPeriodeForMedl = Datohjelper(datagrunnlag.periode).kontrollPeriodeForMedl()
    private val datohjelper = Datohjelper(datagrunnlag.periode)

    override fun hentHovedRegel(): Regel =
            sjekkRegel {
                harBrukerMedlOpplysninger
            } hvisJa {
                sjekkRegel {
                    harBrukerGosysOpplysninger
                } hvisNei {
                    sjekkRegel {
                        periodeMedOgUtenMedlemskap
                    } hvisJa {
                        uavklartKonklusjon
                    } hvisNei {
                        sjekkRegel {
                            periodeMedMedlemskap
                        } hvisNei {
                            sjekkRegel {
                                erPeriodeUtenMedlemskapInnenfor12MndPeriode
                            } hvisNei {
                                uavklartKonklusjon
                            } hvisJa {
                                sjekkRegel {
                                    erArbeidsforholdUendretForBrukerUtenMedlemskap
                                } hvisJa {
                                    sjekkRegel {
                                        erAdresseUendretForBrukerUtenMedlemskap
                                    } hvisJa {
                                        neiKonklusjon
                                    } hvisNei {
                                        uavklartKonklusjon
                                    }
                                } hvisNei {
                                    uavklartKonklusjon
                                }
                            }
                        } hvisJa {
                            sjekkRegel {
                                erPeriodeMedMedlemskapInnenfor12MndPeriode
                            } hvisJa {
                                sjekkRegel {
                                    erArbeidsforholdUendretForBrukerMedMedlemskap
                                } hvisJa {
                                    sjekkRegel {
                                        erAdresseUendretForBrukerMedMedlemskap
                                    } hvisJa {
                                        sjekkRegel {
                                            harBrukerDekningIMedl
                                        } hvisJa {
                                            jaKonklusjon
                                        } hvisNei {
                                            neiKonklusjon
                                        }
                                    } hvisNei {
                                        uavklartKonklusjon
                                    }
                                } hvisNei {
                                    uavklartKonklusjon
                                }
                            } hvisNei {
                                uavklartKonklusjon
                            }
                        }
                    }
                } hvisJa {
                    uavklartKonklusjon
                }
            }

    private val harBrukerMedlOpplysninger = Regel(
            identifikator = "A",
            avklaring = "Finnes det noe på personen i MEDL?",
            beskrivelse = """
                Vedtak (gjort av NAV eller utenlandsk trygdemyndighet) som er registrert i MEDL, 
                må vurderes manuelt og det må vurderes om brukers situasjon er uendret i forhold 
                til situasjonen på vedtakstidspunktet.
            """.trimIndent(),
            operasjon = { harBrukerPerioderIMedl() }
    )

    private val harBrukerGosysOpplysninger = Regel(
            identifikator = "B",
            avklaring = "Finnes det åpne oppgaver i GOSYS på medlemskapsområdet?",
            beskrivelse = """"
                Skal sikre at ubehandlede saker og ikke-registrerte vedtak fanges opp for å bli 
                vurdert manuelt. MEDL er ikke en komplett oversikt over alle medlemsavklaringene 
                som NAV har gjort. 
            """.trimIndent(),
            operasjon = { harBrukerAapneOppgaverIGsak() }
    )

    private val periodeMedOgUtenMedlemskap = Regel(
            identifikator = "1.1",
            avklaring = "Er det periode både med og uten medlemskap innenfor 12 mnd?",
            beskrivelse = """
                Dersom en bruker har en periode med medlemskap og en periode uten medlemskap innenfor 12 mnd 
                periode, skal det gå til uavklart.
            """.trimIndent(),
            operasjon = { harPeriodeMedOgUtenMedlemskap() }
    )

    private val periodeMedMedlemskap = Regel(
            identifikator = "1.2",
            avklaring = "Er det en periode med medlemskap?",
            beskrivelse = """"
               Har medlemskap og Norge er lovvalg. 
            """.trimIndent(),
            operasjon = { periodeMedMedlemskap() }
    )

    private val erPeriodeUtenMedlemskapInnenfor12MndPeriode = Regel(
            identifikator = "1.2.1",
            avklaring = "Er hele perioden uten medlemskap innenfor 12-måneders perioden?",
            beskrivelse = """"
               Er hele perioden uten medlemskap innenfor 12-månedersperioden?
            """.trimIndent(),
            operasjon = { erMedlemskapPeriodeOver12MndPeriode(false) }
    )

    private val erPeriodeMedMedlemskapInnenfor12MndPeriode = Regel(
            identifikator = "1.3",
            avklaring = "Er hele perioden med medlemskap innenfor 12-måneders perioden?",
            beskrivelse = """"
               Er hele perioden med medlemskap innenfor 12-månedersperioden?
            """.trimIndent(),
            operasjon = { erMedlemskapPeriodeOver12MndPeriode(true) }
    )


    private val erArbeidsforholdUendretForBrukerUtenMedlemskap = Regel(
            identifikator = "1.2.2",
            avklaring = "Er bruker uten medlemskap sin situasjon uendret?",
            beskrivelse = """"
                Er brukers situasjon uendret i forhold til da A1 ble utstedt? Sjekker at det er samme arbeidsforhold
                i dag som på fra og med tidspunktet for perioden gitt fra MEDL
            """.trimIndent(),
            operasjon = { erBrukersArbeidsforholdUendret() }
    )

    private val erAdresseUendretForBrukerUtenMedlemskap = Regel(
            identifikator = "1.2.3",
            avklaring = "Er bruker uten medlemskap sin situasjon uendret?",
            beskrivelse = """"
                Er brukers situasjon uendret i forhold til da A1 ble utstedt? Sjekker at det er samme arbeidsforhold
                i dag som på fra og med tidspunktet for perioden gitt fra MEDL
            """.trimIndent(),
            operasjon = { erBrukersAdresseUendret() }
    )

    private val erArbeidsforholdUendretForBrukerMedMedlemskap = Regel(
            identifikator = "1.4",
            avklaring = "Er brukers situasjon uendret?",
            beskrivelse = """"
                Er brukers situasjon uendret i forhold til da A1 ble utstedt? Sjekker at det er samme arbeidsforhold
                i dag som på fra og med tidspunktet for perioden gitt fra MEDL
            """.trimIndent(),
            operasjon = { erBrukersArbeidsforholdUendret() }
    )

    private val erAdresseUendretForBrukerMedMedlemskap = Regel(
            identifikator = "1.5",
            avklaring = "Er brukers situasjon uendret?",
            beskrivelse = """"
                Er brukers situasjon uendret i forhold til da A1 ble utstedt? Sjekker at det er samme arbeidsforhold
                i dag som på fra og med tidspunktet for perioden gitt fra MEDL
            """.trimIndent(),
            operasjon = { erBrukersAdresseUendret() }
    )

    private val harBrukerDekningIMedl = Regel(
            identifikator = "1.6",
            avklaring = "Har bruker et medlemskap som omfatter sykepenger? (Dekning i MEDL)",
            beskrivelse = """"
                Har bruker et medlemskap som omfatter sykepenger? Sjekker registrert dekning gitt fra MEDL
            """.trimIndent(),
            operasjon = { harBrukerMedlemskapSomOmfatterSykepenger() }
    )

    private fun harBrukerPerioderIMedl(): Resultat =
            when {
                medlemskap finnesPersonIMedlForKontrollPeriode kontrollPeriodeForMedl -> ja()
                else -> nei()
            }

    private fun harBrukerAapneOppgaverIGsak(): Resultat =
            when {
                datagrunnlag.oppgaver.finnesAapneOppgaver() -> ja()
                else -> nei()
            }

    private fun harPeriodeMedOgUtenMedlemskap(): Resultat =
            when {
                medlemskap harMedlPeriodeMedOgUtenMedlemskap kontrollPeriodeForMedl -> ja()
                else -> nei()
            }

    private fun erBrukersAdresseUendret(): Resultat =
            when {
                datagrunnlag.personhistorikk.bostedsadresser.harSammeAdressePaaGitteDatoer(
                        medlemskap.tidligsteFraOgMedDatoForMedl(kontrollPeriodeForMedl), kontrollPeriodeForMedl.tom!!) -> ja()
                else -> nei()
            }

    private fun erBrukersArbeidsforholdUendret(): Resultat =
            when {
                harSammeArbeidsforholdSidenFomDatoFraMedl() -> ja()
                else -> nei()
            }

    private fun periodeMedMedlemskap(): Resultat =
            when {
                medlemskap harPeriodeMedMedlemskap kontrollPeriodeForMedl -> ja()
                else -> nei()
            }


    private fun erMedlemskapPeriodeOver12MndPeriode(finnPeriodeMedMedlemskap: Boolean): Resultat =
            when {
                medlemskap.erMedlemskapsperioderOver12Mnd(finnPeriodeMedMedlemskap, kontrollPeriodeForMedl)
                        && medlemskap harGyldigeMedlemskapsperioder kontrollPeriodeForMedl -> ja()
                else -> nei()
            }

    private fun harBrukerMedlemskapSomOmfatterSykepenger(): Resultat =
            when {
                medlemskap medlemskapsPerioderOver12MndPeriodeDekning kontrollPeriodeForMedl inneholderNoe gyldigeDekningerForSykepenger -> ja()
                else -> nei()
            }

    private fun harSammeArbeidsforholdSidenFomDatoFraMedl(): Boolean =
            arbeidsforhold.arbeidsforholdForDato(medlemskap.tidligsteFraOgMedDatoForMedl(datohjelper.kontrollPeriodeForMedl())).isNotEmpty() &&
                    (ulikeArbeidsforholdMenSammeArbeidsgiver() ||
                            arbeidsforhold.arbeidsforholdForDato(medlemskap.tidligsteFraOgMedDatoForMedl(datohjelper.kontrollPeriodeForMedl())) ==
                            arbeidsforhold.arbeidsforholdForDato(datohjelper.tilOgMedDag()))

    private fun ulikeArbeidsforholdMenSammeArbeidsgiver() =
            (arbeidsforhold.arbeidsforholdForDato(medlemskap.tidligsteFraOgMedDatoForMedl(datohjelper.kontrollPeriodeForMedl())) != arbeidsforhold.arbeidsforholdForDato(datohjelper.tilOgMedDag()) &&
                    arbeidsforhold.arbeidsforholdForDato(medlemskap.tidligsteFraOgMedDatoForMedl(datohjelper.kontrollPeriodeForMedl())).map { it.arbeidsgiver.identifikator } ==
                    arbeidsforhold.arbeidsforholdForDato(datohjelper.tilOgMedDag()).map { it.arbeidsgiver.identifikator })
}