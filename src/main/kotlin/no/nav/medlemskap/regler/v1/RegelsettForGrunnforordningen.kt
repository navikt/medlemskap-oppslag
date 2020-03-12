package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.common.Avklaring
import no.nav.medlemskap.regler.common.Funksjoner.harAlle
import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.common.Regelsett
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.uttrykk.HvisUttrykk.Companion.hvis

class RegelsettForGrunnforordningen : Regelsett("Regelsett for grunnforordningen") {

    override val KONKLUSJON_IDENTIFIKATOR: String get() = "EØS"
    override val KONKLUSJON_AVKLARING: String get() = "Er brukeren omfattet av Grunnforordningen?"

    override fun evaluer(personfakta: Personfakta): Resultat {
        val resultat =
                avklar {
                    personfakta oppfyller erPersonenEøsStatsborger
                } hvisJa {
                    konkluderMed(ja("Brukeren er omfattet av Grunnforordningen"))
                } hvisNei {
                    konkluderMed(nei("Brukeren er ikke omfattet av Grunnforordningen"))
                }

        return hentUtKonklusjon(resultat)
    }


    private val erPersonenEøsStatsborger = Avklaring(
            identifikator = "EØS-1",
            avklaring = "Er brukeren statsborger i et EØS land?",
            beskrivelse = "For å avklare om bruker er omfattet av grunnforordningen",
            operasjon = { sjekkStatsborgerskap(it) }
    )

    private fun sjekkStatsborgerskap(personfakta: Personfakta): Resultat =
            hvis {
                eøsLand harAlle personfakta.hentStatsborgerskapIPeriode()
            } så {
                ja("Brukeren er statsborger i et EØS-land.")
            } ellers {
                nei("Brukeren er ikke statsborger i et EØS-land.")
            }


    val eøsLand = mapOf(
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
