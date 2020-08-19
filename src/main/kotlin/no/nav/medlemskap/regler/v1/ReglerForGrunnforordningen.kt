package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.Regelflyt
import no.nav.medlemskap.regler.common.Regler
import no.nav.medlemskap.regler.common.regelFlytUavklart

class ReglerForGrunnforordningen(
        ytelse: Ytelse,
        val periode: InputPeriode,
        val statsborgerskap: List<Statsborgerskap>,
        val reglerForArbeidsForhold: ReglerForArbeidsforhold
) : Regler(ytelse) {

    override fun hentRegelflyt(): Regelflyt {
        val erBrukerEØSborgerFlyt = lagRegelflyt(
                regel = erBrukerEØSborger.regel,
                hvisJa = reglerForArbeidsForhold.hentRegelflyt(),
                hvisNei = regelFlytUavklart(ytelse)
        )

        return erBrukerEØSborgerFlyt
    }

    val erBrukerEØSborger = ErBrukerEøsBorgerRegel(
            ytelse = ytelse,
            periode = periode,
            statsborgerskap = statsborgerskap
    )

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForGrunnforordningen {
            return ReglerForGrunnforordningen(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    statsborgerskap = datagrunnlag.personhistorikk.statsborgerskap,
                    reglerForArbeidsForhold = ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag)
            )
        }
    }
}
