package no.nav.medlemskap.domene

import java.time.LocalDate

data class Oppgave (
        val aktivDato: LocalDate,
        val prioritet: Prioritet,
        val status: Status,
        val tema: String?
)

enum class Prioritet {
    HOY, NORM, LAV
}

enum class Status {
    OPPRETTET, AAPNET, UNDER_BEHANDLING, FERDIGSTILT, FEILREGISTRERT
}
