package no.nav.medlemskap.regler.personer

import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.personer.Personleser.Companion.ikkeEøs
import no.nav.medlemskap.regler.personer.Personleser.Companion.norsk

class Personleser {

    private val hovedregler = "/testpersoner/hovedregler"
    private val norskLovvalg = "/testpersoner/regler_for_lovvalg"
    private val arbeidsforhold = "/testpersoner/regler_for_arbeidsforhold"
    private val grunnforordningen = "/testpersoner/regler_for_grunnforordningen"
    private val medl = "/testpersoner/regler_for_medl"

    fun brukerIkkeFolkeregistrertSomBosattINorge() = lesDatagrunnlag("$norskLovvalg/regel_12/norsk_med_ett_arbeidsforhold_under_25_stillingsprosent_i_periode.json")

    private fun lesDatagrunnlag(filnavn: String): Datagrunnlag {
        val res = Personleser::class.java.getResource(filnavn)
        return objectMapper.readValue(res, Datagrunnlag::class.java)
    }

    fun dataGrunnlagFraJson(json: String): Datagrunnlag {
        return objectMapper.readValue(json, Datagrunnlag::class.java)
    }

    companion object {
        val hovedregler = "/testpersoner/hovedregler"
        val norskLovvalg = "/testpersoner/regler_for_lovvalg"
        val arbeidsforhold = "/testpersoner/regler_for_arbeidsforhold"
        val grunnforordningen = "/testpersoner/regler_for_grunnforordningen"
        val medl = "/testpersoner/regler_for_medl"
        val norsk = "/testpersoner/norske"
        val ikkeEøs = "/testpersoner/ikke_eos"
    }
}

enum class DatagrunnlagFil(val filnavn: String) {
    enkelNorsk("$norsk/kun_enkelt_statsborgerskap.json"),
    enkelAmerikansk("$ikkeEøs/kun_enkelt_amerikansk_statsborgerskap.json")
}
