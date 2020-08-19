package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.hentStatsborgerskapVedSluttAvKontrollperiode
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.hentStatsborgerskapVedStartAvKontrollperiode
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.registrerStatsborgerskapGrafana

class ErBrukerEøsBorgerRegel(
        ytelse: Ytelse,
        val periode: InputPeriode,
        val statsborgerskap: List<Statsborgerskap>
): BasisRegel(RegelId.REGEL_2, ytelse) {

    override fun operasjon(): Resultat {
        val kontrollPeriodeForStatsborgerskap = Datohjelper(periode, ytelse).kontrollPeriodeForPersonhistorikk()
        val førsteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedStartAvKontrollperiode(kontrollPeriodeForStatsborgerskap)
        val sisteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedSluttAvKontrollperiode(kontrollPeriodeForStatsborgerskap)

        if (førsteStatsborgerskap.any{ Eøsland.erEØSland(it) } && sisteStatsborgerskap.any{ Eøsland.erEØSland(it) }) {
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
                    statsborgerskap = datagrunnlag.personhistorikk.statsborgerskap
            )
        }

    }
}