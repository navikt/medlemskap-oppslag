package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Arbeidsforholdstype
import no.nav.medlemskap.domene.Skipsregister
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.alleEr
import no.nav.medlemskap.regler.common.Funksjoner.finnes
import no.nav.medlemskap.regler.common.Funksjoner.finnesMindreEnn
import no.nav.medlemskap.regler.common.Funksjoner.inneholderNoe
import no.nav.medlemskap.regler.common.Funksjoner.kunInneholder

enum class YrkeskoderForLuftFart(val styrk: String) {
    //5111-rangen:
    DAGLIG_LEDER_VERTER_VERTINNER_PÅ_FLY("5111013"),
    FLY_OG_TOGVERT("5111100"),
    FLYVERT("5111103"),
    FLYVERTINNE("5111104"),
    KABINPERSONALE("5111105"),
    AIRPURSER("5111106"),
    HOST_LUFTFARTØY("5111107"),
    CABIN_ATTENDANT("5111108"),
    STEWARDESS_LUFTFARTØY("5111109"),
    PURSERASSISTENT_TRANSPORT("5111110"),
    PURSER_TRANSPORT("5111114"),
    CABIN_CHIEF("5111115"),
    AIRSTEWARD("5111116"),
    KABINSJEF("5111117"),
    STEWARD_LUFTFARTØY("5111119"),
    HOSTESS_LUFTFARTØY("5111122"),
    FLYVERTLÆRLING("5111124"),

    //3143-rangen:
    FLYGER_OVERORDNET("3143100"),
    LEDER_FLYGERE("3143001"),
    DAGLIG_LEDER_FLYGERE("3143013"),
    ANNENFLYGER("3143101"),
    FLYGER("3143102"),
    FLYNAVIGATØR("3143103"),
    NAVIGATØR_FLY("3143104"),
    SJEKKFLYGER("3143105"),
    KONTROLLFLYGER("3143106"),
    PILOT("3143107"),
    FLYKAPTEIN("3143108"),
    FLYSTYRMANN("3143109"),
    TRAFIKKFLYGER("3143110")

}

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
            }

    private val reglerForLovvalg = ReglerForLovvalg(personfakta)

    private val harBrukerSammenhengendeArbeidsforholdSiste12Mnd = Regel(
            identifikator = "ARB-1",
            avklaring = "Har bruker hatt sammenhengende arbeidsforhold i Aa-registeret de siste 12 månedene?",
            beskrivelse = "",
            operasjon = { sjekkArbeidsforhold() }
    )

    private val erArbeidsgiverOrganisasjon = Regel(
            identifikator = "ARB-2",
            avklaring = "Er foretaket registrert i foretaksregisteret?",
            beskrivelse = "",
            operasjon = { sjekkArbeidsgiver() }
    )

    private val harForetakMerEnn5Ansatte = Regel(
            identifikator = "ARB-3",
            avklaring = "Har arbeidsgiver sin hovedaktivitet i Norge?",
            beskrivelse = "",
            operasjon = { sjekkOmForetakMerEnn5Ansatte() }
    )

    private val erForetakAktivt = Regel(
            identifikator = "ARB-4",
            avklaring = "Er foretaket aktivt?",
            beskrivelse = "",
            operasjon = { sjekKonkursstatus() }
    )

    private val erArbeidsforholdetMaritimt = Regel(
            identifikator = "ARB-5",
            avklaring = "Har bruker et maritimt arbeidsforhold?",
            beskrivelse = "",
            operasjon = { sjekkMaritim() }
    )

    private val jobberBrukerPåNorskSkip = Regel(
            identifikator = "ARB-6",
            avklaring = "Jobber bruker på et norskregistrert skip?",
            beskrivelse = "",
            operasjon = { sjekkSkipsregister() }
    )

    private val erBrukerPilotEllerKabinansatt = Regel(
            identifikator = "ARB-7",
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
                personfakta.sisteArbeidsforholdSkipsregister() kunInneholder Skipsregister.nor.name -> ja()
                else -> nei()
            }


    private fun sjekKonkursstatus(): Resultat {
        if (personfakta.konkursStatuserArbeidsgivere().finnes())
            return nei("Arbeidstaker har hatt arbeidsforhold til arbeidsgiver som har konkurs-status satt")

        return ja()
    }
}
