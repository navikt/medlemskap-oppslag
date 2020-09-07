package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.Regelflyt
import no.nav.medlemskap.regler.common.Regler
import no.nav.medlemskap.regler.common.konklusjonUavklart

class ReglerForAndreStatsborgere(
    val periode: InputPeriode,
    ytelse: Ytelse
) : Regler(ytelse) {

    override fun hentRegelflyter(): List<Regelflyt> {
        return listOf(konklusjonUavklart(ytelse))
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForAndreStatsborgere {
            with(datagrunnlag) {
                return ReglerForAndreStatsborgere(
                    periode = periode,
                    ytelse = ytelse
                )
            }
        }
    }
}
