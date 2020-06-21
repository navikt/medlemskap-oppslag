package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Journalpost
import no.nav.medlemskap.domene.Oppgave
import no.nav.medlemskap.domene.Status
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.antall
import no.nav.medlemskap.regler.common.Funksjoner.erDelAv
import no.nav.medlemskap.regler.funksjoner.GsakFunksjoner.finnesAapneOppgaver
import no.nav.medlemskap.regler.funksjoner.JoarkFunksjoner.finnesDokumenterMedTillatteTeamer

class ReglerForRegistrerteOpplysninger(val datagrunnlag: Datagrunnlag) : Regler() {

    override fun hentHovedRegel() =
            sjekkRegel {
                harBrukerRegistrerteOpplysninger
            }

    private val harBrukerRegistrerteOpplysninger = Regel(
            identifikator = "OPPLYSNINGER",
            avklaring = "Finnes det registrerte opplysninger på bruker?",
            beskrivelse = "For å sjekke uregistrerte lovvalg og/eller medlemskap",
            operasjon = { minstEnAvDisse(medl, joark, gsak) }
    )

    private val medl = Regel(
            identifikator = "OPPLYSNINGER-MEDL",
            avklaring = "Finnes det registrerte opplysninger i MEDL?",
            beskrivelse = """
                Vedtak (gjort av NAV eller utenlandsk trygdemyndighet) som er registrert i MEDL, 
                må vurderes manuelt og det må vurderes om brukers situasjon er uendret i forhold 
                til situasjonen på vedtakstidspunktet.
            """.trimIndent(),
            operasjon = { sjekkPerioderIMedl() }
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


    private fun sjekkPerioderIMedl(): Resultat =
            when {
                datagrunnlag.medlemskap.isNotEmpty() -> ja()
                else -> nei()
            }

    private fun tellDokumenter(): Resultat =
            when {
                datagrunnlag.dokument.finnesDokumenterMedTillatteTeamer() -> ja()
                else -> nei()
            }


    fun tellÅpneOppgaver(): Resultat =
            when {
                datagrunnlag.oppgaver.finnesAapneOppgaver() -> ja()
                else -> nei()
            }
}