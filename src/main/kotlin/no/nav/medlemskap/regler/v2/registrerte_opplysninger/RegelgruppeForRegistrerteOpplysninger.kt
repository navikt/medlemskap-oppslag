package no.nav.medlemskap.regler.v2.registrerte_opplysninger

import no.nav.medlemskap.domene.Journalpost
import no.nav.medlemskap.domene.Oppgave
import no.nav.medlemskap.domene.Status
import no.nav.medlemskap.regler.common.Funksjoner.antall
import no.nav.medlemskap.regler.common.Funksjoner.erDelAv
import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.v2.common.*

class RegelgruppeForRegistrerteOpplysninger(val personfakta: Personfakta) : Regelgruppe() {


    override fun evaluer(): Spørsmål {
        val hovedspørsmål = Spørsmål(
                identifikator = "1.3",
                spørsmål = "Har bruker noen registrerte opplysninger?",
                beskrivelse = "",
                operasjon = {
                    minstEnAvDisse(
                            harRegistrerteOpplysningerIMedl,
                            harDokumenterIJoark,
                            harÅpenOppgaveIGsak
                    )
                }
        )

        return hovedspørsmål.utfør()
    }

    private val tillatteTemaer = listOf("MED", "UFM", "TRY")
    private val tillatteStatuser = listOf(Status.AAPNET, Status.OPPRETTET, Status.UNDER_BEHANDLING)

    private val harRegistrerteOpplysningerIMedl = Spørsmål(
            identifikator = "1.3.1",
            spørsmål = "Finnes det registrerte opplysninger i MEDL?",
            beskrivelse = "For å sjekke avklarte lovvalg og/eller medlemskap",
            operasjon = { sjekkPerioderIMedl() }
    )

    private val harDokumenterIJoark = Spørsmål(
            identifikator = "1.3.2",
            spørsmål = "Finnes det dokumenter i JOARK på medlemskapsområde?t",
            beskrivelse = "For å sjekke uregistrerte lovvalg og/eller medlemskap",
            operasjon = { tellDokumenter() }
    )

    private val harÅpenOppgaveIGsak = Spørsmål(
            identifikator = "1.3.3",
            spørsmål = "Finnes det åpne oppgaver i GOSYS på medlemskapsområdet?",
            beskrivelse = "For å sjekke uregistrerte lovvalg og/eller medlemskap",
            operasjon = { tellÅpneOppgaver() }
    )

    private fun sjekkPerioderIMedl(): Svar =
            when {
                antall(personfakta.personensPerioderIMedl()) == 0 -> nei("Brukeren har ingen registrerte opplysninger i MEDL")
                else -> ja("Brukeren har registrerte opplysninger i MEDL")
            }

    private fun tellDokumenter(): Svar =
            when {
                antallDokumenter(personfakta.personensDokumenterIJoark()) > 0 -> ja("Brukeren har dokumenter på medlemskapsområdet")
                else -> nei("Brukeren har ingen dokumenter på medlemskapsområdet")
            }


    private fun tellÅpneOppgaver(): Svar =
            when {
                antallÅpneOppgaver(personfakta.personensOppgaverIGsak()) > 0 -> ja("Brukeren har åpne oppgaver i GOSYS på medlemskapsområdet")
                else -> nei("Brukeren har ingen åpne oppgaver i GOSYS på medlemskapsområdet")
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
