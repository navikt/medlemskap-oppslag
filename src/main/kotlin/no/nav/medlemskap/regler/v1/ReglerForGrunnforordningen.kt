package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.finnesI

class ReglerForGrunnforordningen(val personfakta: Personfakta) : Regler() {

    override fun hentHovedRegel() =
            sjekkRegel {
                erBrukerEØSborger
            }

    private val erBrukerEØSborger = Regel(
            identifikator = "GRUNNFORORDNING-EØS",
            avklaring = "Er brukeren statsborger i et EØS land?",
            beskrivelse = """
                Skal sikre at bare brukere som er omfattet av grunnforordningen blir vurdert videre.
                Grunnforordningen er forordning (EF) 883/2004
            """.trimIndent(),
            operasjon = { sjekkStatsborgerskap() }
    )

    private fun sjekkStatsborgerskap(): Resultat {
        val førsteStatsborgerskap = personfakta.hentStatsborgerskapFor(personfakta.FØRSTE_DAG_I_KONTROLLPERIODE)
        val sisteStatsborgerskap = personfakta.hentStatsborgerskapFor(personfakta.SISTE_DAG_I_KONTROLLPERIODE)
        return when {
            eøsLand finnesI førsteStatsborgerskap && eøsLand finnesI sisteStatsborgerskap -> ja()
            else -> nei("Brukeren er ikke statsborger i et EØS-land.")
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
}
