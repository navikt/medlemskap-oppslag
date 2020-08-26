package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.finnesUavklartePerioder

class ErPerioderAvklartRegel(
        ytelse: Ytelse,
        val medlemskap: List<Medlemskap>,
        val periode: InputPeriode
) : MedlemRegel(RegelId.REGEL_1_1, ytelse, periode, medlemskap) {

    override fun operasjon(): Resultat {
        return when {
            medlemskap finnesUavklartePerioder kontrollPeriodeForMedl -> nei()
            else -> ja()
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErPerioderAvklartRegel {
            return ErPerioderAvklartRegel(
                    ytelse = datagrunnlag.ytelse,
                    medlemskap = datagrunnlag.medlemskap,
                    periode = datagrunnlag.periode
            )
        }
    }
}