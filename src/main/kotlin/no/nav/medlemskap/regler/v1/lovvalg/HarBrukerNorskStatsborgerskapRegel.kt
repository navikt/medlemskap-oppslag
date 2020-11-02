package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.sjekkStatsborgerskap
import java.time.LocalDate

class HarBrukerNorskStatsborgerskapRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    førsteDagForYtelse: LocalDate?,
    private val statsborgerskap: List<Statsborgerskap>,
    regelId: RegelId = RegelId.REGEL_11
) : LovvalgRegel(regelId, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {
        val erNorskStatsborger = sjekkStatsborgerskap(statsborgerskap, kontrollPeriodeForPersonhistorikk, { s -> Eøsland.erNorsk(s) })

        return when {
            erNorskStatsborger -> ja()
            else -> nei("Brukeren er ikke norsk statsborger")
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerNorskStatsborgerskapRegel {
            return HarBrukerNorskStatsborgerskapRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                statsborgerskap = datagrunnlag.pdlpersonhistorikk.statsborgerskap
            )
        }
    }
}
