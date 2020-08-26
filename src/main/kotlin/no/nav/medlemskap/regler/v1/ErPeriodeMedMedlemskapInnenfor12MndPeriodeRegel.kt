package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat

class ErPeriodeMedMedlemskapInnenfor12MndPeriodeRegel(
        ytelse: Ytelse,
        periode: InputPeriode,
        medlemskap: List<Medlemskap>
) : MedlemRegel(RegelId.REGEL_1_4, ytelse, periode, medlemskap) {

    override fun operasjon(): Resultat {
        return erMedlemskapPeriodeOver12MndPeriode(true)
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErPeriodeMedMedlemskapInnenfor12MndPeriodeRegel {
            return ErPeriodeMedMedlemskapInnenfor12MndPeriodeRegel(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    medlemskap = datagrunnlag.medlemskap
            )
        }
    }
}