package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.common.medlCounter
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.funksjoner.GsakFunksjoner.finnesAapneOppgaver
import no.nav.medlemskap.regler.funksjoner.JoarkFunksjoner.finnesDokumenterMedTillatteTeamer
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.finnesPersonIMedlForKontrollPeriode

class ReglerForRegistrerteOpplysninger(
        val medlemskap: List<Medlemskap> = emptyList(),
        val oppgaver: List<Oppgave> = emptyList(),
        val dokument: List<Journalpost> = emptyList(),
        ytelse: Ytelse,
        val periode: InputPeriode,
        val reglerForGrunnforordningen: ReglerForGrunnforordningen,
        val reglerForMedl: ReglerForMedl
) : Regler(ytelse) {

    val harBrukerRegistrerteOpplysninger = Regel(
            regelId = REGEL_OPPLYSNINGER,
            ytelse = ytelse,
            operasjon = { minstEnAvDisse(medl, joark, gsak) }
    )

    private val medl = Regel(
            REGEL_A,
            ytelse = ytelse,
            operasjon = { sjekkPerioderIMedl() }
    )

    private val gsak = Regel(
            regelId = REGEL_B,
            ytelse = ytelse,
            operasjon = { tellÅpneOppgaver() }
    )

    private val joark = Regel(
            regelId = REGEL_C,
            ytelse = ytelse,
            operasjon = { tellDokumenter() }
    )

    private fun sjekkPerioderIMedl(): Resultat {
        val kontrollPeriodeForMedl = Datohjelper(periode, ytelse).kontrollPeriodeForMedl()
        if (medlemskap.isNotEmpty()) medlCounter().increment()
        return when {
            medlemskap finnesPersonIMedlForKontrollPeriode kontrollPeriodeForMedl -> ja()
            else -> nei()
        }
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

    override fun hentRegelflyt(): Regelflyt {
        val harBrukerRegistrerteOpplysninger = lagRegelflyt(
                regel = harBrukerRegistrerteOpplysninger,
                hvisJa = reglerForMedl.hentRegelflyt(),
                hvisNei = reglerForGrunnforordningen.hentRegelflyt(),
                hvisUavklart = regelFlytUavklart(ytelse)
        )

        return harBrukerRegistrerteOpplysninger
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForRegistrerteOpplysninger {
            return ReglerForRegistrerteOpplysninger(
                    medlemskap = datagrunnlag.medlemskap,
                    oppgaver = datagrunnlag.oppgaver,
                    dokument = datagrunnlag.dokument,
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    reglerForGrunnforordningen = ReglerForGrunnforordningen.fraDatagrunnlag(datagrunnlag),
                    reglerForMedl = ReglerForMedl.fraDatagrunnlag(datagrunnlag)
            )
        }
    }
}