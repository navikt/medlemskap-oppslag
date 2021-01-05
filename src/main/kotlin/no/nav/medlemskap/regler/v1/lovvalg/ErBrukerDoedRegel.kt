package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.personhistorikk.Personhistorikk.Companion.erBrukerDoedEtterPeriode
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.common.Resultat.Companion.uavklart
import java.time.LocalDate

class ErBrukerDoedRegel(
    val doedsfall: List<LocalDate>,
    ytelse: Ytelse,
    val periode: InputPeriode,
    førsteDagForYtelse: LocalDate?
) : LovvalgRegel(RegelId.REGEL_13, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {
        val erBrukerDoed = !doedsfall.isNullOrEmpty()
        val erBrukerDoedEtterInputperiode = doedsfall.erBrukerDoedEtterPeriode(periode)

        if (erBrukerDoed && erBrukerDoedEtterInputperiode) {
            return ja(regelId)
        } else if (erBrukerDoed && !erBrukerDoedEtterInputperiode) {
            return uavklart(regelId)
        }
        return nei(regelId)
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErBrukerDoedRegel {
            return ErBrukerDoedRegel(
                doedsfall = datagrunnlag.pdlpersonhistorikk.doedsfall,
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse
            )
        }
    }
}
