package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Arbeidsforholdstype
import no.nav.medlemskap.domene.Skipsregister
import no.nav.medlemskap.regler.common.Avklaring
import no.nav.medlemskap.regler.common.Fakta
import no.nav.medlemskap.regler.common.Funksjoner.erDelAv
import no.nav.medlemskap.regler.common.Regelsett
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.uttrykk.HvisUttrykk.Companion.hvis

class RegelsettForNorskLovvalg(fakta: Fakta) : Regelsett("Regelsett for norsk lovvalg", fakta) {

    override val KONKLUSJON_IDENTIFIKATOR: String get() = "LOV"
    override val KONKLUSJON_AVKLARING: String get() = "Er personen omfattet av norsk lovvalg?"

    override fun evaluer(): Resultat {
        val resultat =
                avklar {
                    erArbeidsgiverNorsk evaluerMed fakta
                } hvisJa {
                    avklar {
                        erArbeidsforholdetMaritimt evaluerMed fakta
                    } hvisJa {
                        avklar {
                            jobberPersonenPåEtNorskregistrertSkip evaluerMed fakta
                        } hvisJa {
                            avklar {
                                harBrukerJobbetUtenforNorge evaluerMed fakta
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
                            erPersonenPilotEllerKabinansatt evaluerMed fakta
                        } hvisNei {
                            avklar {
                                harBrukerJobbetUtenforNorge evaluerMed fakta
                            } hvisNei {
                                konkluderMed(ja("Personen er omfattet av norsk lovvalg"))
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

        return hentUtKonklusjon(resultat)
    }

    private val yrkeskoderLuftfart = listOf("3143107", "5111105", "5111117")

    private val norskeSkipsregister = listOf(Skipsregister.nor)

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

    private fun sjekkArbeidsgiver(fakta: Fakta): Resultat =
            hvis {
                fakta.sisteArbeidsgiversLand() == "NOR"
            } så {
                ja("Arbeidsgiver er norsk")
            } ellers {
                nei("Arbeidsgiver er ikke norsk. Land: ${fakta.sisteArbeidsgiversLand()}")
            }


    private fun sjekkMaritim(fakta: Fakta): Resultat =
            hvis {
                fakta.sisteArbeidsforholdtype() == Arbeidsforholdstype.MARITIM
            } så {
                ja("Personen er ansatt i det maritime")
            } ellers {
                nei("Personen jobber ikke i det maritime")
            }


    private fun sjekkYrkeskodeLuftfart(fakta: Fakta): Resultat =
            hvis {
                fakta.sisteArbeidsforholdYrkeskode() erDelAv yrkeskoderLuftfart
            } så {
                ja("Personen er pilot eller kabinansatt")
            } ellers {
                nei("Personen er ikke pilot eller kabinansatt")
            }

    private fun sjekkSkipsregister(fakta: Fakta): Resultat =
            hvis {
                fakta.sisteArbeidsforholdSkipsregister() erDelAv norskeSkipsregister
            } så {
                ja("Personen jobber på et norskregistrert skip")
            } ellers {
                nei("Personen jobber ikke på et norskregistrert skip")
            }

    private fun sjekkOmBrukerHarJobbetUtenforNorge(fakta: Fakta): Resultat =
            hvis {
                fakta.hentBrukerinputArbeidUtenforNorge()
            } så {
                ja("Bruker har oppgitt å ha jobbet utenfor Norge")
            } ellers {
                nei("Bruker har ikke jobbet utenfor Norge")
            }


}
