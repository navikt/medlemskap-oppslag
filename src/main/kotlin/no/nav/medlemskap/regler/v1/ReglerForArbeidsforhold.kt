package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Arbeidsforholdstype
import no.nav.medlemskap.domene.Skipsregister
import no.nav.medlemskap.domene.YrkeskoderForLuftFart
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.alleEr
import no.nav.medlemskap.regler.common.Funksjoner.finnes
import no.nav.medlemskap.regler.common.Funksjoner.finnesMindreEnn
import no.nav.medlemskap.regler.common.Funksjoner.inneholderNoe
import no.nav.medlemskap.regler.common.Funksjoner.kunInneholder

class ReglerForArbeidsforhold(val personfakta: Personfakta) : Regler() {

    override fun hentHovedRegel() =
            sjekkRegel {
                harBrukerSammenhengendeArbeidsforholdSiste12Mnd
            } hvisNei {
                uavklartKonklusjon
            } hvisJa {
                sjekkRegel {
                    erArbeidsgiverOrganisasjon
                } hvisNei {
                    uavklartKonklusjon
                } hvisJa {
                    sjekkRegel {
                        harForetakMerEnn5Ansatte
                    } hvisNei {
                        uavklartKonklusjon
                    } hvisJa {
                        sjekkRegel {
                            erForetakAktivt
                        } hvisNei {
                            uavklartKonklusjon
                        } hvisJa {
                            sjekkRegel {
                                erArbeidsforholdetMaritimt
                            } hvisNei {
                                sjekkRegel {
                                    erBrukerPilotEllerKabinansatt
                                } hvisJa {
                                    uavklartKonklusjon
                                } hvisNei {
                                    sjekkRegelsett {
                                        reglerForLovvalg
                                    }
                                }
                            } hvisJa {
                                sjekkRegel {
                                    jobberBrukerPaaNorskSkip
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
            }

    private val reglerForLovvalg = ReglerForLovvalg(personfakta)

    private val harBrukerSammenhengendeArbeidsforholdSiste12Mnd = Regel(
            identifikator = "3",
            avklaring = "Har bruker hatt et sammenhengende arbeidsforhold i Aa-registeret de siste 12 månedene?",
            beskrivelse = "",
            operasjon = { sjekkArbeidsforhold() }
    )

    private val erArbeidsgiverOrganisasjon = Regel(
            identifikator = "4",
            avklaring = "Er foretaket registrert i foretaksregisteret?",
            beskrivelse = "",
            operasjon = { sjekkArbeidsgiver() }
    )

    private val harForetakMerEnn5Ansatte = Regel(
            identifikator = "5",
            avklaring = "Har arbeidsgiver sin hovedaktivitet i Norge?",
            beskrivelse = "",
            operasjon = { sjekkOmForetakMerEnn5Ansatte() }
    )

    private val erForetakAktivt = Regel(
            identifikator = "6",
            avklaring = "Er foretaket aktivt?",
            beskrivelse = "",
            operasjon = { sjekKonkursstatus() }
    )

    private val erArbeidsforholdetMaritimt = Regel(
            identifikator = "7",
            avklaring = "Er arbeidsforholdet maritimt?",
            beskrivelse = "",
            operasjon = { sjekkMaritim() }
    )

    private val jobberBrukerPaaNorskSkip = Regel(
            identifikator = "7.1",
            avklaring = "Er bruker ansatt på et NOR-skip?",
            beskrivelse = "",
            operasjon = { sjekkSkipsregister() }
    )

    private val erBrukerPilotEllerKabinansatt = Regel(
            identifikator = "8",
            avklaring = "Er bruker pilot eller kabinansatt?",
            beskrivelse = "",
            operasjon = { sjekkYrkeskodeLuftfart() }
    )

    private val yrkeskoderLuftfart = YrkeskoderForLuftFart.values().map { it.styrk }

    private fun sjekkArbeidsforhold(): Resultat {

        if (!personfakta.harSammenhengendeArbeidsforholdSiste12Mnd())
            return nei("Arbeidstaker har ikke sammenhengende arbeidsforhold siste 12 mnd")

        //Denne skal flyttes til senere i løpet og skal modifiseres til å kunne ha flere parallelle arbeidsforhold
        //if (personfakta.arbeidsforholdIOpptjeningsperiode() antallErIkke 1)
        //    return nei("Bruker må ha ett arbeidsforhold i hele opptjeningsperioden")

        return ja()
    }

    private fun sjekkArbeidsgiver(): Resultat {

        if (!personfakta.erArbeidsgivereOrganisasjon())
            return nei("Ikke alle arbeidsgivere er av typen organisasjon")

        return ja()
    }


    private fun sjekkOmForetakMerEnn5Ansatte(): Resultat {
        if (personfakta.antallAnsatteHosArbeidsgivere() finnesMindreEnn 6)
            return nei("Ikke alle arbeidsgivere har 6 ansatte eller flere")

        return ja()
    }


    private fun sjekkMaritim(): Resultat =
            when {
                personfakta.arbeidsforholdForYrkestype() alleEr Arbeidsforholdstype.MARITIM.navn -> ja()
                else -> nei()
            }


    private fun sjekkYrkeskodeLuftfart(): Resultat =
            when {
                personfakta.sisteArbeidsforholdYrkeskode() inneholderNoe yrkeskoderLuftfart -> ja()
                else -> nei()
            }


    private fun sjekkSkipsregister(): Resultat =
            when {
                personfakta.sisteArbeidsforholdSkipsregister() kunInneholder Skipsregister.NOR.name -> ja()
                else -> nei()
            }


    private fun sjekKonkursstatus(): Resultat {
        if (personfakta.konkursStatuserArbeidsgivere().finnes())
            return nei("Arbeidstaker har hatt arbeidsforhold til arbeidsgiver som har konkurs-status satt")

        return ja()
    }
}
