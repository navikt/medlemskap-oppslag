package no.nav.medlemskap.regler.v1.statsborgerskap

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap.Companion.erBritiskEllerSveitsiskBorger
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.v1.lovvalg.LovvalgRegel
import java.time.LocalDate

class ErBrukerBritiskEllerSveitsiskBorgerRegel(
    regelId: RegelId = RegelId.REGEL_19_5,
    ytelse: Ytelse,
    private val statsborgerskap: List<Statsborgerskap>,
    periode: InputPeriode,
    førsteDagForYtelse: LocalDate?
) : LovvalgRegel(regelId, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {

        return when {
            statsborgerskap.erBritiskEllerSveitsiskBorger(kontrollPeriodeForPersonhistorikk) -> Resultat.ja(regelId)
            else -> Resultat.nei(regelId)
        }
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErBrukerBritiskEllerSveitsiskBorgerRegel {

            return ErBrukerBritiskEllerSveitsiskBorgerRegel(
                regelId = RegelId.REGEL_19_5,
                ytelse = datagrunnlag.ytelse,
                statsborgerskap = datagrunnlag.pdlpersonhistorikk.statsborgerskap,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse
            )
        }
    }
}
