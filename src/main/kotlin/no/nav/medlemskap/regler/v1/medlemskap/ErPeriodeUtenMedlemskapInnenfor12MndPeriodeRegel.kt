package no.nav.medlemskap.regler.v1.medlemskap

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat

class ErPeriodeUtenMedlemskapInnenfor12MndPeriodeRegel(
    ytelse: Ytelse,
    periode: InputPeriode,
    medlemskap: List<Medlemskap>
) : MedlemskapRegel(RegelId.REGEL_1_3_1, ytelse, periode, medlemskap) {

    override fun operasjon(): Resultat {
        return erMedlemskapPeriodeOver12MndPeriode(false)
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErPeriodeUtenMedlemskapInnenfor12MndPeriodeRegel {
            return ErPeriodeUtenMedlemskapInnenfor12MndPeriodeRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                medlemskap = datagrunnlag.medlemskap
            )
        }
    }
}
