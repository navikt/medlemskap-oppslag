package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.finnesI
import no.nav.medlemskap.regler.common.RegelId.REGEL_2
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.hentStatsborgerskapVedSluttAvKontrollperiode
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.hentStatsborgerskapVedStartAvKontrollperiode
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.registrerStatsborgerskapGrafana

class ReglerForGrunnforordningen(val ytelse: Ytelse, val periode: InputPeriode, val statsborgerskap: List<Statsborgerskap>) : Regler() {
    val kontrollPeriodeForStatsborgerskap = Datohjelper(periode, ytelse).kontrollPeriodeForPersonhistorikk()

    override fun hentHovedRegel() =
            sjekkRegel {
                erBrukerEØSborger
            }

    private val erBrukerEØSborger = Regel(
            regelId = REGEL_2,
            ytelse = ytelse,
            operasjon = { sjekkStatsborgerskap() }
    )

    private fun sjekkStatsborgerskap(): Resultat {
        val førsteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedStartAvKontrollperiode(kontrollPeriodeForStatsborgerskap)
        val sisteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedSluttAvKontrollperiode(kontrollPeriodeForStatsborgerskap)

        if (eøsLand finnesI førsteStatsborgerskap && eøsLand finnesI sisteStatsborgerskap) {
            return ja()
        } else {
            statsborgerskap.registrerStatsborgerskapGrafana(kontrollPeriodeForStatsborgerskap, ytelse, REGEL_2)
            return nei("Brukeren er ikke statsborger i et EØS-land.")
        }
    }

    private val eøsLand = mapOf(
            "BEL" to "BELGIA",
            "BGR" to "BULGARIA",
            "DNK" to "DANMARK",
            "EST" to "ESTLAND",
            "FIN" to "FINLAND",
            "FRA" to "FRANKRIKE",
            "GRC" to "HELLAS",
            "IRL" to "IRLAND",
            "ISL" to "ISLAND",
            "ITA" to "ITALIA",
            "HRV" to "KROATIA",
            "CYP" to "KYPROS",
            "LVA" to "LATVIA",
            "LIE" to "LIECHTENSTEIN",
            "LTU" to "LITAUEN",
            "LUX" to "LUXENBURG",
            "MLT" to "MALTA",
            "NLD" to "NEDERLAND",
            "NOR" to "NORGE",
            "POL" to "POLEN",
            "PRT" to "PORTUGAL",
            "ROU" to "ROMANIA",
            "SVK" to "SLOVAKIA",
            "SVN" to "SLOVENIA",
            "ESP" to "SPANIA",
            "SWE" to "SVERIGE",
            "CZE" to "TSJEKKIA",
            "DEU" to "TYSKAND",
            "HUN" to "UNGARN",
            "AUT" to "ØSTERRIKE"
    )

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForGrunnforordningen {
            return ReglerForGrunnforordningen(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    statsborgerskap = datagrunnlag.personhistorikk.statsborgerskap)
        }
    }
}
