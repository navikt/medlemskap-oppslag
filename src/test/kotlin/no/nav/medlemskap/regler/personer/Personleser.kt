package no.nav.medlemskap.regler.personer

import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.domene.Datagrunnlag

object Personleser {
    fun brukerIkkeFolkeregistrertSomBosattINorge() =
        lesDatagrunnlag(DatagrunnlagFil.norsk_Med_Ett_Stillingsforhold_Under_25_Prosent.filnavn)

    private fun lesDatagrunnlag(filnavn: String): Datagrunnlag {
        val res = Personleser::class.java.getResource(filnavn)
        return objectMapper.readValue(res, Datagrunnlag::class.java)
    }

    fun dataGrunnlagFraJson(json: String): Datagrunnlag {
        return objectMapper.readValue(json, Datagrunnlag::class.java)
    }
}

enum class DatagrunnlagFil(val filnavn: String) {
    norsk_Med_Ett_Stillingsforhold_Under_25_Prosent(
        "/testpersoner/regler_for_lovvalg/regel_12/norsk_med_ett_arbeidsforhold_under_25_stillingsprosent_i_periode.json",
    ),
}
