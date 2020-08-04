package no.nav.medlemskap.services.pdl

import java.util.*

data class HentNasjonalitetResponse(val data: HentNasjonalitet?, val errors: List<PdlError>?)

data class HentNasjonalitet(val hentNasjonalitet: List<Nasjonalitet>)

data class Nasjonalitet(
        val land: String,
        val gyldigFraOgMed: Date,
        val gyldigTilOgMed: Date?
    )