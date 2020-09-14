package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*

class ReglerForAndreStatsborgere(
    val periode: InputPeriode,
    ytelse: Ytelse
) : Regler(ytelse) {

    override fun kjørRegelflyter(): List<Resultat> {
        return listOf(kjørRegelflyt(konklusjonUavklart(ytelse, RegelId.REGEL_ANDRE_BORGERE)))
    }

    override fun hentRegelflyter(): List<Regelflyt> {
        return listOf(konklusjonUavklart(ytelse, RegelId.REGEL_ANDRE_BORGERE))
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
