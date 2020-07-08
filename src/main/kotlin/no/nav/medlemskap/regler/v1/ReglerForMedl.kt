package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.common.dekningCounter
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Dekning.Companion.gjelderForYtelse
import no.nav.medlemskap.domene.Dekning.Companion.uavklartForYtelse
import no.nav.medlemskap.domene.Ytelse.Companion.metricName
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.arbeidsforholdForDato
import no.nav.medlemskap.regler.funksjoner.GsakFunksjoner.finnesAapneOppgaver
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.erMedlemskapsperioderOver12Mnd
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.finnesPersonIMedlForKontrollPeriode
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
                harBrukerMedlOpplysninger
            } hvisJa {
                sjekkRegel {
                    harBrukerGosysOpplysninger
                } hvisNei {
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
                                    neiKonklusjon(ytelse)
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
                                            jaKonklusjon(ytelse)
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
                } hvisJa {
                    uavklartKonklusjon(ytelse)
                }
            }

    val harBrukerMedlOpplysninger = Regel(
            identifikator = "A",
            avklaring = "Finnes det noe på personen i MEDL?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { harBrukerPerioderIMedl() }
    )

    val harBrukerGosysOpplysninger = Regel(
            identifikator = "B",
            avklaring = "Finnes det åpne oppgaver i GOSYS på medlemskapsområdet?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { harBrukerAapneOppgaverIGsak() }
    )

    val periodeMedOgUtenMedlemskap = Regel(
            identifikator = "1.1",
            avklaring = "Er det periode både med og uten medlemskap innenfor 12 mnd?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { harPeriodeMedOgUtenMedlemskap() }
    )

    val periodeMedMedlemskap = Regel(
            identifikator = "1.2",
            avklaring = "Er det en periode med medlemskap?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { periodeMedMedlemskap() }
    )

    val erPeriodeUtenMedlemskapInnenfor12MndPeriode = Regel(
            identifikator = "1.2.1",
            avklaring = "Er hele perioden uten medlemskap innenfor 12-måneders perioden?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { erMedlemskapPeriodeOver12MndPeriode(false) }
    )

    val erPeriodeMedMedlemskapInnenfor12MndPeriode = Regel(
            identifikator = "1.3",
            avklaring = "Er hele perioden med medlemskap innenfor 12-måneders perioden?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { erMedlemskapPeriodeOver12MndPeriode(true) }
    )


    val erArbeidsforholdUendretForBrukerUtenMedlemskap = Regel(
            identifikator = "1.2.2",
            avklaring = "Er bruker uten medlemskap sin situasjon uendret?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { erBrukersArbeidsforholdUendret() }
    )

    val erArbeidsforholdUendretForBrukerMedMedlemskap = Regel(
            identifikator = "1.4",
            avklaring = "Er brukers arbeidsforhold uendret?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { erBrukersArbeidsforholdUendret() }
    )

    val erDekningUavklart = Regel(
            identifikator = "1.5",
            avklaring = "Er brukers dekning uavklart?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { erBrukersDekningUavklart() }
    )

    val harBrukerDekningIMedl = Regel(
            identifikator = "1.6",
            avklaring = "Har bruker et medlemskap som omfatter ytelse? (Dekning i MEDL)",
            beskrivelse = "",
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

    private fun erBrukersArbeidsforholdUendret(): Resultat =
            when {
                harSammeArbeidsforholdSidenFomDatoFraMedl() -> ja()
                else -> nei()
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
                            arbeidsforhold.arbeidsforholdForDato(datohjelper.tilOgMedDag()))

    private fun ulikeArbeidsforholdMenSammeArbeidsgiver() =
            (arbeidsforhold.arbeidsforholdForDato(medlemskap.tidligsteFraOgMedDatoForMedl(datohjelper.kontrollPeriodeForMedl())) != arbeidsforhold.arbeidsforholdForDato(datohjelper.tilOgMedDag()) &&
                    arbeidsforhold.arbeidsforholdForDato(medlemskap.tidligsteFraOgMedDatoForMedl(datohjelper.kontrollPeriodeForMedl())).map { it.arbeidsgiver.identifikator } ==
                    arbeidsforhold.arbeidsforholdForDato(datohjelper.tilOgMedDag()).map { it.arbeidsgiver.identifikator })

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForMedl {
            with(datagrunnlag) {
                return ReglerForMedl(medlemskap, arbeidsforhold, periode, ytelse, oppgaver, personhistorikk.bostedsadresser, personhistorikk.postadresser, personhistorikk.midlertidigAdresser)
            }
        }
    }
}