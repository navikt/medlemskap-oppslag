package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.HvisUttrykk.Companion.hvis
import no.nav.medlemskap.regler.common.Funksjoner.inneholder
import java.util.stream.Collectors.toList

class RegelsettForEøsforordningen(fakta: Fakta) : Regelsett("Regelsett for EØS forordningen", fakta) {

    override val KONKLUSJON_IDENTIFIKATOR: String get() = "EØS"
    override val KONKLUSJON_AVKLARING: String get() = "Er personen omfattet av EØS-forordningen?"

    override fun evaluer(): Resultat {
        val resultat =
                avklar {
                    erPersonenEøsStatsborger evaluerMed fakta
                } hvisJa {
                    konkluderMed(ja("Personen er omfattet av EØS-ordningen"))
                } hvisNei {
                    konkluderMed(nei("Personen er ikke omfattet av EØS-ordningen"))
                }

        return hentUtKonklusjon(resultat)
    }


    private val erPersonenEøsStatsborger = Avklaring(
            identifikator = "EØS-1",
            avklaring = "Er personen statsborger i et EØS land?",
            beskrivelse = "",
            operasjon = { sjekkStatsborgerskap(it) }
    )

    private fun sjekkStatsborgerskap(fakta: Fakta): Resultat =
            hvis {
                eøsLand inneholder fakta.personensSisteStatsborgerskap()
            } så {
                ja("Personen er statsborger i et EØS-land.")
            } ellers {
                nei("Personen er ikke statsborger i et EØS-land.")
            }


    private val eøsLand = mapOf(
            "BEL" to "BELGIA",
            "BGR" to "BULGARIA",
            "DNK" to "DANMAKR",
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
            "DEU" to "TYSKAND",
            "HUN" to "UNGARN",
            "AUT" to "ØSTERRIKE"
    )

        //Tsjekkia?
        //Spesielt FOR storbritania/Nord irland ut 2020

}
