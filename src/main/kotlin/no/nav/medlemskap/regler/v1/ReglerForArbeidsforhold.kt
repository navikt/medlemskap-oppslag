package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.domene.Arbeidsforholdstype
import no.nav.medlemskap.domene.Skipsregister
import no.nav.medlemskap.domene.YrkeskoderForLuftFart
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.alleEr
import no.nav.medlemskap.regler.common.Funksjoner.finnes
import no.nav.medlemskap.regler.common.Funksjoner.finnesMindreEnn
import no.nav.medlemskap.regler.common.Funksjoner.inneholderNoe
import no.nav.medlemskap.regler.common.Funksjoner.kunInneholder
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.antallAnsatteHosArbeidsgivere
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.arbeidsforholdForYrkestype
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.erArbeidsgivereOrganisasjon
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.erSammenhengendeIKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.konkursStatuserArbeidsgivere
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.sisteArbeidsforholdSkipsregister
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.sisteArbeidsforholdYrkeskode

class ReglerForArbeidsforhold(val personfakta: Personfakta) : Regler() {

    private val arbeidsforhold: List<Arbeidsforhold> = personfakta.arbeidsforhold
    private val kontrollPeriodeForArbeidsforhold = Datohjelper(personfakta.datagrunnlag.periode).kontrollPeriodeForArbeidsforhold()

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
            operasjon = { sjekkSammenhengendeArbeidsforhold() }
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


    private fun sjekkSammenhengendeArbeidsforhold(): Resultat =
            when {
                !arbeidsforhold.erSammenhengendeIKontrollPeriode(kontrollPeriodeForArbeidsforhold) -> nei("Arbeidstaker har ikke sammenhengende arbeidsforhold siste 12 mnd")
                else -> ja()
                //Denne skal flyttes til senere i løpet og skal modifiseres til å kunne ha flere parallelle arbeidsforhold
                //if (personfakta.arbeidsforholdIOpptjeningsperiode() antallErIkke 1)
                //    return nei("Bruker må ha ett arbeidsforhold i hele opptjeningsperioden")
            }

    private fun sjekkArbeidsgiver(): Resultat =
            when {
                !arbeidsforhold.erArbeidsgivereOrganisasjon(kontrollPeriodeForArbeidsforhold) -> nei("Ikke alle arbeidsgivere er av typen organisasjon")
                else -> ja()
            }


    private fun sjekkOmForetakMerEnn5Ansatte(): Resultat =
            when {
                arbeidsforhold.antallAnsatteHosArbeidsgivere(kontrollPeriodeForArbeidsforhold) finnesMindreEnn 6 -> nei("Ikke alle arbeidsgivere har 6 ansatte eller flere")
                else -> ja()
            }


    private fun sjekkMaritim(): Resultat =
            when {
                arbeidsforhold.arbeidsforholdForYrkestype(kontrollPeriodeForArbeidsforhold) alleEr Arbeidsforholdstype.MARITIM.navn -> ja()
                else -> nei()
            }


    private fun sjekkYrkeskodeLuftfart(): Resultat =
            when {
                arbeidsforhold.sisteArbeidsforholdYrkeskode(kontrollPeriodeForArbeidsforhold) inneholderNoe yrkeskoderLuftfart -> ja()
                else -> nei()
            }


    private fun sjekkSkipsregister(): Resultat =
            when {
                arbeidsforhold.sisteArbeidsforholdSkipsregister(kontrollPeriodeForArbeidsforhold) kunInneholder Skipsregister.NOR.name -> ja()
                else -> nei()
            }


    private fun sjekKonkursstatus(): Resultat =
            when {
                arbeidsforhold.konkursStatuserArbeidsgivere(kontrollPeriodeForArbeidsforhold).finnes() -> nei("Arbeidstaker har hatt arbeidsforhold til arbeidsgiver som har konkurs-status satt")
                else -> ja()
            }
}