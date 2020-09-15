package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.sjekkStatsborgerskap

class HarBrukerNordiskStatsborgerskapRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    private val statsborgerskap: List<Statsborgerskap>,
    regelId: RegelId = RegelId.REGEL_11
) : LovvalgRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        val erNorskStatsborger = sjekkStatsborgerskap(statsborgerskap, kontrollPeriodeForPersonhistorikk, { s -> Eøsland.erNordisk(s) })

        return when {
            erNorskStatsborger -> ja()
            else -> nei("Brukeren er ikke norsk statsborger")
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerNordiskStatsborgerskapRegel {
            return HarBrukerNordiskStatsborgerskapRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                statsborgerskap = datagrunnlag.pdlpersonhistorikk.statsborgerskap
            )
        }
    }
}
