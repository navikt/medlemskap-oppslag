package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.common.dekningCounter
import no.nav.medlemskap.common.regelUendretCounterMidlertidig
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Dekning.Companion.gjelderForYtelse
import no.nav.medlemskap.domene.Dekning.Companion.uavklartForYtelse
import no.nav.medlemskap.domene.Ytelse.Companion.metricName
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.arbeidsforholdForDato
import no.nav.medlemskap.regler.funksjoner.GsakFunksjoner.finnesAapneOppgaver
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.erMedlemskapsperioderOver12Mnd
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.finnesPersonIMedlForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.finnesUavklartePerioder
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.gjeldendeDekning
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.harGyldigeMedlemskapsperioder
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.harMedlPeriodeMedOgUtenMedlemskap
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.harPeriodeMedMedlemskap
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.tidligsteFraOgMedDatoForMedl

class ReglerForMedl(
        val medlemskap: List<Medlemskap>,
        val arbeidsforhold: List<Arbeidsforhold>,
        val periode: InputPeriode,
        val ytelse: Ytelse,
        val oppgaver: List<Oppgave>,
        val bostedsadresser: List<Adresse>,
        val postadresser: List<Adresse>,
        val midlertidigAdresser: List<Adresse>
) : Regler() {

    private val kontrollPeriodeForMedl = Datohjelper(periode, ytelse).kontrollPeriodeForMedl()
    private val datohjelper = Datohjelper(periode, ytelse)

    override fun hentHovedRegel(): Regel =

            sjekkRegel {
                erPerioderAvklart
            } hvisNei {
                uavklartKonklusjon(ytelse)
            } hvisJa {
                sjekkRegel {
                    periodeMedOgUtenMedlemskap
                } hvisJa {
                    uavklartKonklusjon(ytelse)
                } hvisNei {
                    sjekkRegel {
                        periodeMedMedlemskap
                    } hvisNei {
                        sjekkRegel {
                            erPeriodeUtenMedlemskapInnenfor12MndPeriode
                        } hvisNei {
                            uavklartKonklusjon(ytelse)
                        } hvisJa {
                            sjekkRegel {
                                erArbeidsforholdUendretForBrukerUtenMedlemskap
                            } hvisJa {
                                uavklartKonklusjon(ytelse)
                            } hvisNei {
                                uavklartKonklusjon(ytelse)
                            }
                        }
                    } hvisJa {
                        sjekkRegel {
                            erPeriodeMedMedlemskapInnenfor12MndPeriode
                        } hvisJa {
                            sjekkRegel {
                                erArbeidsforholdUendretForBrukerMedMedlemskap
                            } hvisJa {
                                sjekkRegel {
                                    erDekningUavklart
                                } hvisJa {
                                    uavklartKonklusjon(ytelse)
                                } hvisNei {
                                    sjekkRegel {
                                        harBrukerDekningIMedl
                                    } hvisJa {
                                        jaKonklusjon(ytelse)
                                    } hvisNei {
                                        uavklartKonklusjon(ytelse)
                                    }
                                }
                            } hvisNei {
                                uavklartKonklusjon(ytelse)
                            }
                        } hvisNei {
                            uavklartKonklusjon(ytelse)
                        }
                    }
                }
            }

    val harBrukerMedlOpplysninger = Regel(
            regelId = REGEL_A,
            ytelse = ytelse,
            operasjon = { harBrukerPerioderIMedl() }
    )

    val harBrukerGosysOpplysninger = Regel(
            regelId = REGEL_B,
            ytelse = ytelse,
            operasjon = { harBrukerAapneOppgaverIGsak() }
    )

    val erPerioderAvklart = Regel(
            regelId = REGEL_1_1,
            ytelse = ytelse,
            operasjon = { erPerioderAvklart() }
    )

    val periodeMedOgUtenMedlemskap = Regel(
            regelId = REGEL_1_2,
            ytelse = ytelse,
            operasjon = { harPeriodeMedOgUtenMedlemskap() }
    )

    val periodeMedMedlemskap = Regel(
            regelId = REGEL_1_3,
            ytelse = ytelse,
            operasjon = { periodeMedMedlemskap() }
    )

    val erPeriodeUtenMedlemskapInnenfor12MndPeriode = Regel(
            regelId = REGEL_1_3_1,
            ytelse = ytelse,
            operasjon = { erMedlemskapPeriodeOver12MndPeriode(false) }
    )

    val erPeriodeMedMedlemskapInnenfor12MndPeriode = Regel(
            REGEL_1_4,
            ytelse = ytelse,
            operasjon = { erMedlemskapPeriodeOver12MndPeriode(true) }
    )

    val erArbeidsforholdUendretForBrukerUtenMedlemskap = Regel(
            REGEL_1_3_2,
            ytelse = ytelse,
            operasjon = { erBrukersArbeidsforholdUendret(REGEL_1_3_2) }
    )

    val erArbeidsforholdUendretForBrukerMedMedlemskap = Regel(
            REGEL_1_5,
            ytelse = ytelse,
            operasjon = { erBrukersArbeidsforholdUendret(REGEL_1_5) }
    )

    val erDekningUavklart = Regel(
            REGEL_1_6,
            ytelse = ytelse,
            operasjon = { erBrukersDekningUavklart() }
    )

    val harBrukerDekningIMedl = Regel(
            REGEL_1_7,
            ytelse = ytelse,
            operasjon = { harBrukerMedlemskapSomOmfatterYtelse() }
    )

    private fun harBrukerPerioderIMedl(): Resultat =
            when {
                medlemskap finnesPersonIMedlForKontrollPeriode kontrollPeriodeForMedl -> ja()
                else -> nei()
            }

    private fun harBrukerAapneOppgaverIGsak(): Resultat =
            when {
                oppgaver.finnesAapneOppgaver() -> ja()
                else -> nei()
            }

    private fun erPerioderAvklart(): Resultat =
            when {
                medlemskap finnesUavklartePerioder kontrollPeriodeForMedl -> nei()
                else -> ja()
            }

    private fun harPeriodeMedOgUtenMedlemskap(): Resultat =
            when {
                medlemskap harMedlPeriodeMedOgUtenMedlemskap kontrollPeriodeForMedl -> ja()
                else -> nei()
            }

    private fun erBrukersDekningUavklart(): Resultat {

        val gjeldendeDekning = medlemskap.gjeldendeDekning(kontrollPeriodeForMedl)
        val dekning = Dekning.from(gjeldendeDekning)

        return when {
            dekning == null || dekning.uavklartForYtelse(ytelse) -> ja()
            else -> nei()
        }
    }

    private fun erBrukersArbeidsforholdUendret(regelId: RegelId): Resultat {
        if (harSammeArbeidsforholdSidenFomDatoFraMedl()) {
            regelUendretCounterMidlertidig(regelId, Svar.JA, ytelse).increment()
            return ja()
        } else {
            regelUendretCounterMidlertidig(regelId, Svar.NEI, ytelse).increment()
            return nei()
        }
        /* Tas inn igjen når man ikke lenger trenger egen Grafana-counter for denne (når dekning kan gis i response)
        when {
            harSammeArbeidsforholdSidenFomDatoFraMedl() -> ja()
            else -> nei()
        }
         */
    }

    private fun periodeMedMedlemskap(): Resultat =
            when {
                medlemskap harPeriodeMedMedlemskap kontrollPeriodeForMedl -> ja()
                else -> nei()
            }


    private fun erMedlemskapPeriodeOver12MndPeriode(finnPeriodeMedMedlemskap: Boolean): Resultat =
            when {
                medlemskap.erMedlemskapsperioderOver12Mnd(finnPeriodeMedMedlemskap, kontrollPeriodeForMedl)
                        && medlemskap harGyldigeMedlemskapsperioder kontrollPeriodeForMedl -> ja()
                else -> nei()
            }

    private fun harBrukerMedlemskapSomOmfatterYtelse(): Resultat {
        val dekning = medlemskap gjeldendeDekning kontrollPeriodeForMedl

        dekningCounter(dekning!!, ytelse.metricName()) //Dekning er ikke null, regel 1.5 filtrerer ut dekning som er null

        return when {
            Dekning.from(dekning)!!.gjelderForYtelse(ytelse) -> ja("Bruker har dekning", dekning)
            else -> nei("Bruker har ikke dekning", dekning)
        }
    }

    private fun harSammeArbeidsforholdSidenFomDatoFraMedl(): Boolean =
            arbeidsforhold.arbeidsforholdForDato(medlemskap.tidligsteFraOgMedDatoForMedl(datohjelper.kontrollPeriodeForMedl())).isNotEmpty() &&
                    (ulikeArbeidsforholdMenSammeArbeidsgiver() ||
                            arbeidsforhold.arbeidsforholdForDato(medlemskap.tidligsteFraOgMedDatoForMedl(datohjelper.kontrollPeriodeForMedl())) ==
                            arbeidsforhold.arbeidsforholdForDato(datohjelper.tilOgMedDag().plusDays(1)))

    private fun ulikeArbeidsforholdMenSammeArbeidsgiver() =
            (arbeidsforhold.arbeidsforholdForDato(medlemskap.tidligsteFraOgMedDatoForMedl(datohjelper.kontrollPeriodeForMedl())) != arbeidsforhold.arbeidsforholdForDato(datohjelper.tilOgMedDag()) &&
                    arbeidsforhold.arbeidsforholdForDato(medlemskap.tidligsteFraOgMedDatoForMedl(datohjelper.kontrollPeriodeForMedl())).map { it.arbeidsgiver.identifikator } ==
                    arbeidsforhold.arbeidsforholdForDato(datohjelper.tilOgMedDag().plusDays(1)).map { it.arbeidsgiver.identifikator })

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForMedl {
            with(datagrunnlag) {
                return ReglerForMedl(medlemskap, arbeidsforhold, periode, ytelse, oppgaver, personhistorikk.bostedsadresser, personhistorikk.postadresser, personhistorikk.midlertidigAdresser)
            }
        }
    }
}