package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.er

class ReglerForMedl(val personfakta: Personfakta) : Regler() {


    override fun hentHovedRegel(): Regel =
            sjekkRegel {
                harBrukerMedlOpplysninger
            } hvisJa {
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
                                erSituasjonenUendretForBrukerUtenMedlemskap
                            } hvisJa {
                                neiKonklusjon
                            } hvisNei {
                                uavklartKonklusjon
                            }
                        }
                    } hvisJa {
                        sjekkRegel {
                            erPeriodeMedMedlemskapInnenfor12MndPeriode
                        } hvisJa {
                            sjekkRegel {
                                erSituasjonenUendretForBrukerMedMedlemskap
                            } hvisJa {
                                jaKonklusjon
                            } hvisNei {
                                uavklartKonklusjon
                            }
                        } hvisNei {
                            uavklartKonklusjon
                        }
                    }
                }
            }

    private val harBrukerMedlOpplysninger = Regel(
            identifikator = "1",
            avklaring = "1 - Finnes det noe på personen i MEDL?",
            beskrivelse = """
                Vedtak (gjort av NAV eller utenlandsk trygdemyndighet) som er registrert i MEDL, 
                må vurderes manuelt og det må vurderes om brukers situasjon er uendret i forhold 
                til situasjonen på vedtakstidspunktet.
            """.trimIndent(),
            operasjon = { sjekkPerioderIMedl() }
    )

    private val periodeMedOgUtenMedlemskap = Regel(
            identifikator = "1.1",
            avklaring = "1.1 - Er det periode både med og uten medlemskap innenfor 12 mnd?",
            beskrivelse = """
                Dersom en bruker har en periode med medlemskap og en periode uten medlemskap innenfor 12 mnd 
                periode, skal det gå til uavklart.
            """.trimIndent(),
            operasjon = { harPeriodeMedOgUtenMedlemskap() }
    )

    private val periodeMedMedlemskap = Regel(
            identifikator = "1.2",
            avklaring = "1.2 - Er det en periode med medlemskap?",
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


    private val erSituasjonenUendretForBrukerUtenMedlemskap = Regel(
            identifikator = "1.2.2",
            avklaring = "Er bruker uten medlemskap sin situasjon uendret?",
            beskrivelse = """"
                Er brukers situasjon uendret i forhold til da A1 ble utstedt? Sjekker at det er samme arbeidsforhold
                i dag som på fra og med tidspunktet for perioden gitt fra MEDL
            """.trimIndent(),
            operasjon = { situasjonenErUendret() }
    )

    private val erSituasjonenUendretForBrukerMedMedlemskap = Regel(
            identifikator = "1.4",
            avklaring = "Er brukers situasjon uendret?",
            beskrivelse = """"
                Er brukers situasjon uendret i forhold til da A1 ble utstedt? Sjekker at det er samme arbeidsforhold
                i dag som på fra og med tidspunktet for perioden gitt fra MEDL
            """.trimIndent(),
            operasjon = { situasjonenErUendret() }
    )


    private fun sjekkPerioderIMedl(): Resultat =
            when {
                personfakta.finnesPersonIMedlSiste12mnd() -> ja()
                else -> nei()
            }


    private fun harPeriodeMedOgUtenMedlemskap(): Resultat  =
            when {
                personfakta.harMedlPeriodeMedOgUtenMedlemskap() -> ja()
                else -> nei()
            }

    private fun situasjonenErUendret(): Resultat =
            when {
                personfakta.harSammeArbeidsforholdSidenFomDatoFraMedl()
                        && personfakta.harSammeAdresseSidenFomDatoFraMedl() -> ja()
                else -> nei()
            }

    //1.1- utvidelse 1 - Har bruker et avklart lovvalg i MEDL?
    private fun periodeMedMedlemskap(): Resultat =
            when {
                personfakta.personensPerioderIMedlSiste12Mnd().stream().anyMatch {
                    it.erMedlem && it.lovvalgsland er "NOR"
                } -> ja()
                else -> nei()
            }


    private fun erMedlemskapPeriodeOver12MndPeriode(finnPeriodeMedMedlemskap: Boolean): Resultat =
            when {
                personfakta.erMedlemskapPeriodeOver12MndPeriode(finnPeriodeMedMedlemskap)
                        && personfakta.harGyldigeMedlemskapsperioder() -> ja()
                else -> nei()
            }

}