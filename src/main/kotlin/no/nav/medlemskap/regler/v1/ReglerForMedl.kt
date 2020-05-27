package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.er

class ReglerForMedl(val personfakta: Personfakta) : Regler() {


    override fun hentHovedRegel(): Regel =
            sjekkRegel {
                harBrukerMedlOpplysninger
            } hvisJa {
                sjekkRegel {
                    periodeMedMedlemskap
                } hvisNei {
                    sjekkRegel {
                        erPeriodeUtenMedlemskapInnenfor12MndPeriode
                    } hvisNei {
                        uavklartKonklusjon
                    } hvisJa {
                        sjekkRegel {
                            erSituasjonenUendret
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
                            erSituasjonenUendret
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


    private val harBrukerMedlOpplysninger = Regel(
            identifikator = "OPPLYSNINGER-MEDL",
            avklaring = "1.1 - Finnes det noe på personen i MEDL?",
            beskrivelse = """
                Vedtak (gjort av NAV eller utenlandsk trygdemyndighet) som er registrert i MEDL, 
                må vurderes manuelt og det må vurderes om brukers situasjon er uendret i forhold 
                til situasjonen på vedtakstidspunktet.
            """.trimIndent(),
            operasjon = { sjekkPerioderIMedl() }
    )

    private val periodeMedMedlemskap = Regel(
            identifikator = "MEDL-1.1.1",
            avklaring = "1.1.1 - Er det en periode med medlemskap?",
            beskrivelse = """"
               Har medlemskap og Norge er lovvalg. 
            """.trimIndent(),
            operasjon = { periodeMedMedlemskap() }
    )

    private val erPeriodeUtenMedlemskapInnenfor12MndPeriode = Regel(
            identifikator = "MEDL-1.1.4",
            avklaring = "Er hele perioden uten medlemskap innenfor 12-måneders perioden?",
            beskrivelse = """"
               Er hele perioden uten medlemskap innenfor 12-månedersperioden?
            """.trimIndent(),
            operasjon = { erMedlemskapPeriodeInnenforOpptjeningsperiode(false) }
    )

    private val erPeriodeMedMedlemskapInnenfor12MndPeriode = Regel(
            identifikator = "MEDL-1.1.2",
            avklaring = "Er hele perioden med medlemskap innenfor 12-måneders perioden?",
            beskrivelse = """"
               Er hele perioden med medlemskap innenfor 12-månedersperioden?
            """.trimIndent(),
            operasjon = { erMedlemskapPeriodeInnenforOpptjeningsperiode(true) }
    )


    private val erSituasjonenUendret = Regel(
            identifikator = "MEDL-1.1.4.1",
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


    //TODO MÅ IMPLEMENTERES
    private fun situasjonenErUendret(): Resultat =
            when {
                personfakta.harSammeArbeidsforholdSidenFomDatoFraMedl() -> ja()
                else -> nei()
            }

    //1.1- utvidelse 1 - Har bruker et avklart lovvalg i MEDL?
    private fun periodeMedMedlemskap(): Resultat =
            when {
                personfakta.personensPerioderIMedlSiste12Mnd().stream().anyMatch() {
                    it.erMedlem && it.lovvalg er "NOR"
                } -> ja()
                else -> nei()
            }


    private fun erMedlemskapPeriodeInnenforOpptjeningsperiode(finnPeriodeMedMedlemskap: Boolean): Resultat =
            when {
                personfakta.erMedlemskapPeriodeInnenforOpptjeningsperiode(finnPeriodeMedMedlemskap) -> ja()
                else -> nei()
            }

}