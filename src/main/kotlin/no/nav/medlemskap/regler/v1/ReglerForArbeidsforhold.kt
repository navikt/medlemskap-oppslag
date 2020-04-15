package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Arbeidsforholdstype
import no.nav.medlemskap.domene.Skipsregister
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.finnes
import no.nav.medlemskap.regler.common.Funksjoner.finnesFærreEnn
import no.nav.medlemskap.regler.common.Funksjoner.inneholder
import no.nav.medlemskap.regler.common.Funksjoner.inneholderNoe
import no.nav.medlemskap.regler.common.Funksjoner.kunEr
import no.nav.medlemskap.regler.common.Funksjoner.kunInneholder

class ReglerForArbeidsforhold(val personfakta: Personfakta) : Regler() {

    override fun hentHovedRegel() =
            sjekkRegel {
                harBrukerEtArbeidsforhold
            } hvisNei {
                uavklartKonklusjon
            } hvisJa {
                sjekkRegel {
                    erArbeidsgiverNorsk
                } hvisNei {
                    uavklartKonklusjon
                } hvisJa {
                    sjekkRegel {
                        erBrukerPilotEllerKabinansatt
                    } hvisJa {
                        uavklartKonklusjon
                    } hvisNei {
                        sjekkRegel {
                            erArbeidsforholdetMaritimt
                        } hvisNei {
                            sjekkRegelsett {
                                reglerForLovvalg
                            }
                        } hvisJa {
                            sjekkRegel {
                                jobberBrukerPåNorskSkip
                            } hvisNei {
                                uavklartKonklusjon
                            } hvisJa {
                                sjekkRegelsett {
                                    reglerForLovvalg
                                }
                            }
                        }
                    }
                }
            }

    private val reglerForLovvalg = ReglerForLovvalg(personfakta)

    private val harBrukerEtArbeidsforhold = Regel(
            identifikator = "ARB-1",
            avklaring = "Har bruker et registrert arbeidsforhold?",
            beskrivelse = "",
            operasjon = { sjekkArbeidsforhold() }
    )

    private val erArbeidsgiverNorsk = Regel(
            identifikator = "ARB-2",
            avklaring = "Jobber bruker for en norsk arbeidsgiver?",
            beskrivelse = "",
            operasjon = { sjekkArbeidsgiver() }
    )

    private val erBrukerPilotEllerKabinansatt = Regel(
            identifikator = "ARB-3",
            avklaring = "Er bruker pilot eller kabinansatt?",
            beskrivelse = "",
            operasjon = { sjekkYrkeskodeLuftfart() }
    )

    private val erArbeidsforholdetMaritimt = Regel(
            identifikator = "ARB-4",
            avklaring = "Har bruker et maritimt arbeidsforhold?",
            beskrivelse = "",
            operasjon = { sjekkMaritim() }
    )

    private val jobberBrukerPåNorskSkip = Regel(
            identifikator = "ARB-5",
            avklaring = "Jobber bruker på et norskregistrert skip?",
            beskrivelse = "",
            operasjon = { sjekkSkipsregister() }
    )

    private val yrkeskoderLuftfart = listOf("3143107", "5111105", "5111117")

    private fun sjekkArbeidsforhold(): Resultat =
            when {
                personfakta.arbeidsforhold() kunEr 1 -> ja()
                else -> nei()
            }

    private fun sjekkArbeidsgiver(): Resultat {

        if (!personfakta.erArbeidsgivereOrganisasjon())
            return nei("Ikke alle arbeidsgivere er av typen organisasjon")

        if (personfakta.antallAnsatteHosArbeidsgivere() finnesFærreEnn 6)
            return nei("Ikke alle arbeidsgivere har 6 ansatte eller flere")

        if (!personfakta.harSammenhengendeArbeidsforholdSiste12Mnd())
            return nei("Arbeidstaker har ikke sammenhengende arbeidsforhold siste 12 mnd")

        if (personfakta.konkursStatuserArbeidsgivere().finnes())
            return nei("Arbeidstaker har hatt arbeidsforhold til arbeidsgiver som har konkurs-status satt")

        return ja()
    }

    private fun sjekkMaritim(): Resultat =
            when {
                personfakta.sisteArbeidsforholdtype() inneholder Arbeidsforholdstype.MARITIM.navn -> ja()
                else -> nei()
            }


    private fun sjekkYrkeskodeLuftfart(): Resultat =
            when {
                personfakta.sisteArbeidsforholdYrkeskode() inneholderNoe yrkeskoderLuftfart -> ja()
                else -> nei()
            }


    private fun sjekkSkipsregister(): Resultat =
            when {
                personfakta.sisteArbeidsforholdSkipsregister() kunInneholder Skipsregister.nor.name -> ja()
                else -> nei()
            }
}
