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
                harArbeidsforhold
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

    private val harArbeidsforhold = Regel(
            identifikator = "ARB-1",
            avklaring = "Har personen et registrert arbeidsforhold",
            beskrivelse = "",
            operasjon = { sjekkArbeidsforhold() }
    )

    private val erArbeidsgiverNorsk = Regel(
            identifikator = "ARB-2",
            avklaring = "Jobber personen for en norsk arbeidsgiver?",
            beskrivelse = "",
            operasjon = { sjekkArbeidsgiver() }
    )

    private val erBrukerPilotEllerKabinansatt = Regel(
            identifikator = "ARB-3",
            avklaring = "Sjekk om personen er pilot eller kabinansatt",
            beskrivelse = "",
            operasjon = { sjekkYrkeskodeLuftfart() }
    )

    private val erArbeidsforholdetMaritimt = Regel(
            identifikator = "ARB-4",
            avklaring = "Sjekk om personen jobber i det maritime",
            beskrivelse = "",
            operasjon = { sjekkMaritim() }
    )

    private val jobberBrukerPåNorskSkip = Regel(
            identifikator = "ARB-5",
            avklaring = "Sjekk om personen jobber på et norskregistrert skip",
            beskrivelse = "",
            operasjon = { sjekkSkipsregister() }
    )

    private val yrkeskoderLuftfart = listOf("3143107", "5111105", "5111117")

    private fun sjekkArbeidsforhold(): Resultat =
            when {
                personfakta.arbeidsforhold().erIkkeTom() -> ja("Personen har et registrert arbeidsforhold")
                else -> nei("Personen har ikke et registrert arbeidsforhold")
            }

    private fun sjekkArbeidsgiver(): Resultat =
            when {
                personfakta.arbeidsgiversLandForPeriode() kunInneholder "NOR" -> ja("Arbeidsgiver er norsk")
                else -> nei("Arbeidsgiver er ikke norsk. Land: ${personfakta.arbeidsgiversLandForPeriode()}")
            }

    private fun sjekkMaritim(): Resultat =
            when {
                personfakta.sisteArbeidsforholdtype() inneholder Arbeidsforholdstype.MARITIM.navn -> ja("Personen er ansatt i det maritime")
                else -> nei("Personen jobber ikke i det maritime")
            }


    private fun sjekkYrkeskodeLuftfart(): Resultat =
            when {
                personfakta.sisteArbeidsforholdYrkeskode() inneholderNoe yrkeskoderLuftfart -> ja("Personen er pilot eller kabinansatt")
                else -> nei("Personen er ikke pilot eller kabinansatt")
            }

    private fun sjekkSkipsregister(): Resultat =
            when {
                personfakta.sisteArbeidsforholdSkipsregister() kunInneholder Skipsregister.nor.name -> ja("Personen jobber på et norskregistrert skip")
                else -> nei("Personen jobber ikke på et norskregistrert skip")
            }

}
