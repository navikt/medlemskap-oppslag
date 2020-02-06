package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.HvisUttrykk.Companion.hvis
import no.nav.medlemskap.regler.common.Funksjoner.inneholder

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

    private val eøsland = listOf("NOR", "SVE", "DEN", "FIN", "ISL", "GER", "FRA") // TODO Osv...

    private val erPersonenEøsStatsborger = Avklaring(
            identifikator = "EØS-1",
            avklaring = "Er personen statsborger i et EØS land?",
            beskrivelse = "",
            operasjon = { sjekkStatsborgerskap(it) }
    )

    private fun sjekkStatsborgerskap(fakta: Fakta): Resultat =
            hvis {
                eøsland inneholder fakta.personensSisteStatsborgerskap()
            } så {
                ja("Personen er statsborger i et EØS-land.")
            } ellers {
                nei("Personen er ikke statsborger i et EØS-land.")
            }
}
