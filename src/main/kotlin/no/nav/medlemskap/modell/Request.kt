package no.nav.medlemskap.modell
import no.nav.medlemskap.domene.Brukerinput
import java.time.LocalDate

data class Request (
        val fnr: String,
        val aktoerId: String, //Todo: Fjern, for å test mot journal/oppgaver
        val oppholdNorgeNeste12: Boolean?,
        val soknadsperiodeStart : LocalDate,
        val soknadsperiodeSlutt: LocalDate,
        val soknadstidspunkt: LocalDate,
        val brukerinput: Brukerinput
)
