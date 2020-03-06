package no.nav.medlemskap.regler.v2

import com.fasterxml.jackson.annotation.JsonIgnore

data class Spørsmål(
        val identifikator: String,
        val spørsmål: String,
        val beskrivelse: String,
        @JsonIgnore val operasjon: () -> Svar,
        val svar: Svar = uavklart("")
) {
    fun utfør(): Spørsmål = this.copy(svar = operasjon.invoke())
}
