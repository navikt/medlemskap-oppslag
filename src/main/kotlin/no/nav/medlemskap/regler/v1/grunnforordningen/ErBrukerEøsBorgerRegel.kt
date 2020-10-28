package no.nav.medlemskap.regler.v1.grunnforordningen

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.registrerStatsborgerskapGrafana
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.sjekkStatsborgerskap
import java.time.LocalDate

class ErBrukerEøsBorgerRegel(
    ytelse: Ytelse,
    val periode: InputPeriode,
    val førsteDagForYtelse: LocalDate?,
    val statsborgerskap: List<Statsborgerskap>
) : BasisRegel(RegelId.REGEL_2, ytelse) {

    override fun operasjon(): Resultat {
        val kontrollPeriodeForStatsborgerskap = Datohjelper(periode, førsteDagForYtelse, ytelse).kontrollPeriodeForPersonhistorikk()
        val erBrukerEøsBorger = sjekkStatsborgerskap(statsborgerskap, kontrollPeriodeForStatsborgerskap, { s -> Eøsland.erEØSland(s) })

        if (erBrukerEøsBorger) {
            return ja()
        } else {
            statsborgerskap.registrerStatsborgerskapGrafana(kontrollPeriodeForStatsborgerskap, ytelse, RegelId.REGEL_2)
            return nei("Brukeren er ikke statsborger i et EØS-land.")
        }
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErBrukerEøsBorgerRegel {
            return ErBrukerEøsBorgerRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                statsborgerskap = datagrunnlag.pdlpersonhistorikk.statsborgerskap
            )
        }
    }
}
