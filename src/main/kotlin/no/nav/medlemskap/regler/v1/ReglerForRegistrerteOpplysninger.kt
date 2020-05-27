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
            identifikator = "OPPLYSNINGER",
            avklaring = "Finnes det registrerte opplysninger på bruker?",
            beskrivelse = "For å sjekke uregistrerte lovvalg og/eller medlemskap",
            operasjon = { minstEnAvDisse(joark, gsak) }
    )

    private val joark = Regel(
            identifikator = "OPPLYSNINGER-JOARK",
            avklaring = "Finnes det dokumenter i JOARK på medlemskapsområdet?",
            beskrivelse = """
                Skal sikre at ubehandlede saker og ikke-registrerte vedtak fanges opp for å bli 
                vurdert manuelt. MEDL er ikke en komplett oversikt over alle medlemsavklaringene 
                som NAV har gjort. 
            """.trimIndent(),
            operasjon = { tellDokumenter() }
    )

    private val gsak = Regel(
            identifikator = "OPPLYSNINGER-GOSYS",
            avklaring = "Finnes det åpne oppgaver i GOSYS på medlemskapsområdet?",
            beskrivelse = """"
                Skal sikre at ubehandlede saker og ikke-registrerte vedtak fanges opp for å bli 
                vurdert manuelt. MEDL er ikke en komplett oversikt over alle medlemsavklaringene 
                som NAV har gjort. 
            """.trimIndent(),
            operasjon = { tellÅpneOppgaver() }
    )

    private val tillatteTemaer = listOf("MED", "UFM", "TRY")
    private val tillatteStatuser = listOf(Status.AAPNET, Status.OPPRETTET, Status.UNDER_BEHANDLING)

    private fun tellDokumenter(): Resultat =
            when {
                personfakta.personensDokumenterIJoark().antallDokumenterMedTillatteTemaer > 0 -> ja()
                else -> nei()
            }


    private fun tellÅpneOppgaver(): Resultat =
            when {
                personfakta.personensOppgaverIGsak().antallÅpneOppgaver > 0 -> ja()
                else -> nei()
            }


    private val List<Journalpost>.antallDokumenterMedTillatteTemaer: Int
        get() = count { journalpost ->
            journalpost.tema erDelAv tillatteTemaer
        }

    private val List<Oppgave>.antallÅpneOppgaver: Int
        get() = count { oppgave ->
            oppgave.tema erDelAv tillatteTemaer && oppgave.status erDelAv tillatteStatuser
        }

}
