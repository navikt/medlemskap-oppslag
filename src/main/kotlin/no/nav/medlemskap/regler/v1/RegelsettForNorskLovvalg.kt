package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Arbeidsforholdstype
import no.nav.medlemskap.domene.Skipsregister
import no.nav.medlemskap.regler.common.Avklaring
import no.nav.medlemskap.regler.common.Funksjoner.erDelAv
import no.nav.medlemskap.regler.common.Funksjoner.erMerEnn
import no.nav.medlemskap.regler.common.Funksjoner.inneholderNoe
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.Funksjoner.kunInneholder
import no.nav.medlemskap.regler.common.Funksjoner.inneholder
import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.common.Regelsett
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.uttrykk.HvisUttrykk.Companion.hvis

class RegelsettForNorskLovvalg : Regelsett("Regelsett for norsk lovvalg") {

    override val KONKLUSJON_IDENTIFIKATOR: String get() = "LOV"
    override val KONKLUSJON_AVKLARING: String get() = "Er personen omfattet av norsk lovvalg?"

    override fun evaluer(personfakta: Personfakta): Resultat {
        val resultat =
                avklar {
                    personfakta oppfyller harArbeidsforhold
                } hvisJa {
                    avklar {
                        personfakta oppfyller erArbeidsgiverNorsk
                    } hvisJa {
                        avklar {
                            personfakta oppfyller erArbeidsforholdetMaritimt
                        } hvisJa {
                            avklar {
                                personfakta oppfyller jobberPersonenPåEtNorskregistrertSkip
                            } hvisJa {
                                avklar {
                                    personfakta oppfyller harBrukerJobbetUtenforNorge
                                } hvisNei {
                                    konkluderMed(ja("Personen er omfattet av norsk lovvalg"))
                                } hvisJa {
                                    konkluderMed(uavklart("Bruker har jobbet utenfor Norge"))
                                }
                            } hvisNei {
                                konkluderMed(uavklart("Bruker jobber ikke på et norskregistrert skip"))
                            }
                        } hvisNei {
                            avklar {
                                personfakta oppfyller erPersonenPilotEllerKabinansatt
                            } hvisNei {
                                avklar {
                                    personfakta oppfyller harBrukerJobbetUtenforNorge
                                } hvisNei {
                                    avklar {
                                        personfakta oppfyller harBrukerJobbbetMerEnn25Prosent
                                    } hvisJa {
                                        konkluderMed(ja("Personen har jobbet mer enn 25 prosent"))
                                    } hvisNei{
                                        konkluderMed(uavklart("Personen har ikke hatt stor nok stillingsprosent"))
                                    }
                                } hvisJa {
                                    konkluderMed(uavklart("Bruker har jobbet utenfor Norge"))
                                }
                            } hvisJa {
                                konkluderMed(uavklart("Personen er pilot eller kabinansatt"))
                            }
                        }
                    } hvisNei {
                        konkluderMed(uavklart("Kan ikke konkludere på arbeidsgiver"))
                    }
                } hvisNei {
                    konkluderMed(uavklart("Kan ikke konkludere på arbeidsgiver"))
                }

        return hentUtKonklusjon(resultat)
    }

    private val yrkeskoderLuftfart = listOf("3143107", "5111105", "5111117")

    private val norskeSkipsregister = listOf(Skipsregister.nor.name)

    private val erArbeidsgiverNorsk = Avklaring(
            identifikator = "LOV-1",
            avklaring = "Jobber personen for en norsk arbeidsgiver?",
            beskrivelse = "",
            operasjon = { sjekkArbeidsgiver(it) }
    )

    private val erArbeidsforholdetMaritimt = Avklaring(
            identifikator = "LOV-2",
            avklaring = "Sjekk om personen jobber i det maritime",
            beskrivelse = "",
            operasjon = { sjekkMaritim(it) }
    )

    private val erPersonenPilotEllerKabinansatt = Avklaring(
            identifikator = "LOV-3",
            avklaring = "Sjekk om personen er pilot eller kabinansatt",
            beskrivelse = "",
            operasjon = { sjekkYrkeskodeLuftfart(it) }
    )

    private val jobberPersonenPåEtNorskregistrertSkip = Avklaring(
            identifikator = "LOV-4",
            avklaring = "Sjekk om personen jobber på et norskregistrert skip",
            beskrivelse = "",
            operasjon = { sjekkSkipsregister(it) }
    )

    private val harBrukerJobbetUtenforNorge = Avklaring(
            identifikator = "LOV-5",
            avklaring = "Sjekk om personen har oppgitt å ha jobbet utenfor Norge",
            beskrivelse = "",
            operasjon = { sjekkOmBrukerHarJobbetUtenforNorge(it) }
    )

    private val harArbeidsforhold = Avklaring(
            identifikator = "LOV-6",
            avklaring = "Har personen et registrert arbeidsforhold",
            beskrivelse = "",
            operasjon = { sjekkArbeidsforhold(it) }
    )

    private val harBrukerJobbbetMerEnn25Prosent= Avklaring(
            identifikator = "LOV-6",
            avklaring = "Har personen jobbet mer enn 25%",
            beskrivelse = "",
            operasjon = { sjekkOmBrukerHarJobbetMerEnn25Prosent(it) }
    )

    private fun sjekkArbeidsforhold(personfakta: Personfakta): Resultat =
            hvis {
                personfakta.arbeidsforhold().erIkkeTom()
            } så {
                ja("Personen har et registrert arbeidsforhold")
            } ellers {
                nei("Personen har ikke et registrert arbeidsforhold")
            }

    private fun sjekkArbeidsgiver(personfakta: Personfakta): Resultat =
            hvis {
                personfakta.arbeidsgiversLandForPeriode() kunInneholder "NOR"
            } så {
                ja("Arbeidsgiver er norsk")
            } ellers {
                nei("Arbeidsgiver er ikke norsk. Land: ${personfakta.arbeidsgiversLandForPeriode()}")
            }


    private fun sjekkMaritim(personfakta: Personfakta): Resultat =
            hvis {
                personfakta.sisteArbeidsforholdtype() inneholder Arbeidsforholdstype.MARITIM.navn
            } så {
                ja("Personen er ansatt i det maritime")
            } ellers {
                nei("Personen jobber ikke i det maritime")
            }


    private fun sjekkYrkeskodeLuftfart(personfakta: Personfakta): Resultat =
            hvis {
                personfakta.sisteArbeidsforholdYrkeskode() inneholderNoe yrkeskoderLuftfart
            } så {
                ja("Personen er pilot eller kabinansatt")
            } ellers {
                nei("Personen er ikke pilot eller kabinansatt")
            }

    private fun sjekkSkipsregister(personfakta: Personfakta): Resultat =
            hvis {
                personfakta.sisteArbeidsforholdSkipsregister() kunInneholder Skipsregister.nor.name
            } så {
                ja("Personen jobber på et norskregistrert skip")
            } ellers {
                nei("Personen jobber ikke på et norskregistrert skip")
            }

    private fun sjekkOmBrukerHarJobbetUtenforNorge(personfakta: Personfakta): Resultat =
            hvis {
                personfakta.hentBrukerinputArbeidUtenforNorge()
            } så {
                ja("Bruker har oppgitt å ha jobbet utenfor Norge")
            } ellers {
                nei("Bruker har ikke jobbet utenfor Norge")
            }


    private fun sjekkOmBrukerHarJobbetMerEnn25Prosent(personfakta: Personfakta): Resultat =
            hvis{
                personfakta.hentTotalStillingprosenter() erMerEnn 25.0
            } så {
                ja("Bruker har jobbet mer enn 25 %")
            } ellers {
                nei("Bruker har ikke jobbet mer en 25 %")
            }

}
