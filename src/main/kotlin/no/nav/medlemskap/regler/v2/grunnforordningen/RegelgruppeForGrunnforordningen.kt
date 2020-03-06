package no.nav.medlemskap.regler.v2.grunnforordningen

import no.nav.medlemskap.regler.common.Funksjoner.erDelAv
import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.v2.common.*

class RegelgruppeForGrunnforordningen(val personfakta: Personfakta) : Regelgruppe() {

    override fun evaluer() = Spørsmål(
            identifikator = "1.4",
            spørsmål = "Er brukeren omfattet av grunnforordningen, dvs statsborger i et EØS land?",
            beskrivelse = "For å avklare om bruker er omfattet av grunnforordningen",
            operasjon = { sjekkStatsborgerskap() }
    ).utfør()


    private fun sjekkStatsborgerskap(): Svar =
            when {
                personfakta.personensSisteStatsborgerskap() erDelAv eøsLand -> ja("Brukeren er statsborger i et EØS-land.")
                else -> nei("Brukeren er ikke statsborger i et EØS-land.")
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
