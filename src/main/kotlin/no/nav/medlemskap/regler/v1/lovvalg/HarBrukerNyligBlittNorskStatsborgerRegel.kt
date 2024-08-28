package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap.Companion.harNyligBlittNorskStatsborger
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import java.time.LocalDate

class HarBrukerNyligBlittNorskStatsborgerRegel(
    ytelse: Ytelse,
    startDatoForYtelse: LocalDate,
    private val statsborgerskap: List<Statsborgerskap>,
    regelId: RegelId = RegelId.REGEL_27,
) : LovvalgRegel(regelId, ytelse, startDatoForYtelse) {
    override fun operasjon(): Resultat {
        return when {
            statsborgerskap.harNyligBlittNorskStatsborger(kontrollPeriodeForPersonhistorikk) -> ja(regelId)
            else -> nei(regelId)
        }
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerNyligBlittNorskStatsborgerRegel {
            return HarBrukerNyligBlittNorskStatsborgerRegel(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                statsborgerskap = datagrunnlag.pdlpersonhistorikk.statsborgerskap,
            )
        }
    }
}
