package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.funksjoner.GsakFunksjoner.finnesAapneOppgaver
import no.nav.medlemskap.regler.funksjoner.JoarkFunksjoner.finnesDokumenterMedTillatteTeamer

class ReglerForRegistrerteOpplysninger(
        val medlemskap: List<Medlemskap> = emptyList(),
        val oppgaver: List<Oppgave> = emptyList(),
        val dokument: List<Journalpost> = emptyList(),
        val ytelse: Ytelse
) : Regler() {

    override fun hentHovedRegel() =
            sjekkRegel {
                harBrukerRegistrerteOpplysninger
            }

    private val harBrukerRegistrerteOpplysninger = Regel(
            identifikator = "OPPLYSNINGER",
            avklaring = "Finnes det registrerte opplysninger på bruker?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { minstEnAvDisse(medl, joark, gsak) }
    )

    private val medl = Regel(
            identifikator = "OPPLYSNINGER-MEDL",
            avklaring = "Finnes det registrerte opplysninger i MEDL?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { sjekkPerioderIMedl() }
    )

    private val joark = Regel(
            identifikator = "OPPLYSNINGER-JOARK",
            avklaring = "Finnes det dokumenter i JOARK på medlemskapsområdet?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { tellDokumenter() }
    )

    private val gsak = Regel(
            identifikator = "OPPLYSNINGER-GOSYS",
            avklaring = "Finnes det åpne oppgaver i GOSYS på medlemskapsområdet?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { tellÅpneOppgaver() }
    )


    private fun sjekkPerioderIMedl(): Resultat =
            when {
                medlemskap.isNotEmpty() -> ja()
                else -> nei()
            }

    private fun tellDokumenter(): Resultat =
            when {
                dokument.finnesDokumenterMedTillatteTeamer() -> ja()
                else -> nei()
            }


    fun tellÅpneOppgaver(): Resultat =
            when {
                oppgaver.finnesAapneOppgaver() -> ja()
                else -> nei()
            }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForRegistrerteOpplysninger {
            return ReglerForRegistrerteOpplysninger(
                    medlemskap = datagrunnlag.medlemskap,
                    oppgaver = datagrunnlag.oppgaver,
                    dokument = datagrunnlag.dokument,
                    ytelse = datagrunnlag.ytelse
            )
        }
    }
}