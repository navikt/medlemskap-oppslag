package no.nav.medlemskap.regler.personer

import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.domene.Datagrunnlag

class Personleser {

    private val norsk = "/testpersoner/norske"
    private val ikkeEøs = "/testpersoner/ikke_eos"
    private val variertDatagrunnlag = "/testpersoner/variertDatagrunnlagIPeriode"
    private val variertDatagrunnlagArbeidsforhold = "/testpersoner/variertDatagrunnlagIPeriode/arbeidsforhold"

    fun enkelNorsk() = lesDatagrunnlag("$norsk/kun_enkelt_statsborgerskap.json")
    fun enkelNorskArbeid() = lesDatagrunnlag("$norsk/norsk_jobb_ikke_maritim_eller_pilot.json")
    fun enkelNorskMaritim() = lesDatagrunnlag("$norsk/norsk_jobb_maritim_norsk_skip.json")
    fun enkelNorskPilot() = lesDatagrunnlag("$norsk/norsk_jobb_ikke_maritim_men_pilot.json")
    fun enkelNorskUtenlandskSkip() = lesDatagrunnlag("$norsk/norsk_jobb_maritim_utenlandsk_skip.json")
    fun norskDobbeltStatsborgerskap() = lesDatagrunnlag("$norsk/dobbelt_statsborgerskap.json")
    fun norskBosattIUtland() = lesDatagrunnlag("$norsk/norsk_bosatt_i_utland.json")

    fun norskMedFlereStatsborgerskap() = lesDatagrunnlag("$variertDatagrunnlag/norsk_flere_statsborgerskap_i_periode.json")
    fun norskMedUlikeEøsStatsborgerskap() = lesDatagrunnlag("$variertDatagrunnlag/norsk_ulike_eøs_statsborgerskap.json")
    fun norskMedFlereStatsborgerskapIogUtenforPeriode() = lesDatagrunnlag("$variertDatagrunnlag/norsk_flere_statsborgerskap_i_og_utenfor_periode.json")
    fun norskMedUtgaattStatsborgerskapIPeriode() = lesDatagrunnlag("$variertDatagrunnlag/norsk_med_utgaatt_statsborgerskap_i_periode.json")


    fun norskMedEttArbeidsforholdIPeriode() = lesDatagrunnlag("$variertDatagrunnlagArbeidsforhold/norsk_med_ett_arbeidsforhold_i_periode.json")
    fun norskMedEttArbeidsforholdMedPrivatpersonSomArbeidsgiverIPeriode() = lesDatagrunnlag("$variertDatagrunnlagArbeidsforhold/norsk_med_ett_arbeidsforhold_til_arbeidsgiver_som_er_privatperson_i_periode.json")
    fun norskMedFlereArbeidsforholdHvoravEnArbeidsgiverMedKun4AnsatteIPeriode() = lesDatagrunnlag("$variertDatagrunnlagArbeidsforhold/norsk_med_flere_arbeidsforhold_hvorav_en_arbeidsgiver_med_kun_4_ansatte_i_periode.json")
    fun norskMedToUsammenhengendeArbeidsforholdIPeriode() = lesDatagrunnlag("$variertDatagrunnlagArbeidsforhold/norsk_med_to_usammenhengende_arbeidsforhold_i_periode.json")
    fun norskMedToSammenhengendeArbeidsforholdIPeriode() = lesDatagrunnlag("$variertDatagrunnlagArbeidsforhold/norsk_med_to_sammenhengende_arbeidsforhold_i_periode.json")
    fun norskMedToSammenhengendeArbeidsforholdUnder12MndIPeriode() = lesDatagrunnlag("$variertDatagrunnlagArbeidsforhold/norsk_med_to_sammenhengende_arbeidsforhold_under_12_mnd_i_periode.json")
    fun norskMedOverlappendeArbeidsforholdIPeriode() = lesDatagrunnlag("$variertDatagrunnlagArbeidsforhold/norsk_med_overlappende_arbeidsforhold_i_periode.json")
    fun norskMedOverTiArbeidsforhold() = lesDatagrunnlag("$variertDatagrunnlagArbeidsforhold/norsk_med_over_ti_arbeidsforhold_i_periode.json")
    fun norskMedEttArbeidsforholdMedArbeidsavtaleUnder25ProsentStillingIPeriode() = lesDatagrunnlag("$variertDatagrunnlagArbeidsforhold/norsk_med_ett_arbeidsforhold_under_25_stillingsprosent_i_periode.json")
    fun norskMedToArbeidsavtalerISammeArbeidsforholdMedForLavTotalStillingProsentIPeriode() = lesDatagrunnlag("$variertDatagrunnlagArbeidsforhold/norsk_med_to_arbeidsavtaler_til_sammen_under_25_stillingsprosent_i_periode.json")
    fun norskMedToOverlappendeArbeidsavtalerSomTilSammenErOver25ProsentIPeriode() = lesDatagrunnlag("$variertDatagrunnlagArbeidsforhold/norsk_med_to_overlappende_arbeidsavtaler_til_sammen_er_over_25_stillingsprosent_i_periode.json")
    fun norskMedFlereArbeidsforholdstyperIPerioder() = lesDatagrunnlag("$variertDatagrunnlagArbeidsforhold/norsk_og_flere_arbeidsforholdtyper_i_periode.json")
    fun norskMedFlereYrkeskoderIPeriode() = lesDatagrunnlag("$variertDatagrunnlagArbeidsforhold/norsk_med_flere_yrkestyper_i_periode.json")
    fun enkelNorskFlereArbeidsforholdIPeriode() = lesDatagrunnlag("$variertDatagrunnlagArbeidsforhold/norsk_med_flere_yrkestyper_i_periode.json")
    fun enkelNorskMedKonkursArbeidsgiver() = lesDatagrunnlag("$variertDatagrunnlagArbeidsforhold/norsk_med_konkurs_arbeidsgiver.json")


    fun enkelAmerikansk() = lesDatagrunnlag("$ikkeEøs/kun_enkelt_amerikansk_statsborgerskap.json")
    fun amerikanskMedl() = lesDatagrunnlag("$ikkeEøs/amerikansk_med_vedtak_i_medl.json")
    fun amerikanskGosys() = lesDatagrunnlag("$ikkeEøs/amerikansk_med_oppgave_i_gosys.json")
    fun amerikanskJoark() = lesDatagrunnlag("$ikkeEøs/amerikansk_med_dokument_i_joark.json")

    private fun lesDatagrunnlag(filnavn: String): Datagrunnlag {
        val res = Personleser::class.java.getResource(filnavn)
        return objectMapper.readValue(res, Datagrunnlag::class.java)
    }


}
