package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Journalpost
import no.nav.medlemskap.domene.Oppgave
import no.nav.medlemskap.domene.Status
import no.nav.medlemskap.regler.common.Avklaring
import no.nav.medlemskap.regler.common.Funksjoner.antall
import no.nav.medlemskap.regler.common.Funksjoner.erDelAv
import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.common.Regelsett
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.uttrykk.EllerUttrykk.Companion.enten
import no.nav.medlemskap.regler.common.uttrykk.HvisUttrykk.Companion.hvis

class RegelsettForRegistrerteOpplysninger : Regelsett("Regelsett for registrerte opplysninger") {

    override val KONKLUSJON_IDENTIFIKATOR: String get() = "OPP"
    override val KONKLUSJON_AVKLARING: String get() = "Har brukeren registrerte eller uregistrerte opplysninger på medlemskapsområdet"

    override fun evaluer(personfakta: Personfakta): Resultat {
        val resultat =
                avklar {
                    enten {
                        personfakta oppfyller harRegistrerteOpplysningerIMedl
                    } eller {
                        personfakta oppfyller harÅpenOppgaveIGsak
                    } eller {
                        personfakta oppfyller harDokumenterIJoark
                    } resultatMedId { KONKLUSJON_IDENTIFIKATOR }
                } hvisJa {
                    konkluderMed(ja("Brukeren har registrerte eller uregistrerte opplysninger på medlemskapsområdet"))
                } hvisNei {
                    konkluderMed(nei("Brukeren har ingen registrerte eller uregistrerte opplysninger på medlemskapsområdet"))
                } hvisUavklart {
                    konkluderMed(uavklart("Kan ikke vurdere registrerte eller uregistrerte opplysninger"))
                }

        return hentUtKonklusjon(resultat)
    }

    private val tillatteTemaer = listOf("MED", "UFM", "TRY")
    private val tillatteStatuser = listOf(Status.AAPNET, Status.OPPRETTET, Status.UNDER_BEHANDLING)

    private val harRegistrerteOpplysningerIMedl = Avklaring(
            identifikator = "VED-1",
            avklaring = "Finnes det registrerte opplysninger i MEDL",
            beskrivelse = "For å sjekke avklarte lovvalg og/eller medlemskap",
            operasjon = { sjekkPerioderIMedl(it) }
    )

    private val harDokumenterIJoark = Avklaring(
            identifikator = "VED-2",
            avklaring = "Finnes det dokumenter i JOARK på medlemskapsområdet",
            beskrivelse = "For å sjekke uregistrerte lovvalg og/eller medlemskap",
            operasjon = { tellDokumenter(it) }
    )

    private val harÅpenOppgaveIGsak = Avklaring(
            identifikator = "VED-3",
            avklaring = "Finnes det åpne oppgaver i GOSYS på medlemskapsområdet",
            beskrivelse = "For å sjekke uregistrerte lovvalg og/eller medlemskap",
            operasjon = { tellÅpneOppgaver(it) }
    )

    private fun sjekkPerioderIMedl(personfakta: Personfakta): Resultat =
            hvis {
                antall(personfakta.personensPerioderIMedl()) == 0
            } så {
                nei("Brukeren har ingen registrerte opplysninger i MEDL")
            } ellers {
                ja("Brukeren har registrerte opplysninger i MEDL")
            }

    private fun tellDokumenter(personfakta: Personfakta): Resultat =
            hvis {
                antallDokumenter(personfakta.personensDokumenterIJoark()) > 0
            } så {
                ja("Brukeren har dokumenter på medlemskapsområdet")
            } ellers {
                nei("Brukeren har ingen dokumenter på medlemskapsområdet")
            }


    private fun tellÅpneOppgaver(personfakta: Personfakta): Resultat =
            hvis {
                antallÅpneOppgaver(personfakta.personensOppgaverIGsak()) > 0
            } så {
                ja("Brukeren har åpne oppgaver i GOSYS på medlemskapsområdet")
            } ellers {
                nei("Brukeren har ingen åpne oppgaver i GOSYS på medlemskapsområdet")
            }


    private fun antallDokumenter(liste: List<Journalpost>) =
            liste.count { journalpost ->
                journalpost.tema erDelAv tillatteTemaer
            }

    private fun antallÅpneOppgaver(liste: List<Oppgave>) =
            liste.count { oppgave ->
                oppgave.tema erDelAv tillatteTemaer && oppgave.status erDelAv tillatteStatuser
            }


}
