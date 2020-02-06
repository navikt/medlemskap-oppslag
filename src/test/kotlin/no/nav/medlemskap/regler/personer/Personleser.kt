package no.nav.medlemskap.regler.personer

import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.domene.Datagrunnlag

class Personleser {

    private val norsk = "/testpersoner/norske"
    private val ikkeEøs = "/testpersoner/ikke_eos"

    fun enkelNorsk() = lesDatagrunnlag("$norsk/kun_enkelt_statsborgerskap.json")
    fun enkelNorskArbeid() = lesDatagrunnlag("$norsk/norsk_jobb_ikke_maritim_eller_pilot.json")
    fun enkelNorskMaritim() = lesDatagrunnlag("$norsk/norsk_jobb_maritim_norsk_skip.json")
    fun enkelNorskPilot() = lesDatagrunnlag("$norsk/norsk_jobb_ikke_maritim_men_pilot.json")
    fun enkelNorskUtenlandskSkip() = lesDatagrunnlag("$norsk/norsk_jobb_maritim_utenlandsk_skip.json")

    fun enkelAmerikansk() = lesDatagrunnlag("$ikkeEøs/kun_enkelt_amerikansk_statsborgerskap.json")
    fun amerikanskMedl() = lesDatagrunnlag("$ikkeEøs/amerikansk_med_vedtak_i_medl.json")
    fun amerikanskGosys() = lesDatagrunnlag("$ikkeEøs/amerikansk_med_oppgave_i_gosys.json")
    fun amerikanskJoark() = lesDatagrunnlag("$ikkeEøs/amerikansk_med_dokument_i_joark.json")

    private fun lesDatagrunnlag(filnavn: String): Datagrunnlag {
        val res = Personleser::class.java.getResource(filnavn)
        return objectMapper.readValue(res, Datagrunnlag::class.java)
    }

}
