package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.domene.Statsborgerskap.Companion.erNordiskBorger
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import java.time.LocalDate

class HarBrukerNordiskStatsborgerskapRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    førsteDagForYtelse: LocalDate?,
    private val statsborgerskap: List<Statsborgerskap>,
    regelId: RegelId = RegelId.REGEL_11
) : LovvalgRegel(regelId, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {

        return when {
            statsborgerskap.erNordiskBorger(kontrollPeriodeForPersonhistorikk) -> ja(regelId)
            else -> nei(regelId)
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerNordiskStatsborgerskapRegel {
            return HarBrukerNordiskStatsborgerskapRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                statsborgerskap = datagrunnlag.pdlpersonhistorikk.statsborgerskap
            )
        }
    }
}
