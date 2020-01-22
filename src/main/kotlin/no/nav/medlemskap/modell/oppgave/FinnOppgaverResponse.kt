package no.nav.medlemskap.modell.oppgave

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class FinnOppgaverResponse (
    val antallTreffTotalt: Int,
    val oppgaver: List<Oppgave>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Oppgave (
        val aktivDato: LocalDate,
        val prioritet: Prioritet,
        val status: Status,
        val versjon: Int,
        val tilordnetRessurs: String?,
        val tema: String?,
        val beskrivelse: String?
)

enum class Prioritet {
    HOY, NORM, LAV
}

enum class Status {
    OPPRETTET, AAPNET, UNDER_BEHANDLING, FERDIGSTILT, FEILREGISTRERT
}
