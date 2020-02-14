package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Journalpost
import no.nav.medlemskap.domene.Oppgave
import no.nav.medlemskap.domene.Status
import no.nav.medlemskap.regler.common.Avklaring
import no.nav.medlemskap.regler.common.Fakta
import no.nav.medlemskap.regler.common.Funksjoner.antall
import no.nav.medlemskap.regler.common.Funksjoner.erDelAv
import no.nav.medlemskap.regler.common.Regelsett
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.uttrykk.EllerUttrykk.Companion.enten
import no.nav.medlemskap.regler.common.uttrykk.HvisUttrykk.Companion.hvis

class RegelsettForManuelleVedtak(fakta: Fakta) : Regelsett("Regelsett for manuelle vedtak", fakta) {

    override val KONKLUSJON_IDENTIFIKATOR: String get() = "VED"
    override val KONKLUSJON_AVKLARING: String get() = "Har personen manuelle vadtak fra NAV?"

    override fun evaluer(): Resultat {
        val resultat =
                avklar {
                    enten {
                        harAvklarteVedtakIMedl evaluerMed fakta
                    } eller {
                        harÅpenOppgaveIGsak evaluerMed fakta
                    } eller {
                        harDokumenterIJoark evaluerMed fakta
                    } resultatMedId { KONKLUSJON_IDENTIFIKATOR }
                } hvisJa {
                    konkluderMed(ja("Personen har manuelle vedtak"))
                } hvisNei {
                    konkluderMed(nei("Personen har ingen manuelle vedtak"))
                } hvisUavklart {
                    konkluderMed(uavklart("Kan ikke vurdere manuelle vedtak"))
                }

        return hentUtKonklusjon(resultat)
    }

    private val tillatteTemaer = listOf("MED", "UFM", "TRY")
    private val tillatteStatuser = listOf(Status.AAPNET, Status.OPPRETTET, Status.UNDER_BEHANDLING)

    private val harAvklarteVedtakIMedl = Avklaring(
            identifikator = "VED-1",
            avklaring = "Sjekk om det finnes avklarte vedtak i MEDL",
            beskrivelse = "",
            operasjon = { sjekkPerioderIMedl(it) }
    )

    private val harDokumenterIJoark = Avklaring(
            identifikator = "VED-2",
            avklaring = "Finnes det åpne dokumenter i JOARK",
            beskrivelse = "",
            operasjon = { tellDokumenter(it) }
    )

    private val harÅpenOppgaveIGsak = Avklaring(
            identifikator = "VED-3",
            avklaring = "Finnes det åpne oppgaver i GOSYS",
            beskrivelse = "",
            operasjon = { tellÅpneOppgaver(it) }
    )

    private fun sjekkPerioderIMedl(fakta: Fakta): Resultat =
            hvis {
                antall(fakta.personensPerioderIMedl()) == 0
            } så {
                nei("Personen har ingen vedtak i MEDL")
            } ellers {
                ja("Personen har vedtak i MEDL")
            }

    private fun tellDokumenter(fakta: Fakta): Resultat =
            hvis {
                antallDokumenter(fakta.personensDokumenterIJoark()) > 0
            } så {
                ja("Personen har dokumenter knyttet til medlemskapsaker.")
            } ellers {
                nei("Personen har ingen dokumenter knyttet til medlemskapsaker.")
            }


    private fun tellÅpneOppgaver(fakta: Fakta): Resultat =
            hvis {
                antallÅpneOppgaver(fakta.personensOppgaverIGsak()) > 0
            } så {
                ja("Personen har åpne oppgaver i GOSYS.")
            } ellers {
                nei("Personen har ingen åpne oppgaver i GOSYS.")
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
