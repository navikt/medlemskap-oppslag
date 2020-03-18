package no.nav.medlemskap.regler.v2.regler

import no.nav.medlemskap.domene.Arbeidsforholdstype
import no.nav.medlemskap.domene.Skipsregister
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.Funksjoner.inneholder
import no.nav.medlemskap.regler.common.Funksjoner.inneholderNoe
import no.nav.medlemskap.regler.common.Funksjoner.kunInneholder
import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.v2.common.*

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
                personfakta.arbeidsforhold().erIkkeTom() -> ja()
                else -> nei()
            }

    private fun sjekkArbeidsgiver(): Resultat =
            when {
                personfakta.arbeidsgiversLandForPeriode() kunInneholder "NOR" -> ja()
                else -> nei("Arbeidsgiver er ikke norsk. Land: ${personfakta.arbeidsgiversLandForPeriode()}")
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
