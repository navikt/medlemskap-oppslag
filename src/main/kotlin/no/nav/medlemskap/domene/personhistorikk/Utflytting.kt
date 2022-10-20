package no.nav.medlemskap.domene.personhistorikk

import java.time.LocalDate

data class Utflytting(
    val tilflyttingsland: String?,
    val tilflyttingsstedIUtlandet: String?,
    val utflyttingsDato: LocalDate?
)
