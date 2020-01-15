package no.nav.medlemskap.modell

import java.time.LocalDate

data class Request (
        val fnr: String,
        val oppholdNorgeNeste12: Boolean?,
        val soknadsperiodeStart : LocalDate,
        val soknadsperiodeSlutt: LocalDate,
        val soknadstidspunkt: LocalDate
)
