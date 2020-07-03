package no.nav.medlemskap.regler.personer

import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.personer.Personleser.Companion.ikkeEøs
import no.nav.medlemskap.regler.personer.Personleser.Companion.norsk

class Personleser {

    private val hovedregler = "/testpersoner/hovedregler"
    private val norskLovvalg = "/testpersoner/regler_for_lovvalg"
    private val arbeidsforhold = "/testpersoner/regler_for_arbeidsforhold"
    private val grunnforordningen = "/testpersoner/regler_for_grunnforordningen"
    private val medl = "/testpersoner/regler_for_medl"

    /*Hovedregler*/

    fun norskMedMedlOpplysninger() = lesDatagrunnlag("$hovedregler/norsk_med_medl_opplysninger.json")
    fun amerikanskUtenMedlOpplysninger() = lesDatagrunnlag("$hovedregler/amerikansk_uten_medl_opplysninger.json")

    /*Regelsett for medl (utkommentert versjon)*/

    //Regel A -  "Finnes det noe på personen i MEDL?"
    fun amerikanskMedl() = lesDatagrunnlag("$medl/regel_A/amerikansk_med_vedtak_i_medl.json")
    fun norskMedOpplysningerIMedl() = lesDatagrunnlag("$medl/regel_A/norsk_med_opplysninger_i_medl.json")

    //Regel B - "Finnes det åpne oppgaver i GOSYS på medlemskapsområdet?"
    fun amerikanskGosys() = lesDatagrunnlag("$medl/regel_B/amerikansk_med_oppgave_i_gosys.json")

    //Regel 1.1 -  "Er det periode både med og uten medlemskap innenfor 12 mnd?"
    fun amerikanskMedOgUtenMedlemskap() = lesDatagrunnlag("$medl/regel_1_1/amerikansk_med_og_uten_medlemskap_innenfor_periode.json")

    //Regel 1.2 "Er det en periode med medlemskap?"
    fun brukerHarPeriodeMedMedlemskap() = lesDatagrunnlag("$medl/regel_1_2/perioder_i_medl.json")
    fun amerikanskUtenMedlemskapLovvalgStatuskodeUavklart() = lesDatagrunnlag("$medl/regel_1_2/amerikansk_uten_medlemskap_lovvalg_statuskode_uavk.json")

    //Regel 1.2.1 - "Er hele perioden uten medlemskap innenfor 12-måneders perioden?"
    fun norskUtenMedlemskapIMedl() = lesDatagrunnlag("$medl/regel_1_2_1/norsk_uten_medlemskap_i_medl.json")
    fun amerikanskUtenMedlemskapOver12MndPeriode() = lesDatagrunnlag("$medl/regel_1_2_1/amerikansk_uten_medlemskap_over_12_mnd_periode.json")
    fun norskUtenMedlemskapDelvisInnenfor12MndPeriode() = lesDatagrunnlag("$medl/regel_1_2_1/norsk_uten_medlemskap_medl_delvis_innenfor_12_mnd_periode.json")

    //Regel 1.2.2 - "Er bruker uten medlemskap sin situasjon uendret?"
    fun amerikanskUtenMedlemskapEndretArbeidsforhold() = lesDatagrunnlag("$medl/regel_1_2_2/amerikansk_uten_medlemskap_endret_arbeidsforhold.json")
    fun amerikanskUtenMedlemskapEndretArbeidsforholdSammeArbeidsgiver() = lesDatagrunnlag("$medl/regel_1_2_2/amerikansk_uten_medlemskap_endret_arbeidsforhold_samme_arbeidsgiver.json")
    fun amerikanskUtenMedlemskapUtenArbeidsforhold() = lesDatagrunnlag("$medl/regel_1_2_2/amerikansk_uten_medlemskap_uten_arbeidsforhold.json")

    //Regel 1.2.3 - "Er bruker uten medlemskap sin situasjon uendret?"
    fun amerikanskUtenMedlemskapEndretAdresse() = lesDatagrunnlag("$medl/regel_1_2_3/amerikansk_uten_medlemskap_endret_adresse.json")
    fun amerikanskUtenMedlemskapSammeArbeidsforholdOgAdresse() = lesDatagrunnlag("$medl/regel_1_2_3/amerikansk_uten_medlemskap_samme_arbeidsforhold_og_adresse.json")

    //Regel 1.3 - "Er hele perioden med medlemskap innenfor 12-måneders perioden?"
    fun amerikanskMedMedlemskapOver12MndPeriode() = lesDatagrunnlag("$medl/regel_1_3/amerikansk_med_medlemskap_over_12_mnd_periode.json")
    fun amerikanskMedMedlemskapUgyldigDato() = lesDatagrunnlag("$medl/regel_1_3/amerikansk_med_ugyldig_medlemskapsdato.json")
    fun amerikanskMedMedlemskapToOverlappendePerioder() = lesDatagrunnlag("$medl/regel_1_3/amerikansk_med_medlemskap_to_overlappende_perioder.json")
    fun amerikanskMedMedlemskapToUsammenhengendePerioder() = lesDatagrunnlag("$medl/regel_1_3/amerikansk_med_medlemskap_to_usammenhengende_perioder.json")
    fun amerikanskMedMedlemskapAvsluttetIGittInputPeriode() = lesDatagrunnlag("$medl/regel_1_3/amerikansk_med_medlemskap_avsluttet_i_gitt_input_periode.json")
    fun amerikanskMedMedlemskapToSammenhengendePerioder() = lesDatagrunnlag("$medl/regel_1_3/amerikansk_med_medlemskap_to_sammenhengende_perioder.json")
    fun norskMedMedlemskapDelvisInnenfor12MndPeriode() = lesDatagrunnlag("$medl/regel_1_3/norsk_med_medlemskap_medl_delvis_innenfor_12_mnd_periode.json")

    //Regel 1.4 - "Er brukers situasjon uendret?"
    fun amerikanskMedMedlemskapEndretArbeidsforhold() = lesDatagrunnlag("$medl/regel_1_4/amerikansk_med_medlemskap_endret_arbeidsforhold.json")

    //Regel 1.5 -  "Er brukers situasjon uendret?"
    fun amerikanskMedMedlemskapEndretAdresse() = lesDatagrunnlag("$medl/regel_1_5/amerikansk_med_medlemskap_endret_adresse.json")
    fun amerikanskMedMedlemskapSammeArbeidsforholdOgAdresse() = lesDatagrunnlag("$medl/regel_1_5/amerikansk_med_medlemskap_samme_arbeidsforhold_og_adresse.json")

    //Regel 1.6 - "Har bruker et medlemskap som omfatter ytelse? (Dekning i MEDL)"
    fun amerikanskMedMedlemskapMedDekningIMedl() = lesDatagrunnlag("$medl/regel_1_6/amerikansk_med_medlemskap_med_dekning_i_medl.json")
    fun amerikanskMedMedlemskapUtenDekningIMedl() = lesDatagrunnlag("$medl/regel_1_6/amerikansk_med_medlemskap_uten_dekning_i_medl.json")

    /*  Regler for grunnforodningen*/

    //Regel 2 - "Er bruker omfattet av grunnforordningen (EØS)? Dvs er bruker statsborger i et EØS-land inkl. Norge?"
    fun brukerErStatsborgerIEØSLand() = lesDatagrunnlag("$grunnforordningen/medlem_i_eøs_land.json")
    fun brukerErIkkeStatsborgerIEØSLand() = lesDatagrunnlag("$grunnforordningen/ikke_medlem_i_eøs_land.json")

   /* Regelsett for arbeidsforhold*/

    //Regel 3 -"Har bruker hatt et sammenhengende arbeidsforhold i Aa-registeret de siste 12 månedene?"
    fun norskMedOverlappendeArbeidsforholdIPeriode() = lesDatagrunnlag("$arbeidsforhold/regel_3/norsk_med_overlappende_arbeidsforhold_i_periode.json")
    fun norskMedEttArbeidsforholdUnder12MndIPeriode() = lesDatagrunnlag("$arbeidsforhold/regel_3/norsk_med_for_kort_arbeidsforhold.json")
    fun norskMedToSammenhengendeArbeidsforholdUnder12MndIPeriode() = lesDatagrunnlag("$arbeidsforhold/regel_3/norsk_med_to_sammenhengende_arbeidsforhold_under_12_mnd_i_periode.json")
    fun norskMedToUsammenhengendeArbeidsforholdIPeriode() = lesDatagrunnlag("$arbeidsforhold/regel_3/norsk_med_to_usammenhengende_arbeidsforhold_i_periode.json")
    fun norskMedOverTiArbeidsforhold() = lesDatagrunnlag("$arbeidsforhold/regel_3/norsk_med_over_ti_arbeidsforhold_i_periode.json")

    //Regel 4 - "Er foretaket registrert i foretaksregisteret?"
    fun norskMedEttArbeidsforholdMedPrivatpersonSomArbeidsgiverIPeriode() = lesDatagrunnlag("$arbeidsforhold/regel_4/norsk_med_ett_arbeidsforhold_til_arbeidsgiver_som_er_privatperson_i_periode.json")

    //Regel 5 -  "Har arbeidsgiver sin hovedaktivitet i Norge?"
    fun norskMedFlereArbeidsforholdHvoravEnArbeidsgiverMedKun4AnsatteIPeriode() = lesDatagrunnlag("$arbeidsforhold/regel_5/norsk_med_flere_arbeidsforhold_hvorav_en_arbeidsgiver_med_kun_4_ansatte_i_periode.json")

    //Regel 6 - "Er foretaket aktivt?"
    fun enkelNorskMedKonkursArbeidsgiver() = lesDatagrunnlag("$arbeidsforhold/regel_6/norsk_med_konkurs_arbeidsgiver.json")

    //Regel 7 - "Er arbeidsforholdet maritimt?"
    fun norskMedFlereArbeidsforholdstyperIPerioder() = lesDatagrunnlag("$arbeidsforhold/regel_7/norsk_og_flere_arbeidsforholdtyper_i_periode.json")
    fun enkelNorskMaritim() = lesDatagrunnlag("$arbeidsforhold/regel_7/norsk_jobb_maritim_norsk_skip.json")

    //Regel 7.1 - "Er bruker ansatt på et NOR-skip?"
    fun brukerArbeiderPaaNISSkip() = lesDatagrunnlag("$arbeidsforhold/regel_7_1/norsk_jobb_maritim_utenlandsk_skip.json")
    fun brukerArbeiderPaaNORSkip() = lesDatagrunnlag("$arbeidsforhold/regel_7_1/norsk_jobb_maritim_norsk_skip.json")

    //Regel 8 - "Er bruker pilot eller kabinansatt?"
    fun brukerErIkkePilotEllerKabinansatt() = lesDatagrunnlag("$arbeidsforhold/regel_8/norsk_jobb_ikke_maritim_eller_pilot.json")
    fun norskMedFlereYrkeskoderIPeriode() = lesDatagrunnlag("$arbeidsforhold/regel_8/norsk_med_flere_yrkestyper_i_periode.json")
    fun brukerErPilot() = lesDatagrunnlag("$arbeidsforhold/regel_8/norsk_jobb_ikke_maritim_men_pilot.json")

    /*  Regelsett norsk lovvalg */

    //Regel 9 - "Har bruker utført arbeid utenfor Norge?"
    fun brukerHarJobbetUtenforNorge() = lesDatagrunnlag("$norskLovvalg/regel_9/bruker_har_jobbet_utenfor_norge.json")
    fun brukerHarIkkeJobbetUtenforNorge() = lesDatagrunnlag("$norskLovvalg/regel_9/bruker_har_ikke_jobbet_utenfor_norge.json")

    //Regel 10 - "Er bruker folkeregistrert som bosatt i Norge og har vært det i 12 mnd?"
    fun brukerHarKunBostedsadresse() = lesDatagrunnlag("$norskLovvalg/regel_10/kun_bostedsadresse.json")
    fun brukerHarIngenBostedsadresse() = lesDatagrunnlag("$norskLovvalg/regel_10/ingen_bostedsadresse.json")
    fun brukerHarAlleAdresserNorsk() = lesDatagrunnlag("$norskLovvalg/regel_10/alle_adresser_er_norsk.json")
    fun brukerHarPostadresseIUtland() = lesDatagrunnlag("$norskLovvalg/regel_10/postadresse_i_utland.json")
    fun brukerHarMidlertidigAdresseIUtland() = lesDatagrunnlag("$norskLovvalg/regel_10/midlertidig_adresse_i_utland.json")

    //Regel 11 - "Er bruker norsk statsborger?",
    fun brukerErNorskStatsborger() = lesDatagrunnlag("$norskLovvalg/regel_11/norsk_statsborger.json")
    fun brukerErIkkeNorskStatsborger() = lesDatagrunnlag("$norskLovvalg/regel_11/utenlands_statsborger.json")

    //Regel 12 - "Har bruker vært i minst 25% stilling de siste 12 mnd?"
    fun norskMedToOverlappendeArbeidsavtalerSomTilSammenErOver25ProsentIPeriode() = lesDatagrunnlag("$norskLovvalg/regel_12/norsk_med_to_overlappende_arbeidsavtaler_til_sammen_er_over_25_stillingsprosent_i_periode.json")
    fun norskMedEttArbeidsforholdMedArbeidsavtaleUnder25ProsentStillingIPeriode() = lesDatagrunnlag("$norskLovvalg/regel_12/norsk_med_ett_arbeidsforhold_under_25_stillingsprosent_i_periode.json")
    fun norskMedToArbeidsavtalerISammeArbeidsforholdMedForLavTotalStillingProsentIPeriode() = lesDatagrunnlag("$norskLovvalg/regel_12/norsk_med_to_arbeidsavtaler_til_sammen_under_25_stillingsprosent_i_periode.json")
    fun norskMedToParallelleArbeidsforholdHvoravEnLavStillingsprosent() = lesDatagrunnlag("$norskLovvalg/regel_12/norsk_med_to_arbeidsforhold_en_heltid_en_deltid.json")
    fun norskMedToSerielleArbeidsforholdHvoravEnLavStillingsprosent() = lesDatagrunnlag("$norskLovvalg/regel_12/norsk_med_to_serielle_arbeidsforhold_hvorav_en_lav_stillingsprosent.json")
    fun norskMedToDelvisOverlappendeArbeidsforholdHvoravEnLavStillingsprosent() = lesDatagrunnlag("$norskLovvalg/regel_12/norsk_med_to_delvis_overlappende_arbeidsforhold_hvorav_en_lav_stillingsprosent.json")
    fun norskMedToSammenhengendeArbeidsforholdIPeriode() = lesDatagrunnlag("$norskLovvalg/regel_12/norsk_med_to_sammenhengende_arbeidsforhold_i_periode.json")

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
