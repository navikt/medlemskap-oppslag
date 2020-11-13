package no.nav.medlemskap.regler.v1.grunnforordningen

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.v1.lovvalg.LovvalgRegel
import java.time.LocalDate

class ErRequestGyldigRegel(
    ytelse: Ytelse,
    val periode: InputPeriode,
    val førsteDagForYtelse: LocalDate?,
    val statsborgerskap: List<Statsborgerskap>
) : LovvalgRegel(RegelId.REGEL_0_1, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {
        val erBrukerSveitsiskStatsborgerUtenAnnetEøsStatsborgerskap = erBrukerSveitsiskBorgerUtenAnnetEøsStatsborgerskap(statsborgerskap)
        val førsteGyldigeDato = if (erBrukerSveitsiskStatsborgerUtenAnnetEøsStatsborgerskap) {
            LocalDate.of(2017, 1, 1)
        } else {
            LocalDate.of(2016, 1, 1)
        }

        if (periode.fom.isBefore(førsteGyldigeDato)) {
            return nei("Periode fom kan ikke være før $førsteGyldigeDato")
        }

        if (førsteDagForYtelse?.isBefore(førsteDagForYtelse) ?: false) {
            return nei("Første dag for ytelse kan ikke være før $førsteGyldigeDato")
        }

        return ja()
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErRequestGyldigRegel {
            return ErRequestGyldigRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                statsborgerskap = datagrunnlag.pdlpersonhistorikk.statsborgerskap
            )
        }
    }
}
