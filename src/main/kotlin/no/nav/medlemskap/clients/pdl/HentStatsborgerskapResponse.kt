package no.nav.medlemskap.clients.pdl

data class HentStatsborgerskapResponse(val data: StatsborgerskapData?, val errors: List<PdlError>?)

data class StatsborgerskapData(val hentPerson: StatsborgerskapHentPerson)

data class StatsborgerskapHentPerson(val statsborgerskap: List<Statsborgerskap>)