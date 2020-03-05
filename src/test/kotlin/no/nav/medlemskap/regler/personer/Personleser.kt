package no.nav.medlemskap.regler.personer

import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.domene.Datagrunnlag

class Personleser {

    private val norsk = "/testpersoner/norske"
    private val ikkeEøs = "/testpersoner/ikke_eos"
    private val variertDatagrunnlag = "/testpersoner/variertDatagrunnlagiPeriode"

    fun enkelNorsk() = lesDatagrunnlag("$norsk/kun_enkelt_statsborgerskap.json")
    fun enkelNorskArbeid() = lesDatagrunnlag("$norsk/norsk_jobb_ikke_maritim_eller_pilot.json")
    fun enkelNorskMaritim() = lesDatagrunnlag("$norsk/norsk_jobb_maritim_norsk_skip.json")
    fun enkelNorskPilot() = lesDatagrunnlag("$norsk/norsk_jobb_ikke_maritim_men_pilot.json")
    fun enkelNorskUtenlandskSkip() = lesDatagrunnlag("$norsk/norsk_jobb_maritim_utenlandsk_skip.json")

    fun norskMedFlereStatsborgerskap() = lesDatagrunnlag("$variertDatagrunnlag/norsk_flere_statsborgerskap_i_periode.json")
    fun norskMedFlereStatsborgerskapUtenforPeriode() = lesDatagrunnlag("$variertDatagrunnlag/norsk_flere_stasborgerskap_utenfor_periode.json")
    fun norskMedFlereStatsborgerskapIogUtenforPeriode() = lesDatagrunnlag("$variertDatagrunnlag/norsk_flere_statsborgerskap_i_og_utenfor_periode.json")
    fun norskMedUtgaattStatsborgerskapIPeriode() = lesDatagrunnlag("$variertDatagrunnlag/norsk_med_utgaatt_statsborgerskap_i_periode.json")

    fun norskArbeidsgiverMedFlereIPeriode () = lesDatagrunnlag("$variertDatagrunnlag/norsk_og_flere_arbeidsgivere_i_periode.json")
    fun norskMedFlereArbeidsforholdstyperIPerioder() = lesDatagrunnlag("$variertDatagrunnlag/norsk_og_flere_arbeidsforholdtyper_i_periode.json")
    fun norskMedFlereYrkeskoderIPeriode () = lesDatagrunnlag("$variertDatagrunnlag/norsk_med_flere_yrkestyper_i_periode.json")



    fun enkelAmerikansk() = lesDatagrunnlag("$ikkeEøs/kun_enkelt_amerikansk_statsborgerskap.json")
    fun amerikanskMedl() = lesDatagrunnlag("$ikkeEøs/amerikansk_med_vedtak_i_medl.json")
    fun amerikanskGosys() = lesDatagrunnlag("$ikkeEøs/amerikansk_med_oppgave_i_gosys.json")
    fun amerikanskJoark() = lesDatagrunnlag("$ikkeEøs/amerikansk_med_dokument_i_joark.json")

    private fun lesDatagrunnlag(filnavn: String): Datagrunnlag {
        val res = Personleser::class.java.getResource(filnavn)
        return objectMapper.readValue(res, Datagrunnlag::class.java)
    }

}
