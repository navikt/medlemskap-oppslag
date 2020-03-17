package no.nav.medlemskap.regler.v2.regler

import no.nav.medlemskap.regler.common.Funksjoner.harAlle
import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.v2.common.*

class ReglerForGrunnforordningen(val personfakta: Personfakta) : Regler() {

    override fun hentHovedRegel() =
            sjekkRegel {
                erBrukerEØSborger
            }

    private val erBrukerEØSborger = Regel(
            identifikator = "GRUNN-1",
            avklaring = "Er brukeren statsborger i et EØS land?",
            beskrivelse = "For å avklare om bruker er omfattet av grunnforordningen",
            operasjon = { sjekkStatsborgerskap() }
    )

    private fun sjekkStatsborgerskap(): Resultat =
            when {
                eøsLand harAlle personfakta.hentStatsborgerskapIPeriode() -> ja()
                else -> nei("Brukeren er ikke statsborger i et EØS-land(${personfakta.hentStatsborgerskapIPeriode()}).")
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
}
