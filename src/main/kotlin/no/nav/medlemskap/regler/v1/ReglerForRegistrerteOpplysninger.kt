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
            beskrivelse = "",
            operasjon = { minstEnAvDisse(medl, joark, gsak) }
    )

    private val medl = Regel(
            identifikator = "OPPLYSNINGER-MEDL",
            avklaring = "Finnes det registrerte opplysninger i MEDL?",
            beskrivelse = "",
            operasjon = { sjekkPerioderIMedl() }
    )

    private val joark = Regel(
            identifikator = "OPPLYSNINGER-JOARK",
            avklaring = "Finnes det dokumenter i JOARK på medlemskapsområdet?",
            beskrivelse = "",
            operasjon = { tellDokumenter() }
    )

    private val gsak = Regel(
            identifikator = "OPPLYSNINGER-GOSYS",
            avklaring = "Finnes det åpne oppgaver i GOSYS på medlemskapsområdet?",
            beskrivelse = "",
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