package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Journalpost
import no.nav.medlemskap.domene.Oppgave
import no.nav.medlemskap.domene.Status
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.antall
import no.nav.medlemskap.regler.common.Funksjoner.erDelAv

class ReglerForRegistrerteOpplysninger(val personfakta: Personfakta) : Regler() {

    override fun hentHovedRegel() =
            sjekkRegel {
                harBrukerRegistrerteOpplysninger
            }

    private val harBrukerRegistrerteOpplysninger = Regel(
            identifikator = "OPP",
            avklaring = "Finnes det registrerte opplysninger på bruker?",
            beskrivelse = "For å sjekke uregistrerte lovvalg og/eller medlemskap",
            operasjon = { minstEnAvDisse(medl, joark, gsak) }
    )

    private val medl = Regel(
            identifikator = "OPP-1",
            avklaring = "Finnes det registrerte opplysninger i MEDL?",
            beskrivelse = "For å sjekke avklarte lovvalg og/eller medlemskap",
            operasjon = { sjekkPerioderIMedl() }
    )

    private val joark = Regel(
            identifikator = "OPP-2",
            avklaring = "Finnes det dokumenter i JOARK på medlemskapsområdet?",
            beskrivelse = "For å sjekke uregistrerte lovvalg og/eller medlemskap",
            operasjon = { tellDokumenter() }
    )

    private val gsak = Regel(
            identifikator = "OPP-3",
            avklaring = "Finnes det åpne oppgaver i GOSYS på medlemskapsområdet?",
            beskrivelse = "For å sjekke uregistrerte lovvalg og/eller medlemskap",
            operasjon = { tellÅpneOppgaver() }
    )

    private val tillatteTemaer = listOf("MED", "UFM", "TRY")
    private val tillatteStatuser = listOf(Status.AAPNET, Status.OPPRETTET, Status.UNDER_BEHANDLING)

    private fun sjekkPerioderIMedl(): Resultat =
            when {
                antall(personfakta.personensPerioderIMedl()) == 0 -> nei()
                else -> ja()
            }

    private fun tellDokumenter(): Resultat =
            when {
                antallDokumenter(personfakta.personensDokumenterIJoark()) > 0 -> ja()
                else -> nei()
            }


    private fun tellÅpneOppgaver(): Resultat =
            when {
                antallÅpneOppgaver(personfakta.personensOppgaverIGsak()) > 0 -> ja()
                else -> nei()
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
