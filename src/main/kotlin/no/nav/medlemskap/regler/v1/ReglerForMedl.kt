package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.DekningForSykepenger
import no.nav.medlemskap.domene.Oppgave
import no.nav.medlemskap.domene.Status
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.er
import no.nav.medlemskap.regler.common.Funksjoner.erDelAv
import no.nav.medlemskap.regler.common.Funksjoner.inneholderNoe

class ReglerForMedl(val personfakta: Personfakta) : Regler() {

    private val gyldigeDekningerForSykepenger = DekningForSykepenger.values().map { it.dekning }

    override fun hentHovedRegel(): Regel =
       sjekkRegel {
           harBrukerMedlOpplysninger
       } hvisJa {
            sjekkRegel {
                brukerHarAapneSakerIGSAK
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
            identifikator = "1",
            avklaring = "Finnes det noe på personen i MEDL?",
            beskrivelse = """
                Vedtak (gjort av NAV eller utenlandsk trygdemyndighet) som er registrert i MEDL, 
                må vurderes manuelt og det må vurderes om brukers situasjon er uendret i forhold 
                til situasjonen på vedtakstidspunktet.
            """.trimIndent(),
            operasjon = { sjekkPerioderIMedl()}
    )

    private val brukerHarAapneSakerIGSAK = Regel(
            identifikator = "1",
            avklaring = "Finnes det noe på personen i MEDL, og ikke i GSAK?",
            beskrivelse = """
                Vedtak (gjort av NAV eller utenlandsk trygdemyndighet) som er registrert i MEDL, 
                må vurderes manuelt og det må vurderes om brukers situasjon er uendret i forhold 
                til situasjonen på vedtakstidspunktet.
            """.trimIndent(),
            operasjon = { sjekkPerioderIGSAK() }
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

    private fun sjekkPerioderIMedl(): Resultat =
            when {
                personfakta.finnesPersonIMedlSiste12mnd() -> ja()
                else -> nei()
            }

    // Ikke noe i GSAK
    private fun sjekkPerioderIGSAK(): Resultat =
            when {
                personfakta.personensOppgaverIGsak().antallAapneOppgaver > 0 -> ja()
                else -> nei()
            }


    private fun harPeriodeMedOgUtenMedlemskap(): Resultat  =
            when {
                personfakta.harMedlPeriodeMedOgUtenMedlemskap() -> ja()
                else -> nei()
            }

    private fun erBrukersAdresseUendret(): Resultat =
            when {
                personfakta.harSammeAdresseSidenFomDatoFraMedl() -> ja()
                else -> nei()
            }

    private fun erBrukersArbeidsforholdUendret(): Resultat =
            when {
                personfakta.harSammeArbeidsforholdSidenFomDatoFraMedl() -> ja()
                else -> nei()
            }

    private fun periodeMedMedlemskap(): Resultat =
            when {
                personfakta.brukerensPerioderIMedlSiste12Mnd().stream().anyMatch {
                    it.erMedlem && it.lovvalgsland er "NOR" && it.lovvalg er "ENDL"
                } -> ja()
                else -> nei()
            }


    private fun erMedlemskapPeriodeOver12MndPeriode(finnPeriodeMedMedlemskap: Boolean): Resultat =
            when {
                personfakta.erMedlemskapPeriodeOver12MndPeriode(finnPeriodeMedMedlemskap)
                        && personfakta.harGyldigeMedlemskapsperioder() -> ja()
                else -> nei()
            }

    private fun harBrukerMedlemskapSomOmfatterSykepenger(): Resultat =
            when {
                personfakta.medlemskapsPerioderOver12MndPeriodeDekning() inneholderNoe gyldigeDekningerForSykepenger -> ja()
                else -> nei()
            }

    private val List<Oppgave>.antallAapneOppgaver: Int
        get() = count { oppgave ->
            oppgave.tema erDelAv tillatteTemaer && oppgave.status erDelAv tillatteStatuser
        }
    private val tillatteTemaer = listOf("MED", "UFM", "TRY")
    private val tillatteStatuser = listOf(Status.AAPNET, Status.OPPRETTET, Status.UNDER_BEHANDLING)
}