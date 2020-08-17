package no.nav.medlemskap.clients.pdl


data class HentFoedselsaarResponse(val data: HentFoedselsaar?, val errors: List<PdlError>?)

data class HentFoedselsaar(val hentPerson: PdlPerson?)

data class PdlPerson(
        val foedsel: List<Foedsel>
)

data class Foedsel(
        val foedselsaar: Int
)