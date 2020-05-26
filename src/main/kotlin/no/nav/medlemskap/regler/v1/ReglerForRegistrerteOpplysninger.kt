package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Journalpost
import no.nav.medlemskap.domene.Oppgave
import no.nav.medlemskap.domene.Status
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.er
import no.nav.medlemskap.regler.common.Funksjoner.erDelAv

class ReglerForRegistrerteOpplysninger(val personfakta: Personfakta) : Regler() {

    override fun hentHovedRegel() =
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
            } hvisNei {
                harBrukerRegistrerteOpplysninger //Todo hvordan løse ja her
            }

    private val harBrukerRegistrerteOpplysninger = Regel(
            identifikator = "OPPLYSNINGER",
            avklaring = "Finnes det registrerte opplysninger på bruker?",
            beskrivelse = "For å sjekke uregistrerte lovvalg og/eller medlemskap",
            operasjon = { minstEnAvDisse(joark, gsak) }
    )

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

    private val joark = Regel(
            identifikator = "OPPLYSNINGER-JOARK",
            avklaring = "Finnes det dokumenter i JOARK på medlemskapsområdet?",
            beskrivelse = """
                Skal sikre at ubehandlede saker og ikke-registrerte vedtak fanges opp for å bli 
                vurdert manuelt. MEDL er ikke en komplett oversikt over alle medlemsavklaringene 
                som NAV har gjort. 
            """.trimIndent(),
            operasjon = { tellDokumenter() }
    )

    private val gsak = Regel(
            identifikator = "OPPLYSNINGER-GOSYS",
            avklaring = "Finnes det åpne oppgaver i GOSYS på medlemskapsområdet?",
            beskrivelse = """"
                Skal sikre at ubehandlede saker og ikke-registrerte vedtak fanges opp for å bli 
                vurdert manuelt. MEDL er ikke en komplett oversikt over alle medlemsavklaringene 
                som NAV har gjort. 
            """.trimIndent(),
            operasjon = { tellÅpneOppgaver() }
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

    private val erPeriodeMedMedlemskapInnenfor12MndPeriode = Regel (
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


    private val tillatteTemaer = listOf("MED", "UFM", "TRY")
    private val tillatteStatuser = listOf(Status.AAPNET, Status.OPPRETTET, Status.UNDER_BEHANDLING)

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


    private fun erMedlemskapPeriodeInnenforOpptjeningsperiode(finnPeriodeMedMedlemskap:Boolean): Resultat =
            when {
                personfakta.erMedlemskapPeriodeInnenforOpptjeningsperiode(finnPeriodeMedMedlemskap) -> ja()
                else -> nei()
            }

    private fun tellDokumenter(): Resultat =
            when {
                antallDokumenter(personfakta.personensDokumenterIJoark()) > 0 -> ja()
                else -> nei()
            }


    private fun tellÅpneOppgaver(): Resultat =
            when {
                antallÅpneOppgaver(personfakta.personensOppgaverIGsak()) > 0 -> ja()
                else -> nei()
            }


    private fun antallDokumenter(liste: List<Journalpost>) =
            liste.count { journalpost ->
                journalpost.tema erDelAv tillatteTemaer
            }

    private fun antallÅpneOppgaver(liste: List<Oppgave>) =
            liste.count { oppgave ->
                oppgave.tema erDelAv tillatteTemaer && oppgave.status erDelAv tillatteStatuser
            }

}