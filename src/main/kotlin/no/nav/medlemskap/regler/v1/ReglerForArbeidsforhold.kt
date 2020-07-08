package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.alleEr
import no.nav.medlemskap.regler.common.Funksjoner.finnes
import no.nav.medlemskap.regler.common.Funksjoner.finnesMindreEnn
import no.nav.medlemskap.regler.common.Funksjoner.inneholderNoe
import no.nav.medlemskap.regler.common.Funksjoner.kunInneholder
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.antallAnsatteHosArbeidsgivere
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.arbeidsforholdForYrkestype
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.erAlleArbeidsgivereOrganisasjon
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.erSammenhengendeIKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.konkursStatuserArbeidsgivere
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.sisteArbeidsforholdSkipsregister
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.sisteArbeidsforholdYrkeskode

class ReglerForArbeidsforhold(
        val arbeidsforhold: List<Arbeidsforhold>,
        val periode: InputPeriode,
        val ytelse: Ytelse,
        val reglerForLovvalg: ReglerForLovvalg
) : Regler() {
    private val kontrollPeriodeForArbeidsforhold = Datohjelper(periode, ytelse).kontrollPeriodeForArbeidsforhold()

    override fun hentHovedRegel() =
            sjekkRegel {
                harBrukerSammenhengendeArbeidsforholdSiste12Mnd
            } hvisNei {
                uavklartKonklusjon(ytelse)
            } hvisJa {
                sjekkRegel {
                    erArbeidsgiverOrganisasjon
                } hvisNei {
                    uavklartKonklusjon(ytelse)
                } hvisJa {
                    sjekkRegel {
                        harForetakMerEnn5Ansatte
                    } hvisNei {
                        uavklartKonklusjon(ytelse)
                    } hvisJa {
                        sjekkRegel {
                            erForetakAktivt
                        } hvisNei {
                            uavklartKonklusjon(ytelse)
                        } hvisJa {
                            sjekkRegel {
                                erArbeidsforholdetMaritimt
                            } hvisNei {
                                sjekkRegel {
                                    erBrukerPilotEllerKabinansatt
                                } hvisJa {
                                    uavklartKonklusjon(ytelse)
                                } hvisNei {
                                    sjekkRegelsett {
                                        reglerForLovvalg
                                    }
                                }
                            } hvisJa {
                                sjekkRegel {
                                    jobberBrukerPaaNorskSkip
                                } hvisNei {
                                    uavklartKonklusjon(ytelse)
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

    private val harBrukerSammenhengendeArbeidsforholdSiste12Mnd = Regel(
            regelId = REGEL_3,
            ytelse = ytelse,
            operasjon = { sjekkSammenhengendeArbeidsforhold() }
    )

    private val erArbeidsgiverOrganisasjon = Regel(
            regelId = REGEL_4,
            ytelse = ytelse,
            operasjon = { sjekkArbeidsgiver() }
    )

    private val harForetakMerEnn5Ansatte = Regel(
            regelId = REGEL_5,
            ytelse = ytelse,
            operasjon = { sjekkOmForetakMerEnn5Ansatte() }
    )

    private val erForetakAktivt = Regel(
            regelId = REGEL_6,
            ytelse = ytelse,
            operasjon = { sjekKonkursstatus() }
    )

    private val erArbeidsforholdetMaritimt = Regel(
            regelId = REGEL_7,
            ytelse = ytelse,
            operasjon = { sjekkMaritim() }
    )

    private val jobberBrukerPaaNorskSkip = Regel(
            regelId = REGEL_7_1,
            ytelse = ytelse,
            operasjon = { sjekkSkipsregister() }
    )

    private val erBrukerPilotEllerKabinansatt = Regel(
            regelId = REGEL_8,
            ytelse = ytelse,
            operasjon = { sjekkYrkeskodeLuftfart() }
    )

    private val yrkeskoderLuftfart = YrkeskoderForLuftFart.values().map { it.styrk }


    private fun sjekkSammenhengendeArbeidsforhold(): Resultat =
            when {
                !arbeidsforhold.erSammenhengendeIKontrollPeriode(kontrollPeriodeForArbeidsforhold, ytelse) -> nei("Arbeidstaker har ikke sammenhengende arbeidsforhold siste 12 mnd")
                else -> ja()
                //Denne skal flyttes til senere i løpet og skal modifiseres til å kunne ha flere parallelle arbeidsforhold
                //if (personfakta.arbeidsforholdIOpptjeningsperiode() antallErIkke 1)
                //    return nei("Bruker må ha ett arbeidsforhold i hele opptjeningsperioden")
            }

    private fun sjekkArbeidsgiver(): Resultat =
            when {
                !arbeidsforhold.erAlleArbeidsgivereOrganisasjon(kontrollPeriodeForArbeidsforhold) -> nei("Ikke alle arbeidsgivere er av typen organisasjon")
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
                arbeidsforhold sisteArbeidsforholdYrkeskode kontrollPeriodeForArbeidsforhold inneholderNoe yrkeskoderLuftfart -> ja()
                else -> nei()
            }


    private fun sjekkSkipsregister(): Resultat =
            when {
                arbeidsforhold sisteArbeidsforholdSkipsregister kontrollPeriodeForArbeidsforhold kunInneholder Skipsregister.NOR.name -> ja()
                else -> nei()
            }


    private fun sjekKonkursstatus(): Resultat =
            when {
                arbeidsforhold.konkursStatuserArbeidsgivere(kontrollPeriodeForArbeidsforhold).finnes() -> nei("Arbeidstaker har hatt arbeidsforhold til arbeidsgiver som har konkurs-status satt")
                else -> ja()
            }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForArbeidsforhold {
            val reglerForLovvalg = ReglerForLovvalg.fraDatagrunnlag(datagrunnlag)

            return ReglerForArbeidsforhold(datagrunnlag.arbeidsforhold, datagrunnlag.periode, datagrunnlag.ytelse, reglerForLovvalg)
        }
    }
}