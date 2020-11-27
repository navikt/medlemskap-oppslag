package no.nav.medlemskap.regler.v1.overstyring

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.v1.lovvalg.LovvalgRegel
import java.time.LocalDate

class OverstyringRegel(
    ytelse: Ytelse,
    private val brukerInput: Brukerinput,
    private val periode: InputPeriode,
    førsteDagForYtelse: LocalDate?,
    private val statsborgerskap: List<Statsborgerskap>,
    regelId: RegelId = RegelId.REGEL_0_2
) : LovvalgRegel(regelId, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {
        if (!brukerInput.arbeidUtenforNorge &&
            ytelse == Ytelse.SYKEPENGER &&
            erBrukerNorskStatsborger(statsborgerskap)
        ) {
            return ja(regelId)
        }

        return nei()
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): OverstyringRegel {
            return OverstyringRegel(
                ytelse = datagrunnlag.ytelse,
                brukerInput = datagrunnlag.brukerinput,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                statsborgerskap = datagrunnlag.pdlpersonhistorikk.statsborgerskap
            )
        }

        fun reglerSomSkalOverstyres(): Map<RegelId, Svar> {
            val overstyringer = HashMap<RegelId, Svar>()

            overstyringer.put(RegelId.REGEL_3, Svar.JA)
            overstyringer.put(RegelId.REGEL_5, Svar.JA)
            overstyringer.put(RegelId.REGEL_12, Svar.JA)

            return overstyringer
        }
    }
}
