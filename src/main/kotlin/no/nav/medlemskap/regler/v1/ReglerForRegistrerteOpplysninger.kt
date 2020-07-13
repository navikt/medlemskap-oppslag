package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
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
            regelId = REGEL_OPPLYSNINGER,
            ytelse = ytelse,
            operasjon = { minstEnAvDisse(medl, joark, gsak) }
    )

    private val medl = Regel(
            REGEL_OPPLYSNINGER_MEDL,
            ytelse = ytelse,
            operasjon = { sjekkPerioderIMedl() }
    )

    private val joark = Regel(
            regelId = REGEL_OPPLYSNINGER_JOARK,
            ytelse = ytelse,
            operasjon = { tellDokumenter() }
    )

    private val gsak = Regel(
            regelId = REGEL_OPPLYSNINGER_GOSYS,
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