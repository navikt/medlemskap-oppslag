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

    /*Hovedregler*/

    fun norskMedMedlOpplysninger() = lesDatagrunnlag("$hovedregler/norsk_med_medl_opplysninger.json")
    fun amerikanskUtenMedlOpplysninger() = lesDatagrunnlag("$hovedregler/amerikansk_uten_medl_opplysninger.json")

    /*  Regler for grunnforodningen*/

    // Regel 2 - "Er bruker omfattet av grunnforordningen (EØS)? Dvs er bruker statsborger i et EØS-land inkl. Norge?"
    fun brukerErStatsborgerIEØSLand() = lesDatagrunnlag("$grunnforordningen/medlem_i_eøs_land.json")
    fun brukerErIkkeStatsborgerIEØSLand() = lesDatagrunnlag("$grunnforordningen/ikke_medlem_i_eøs_land.json")

    /* Regelsett for arbeidsforhold*/

    // Regel 3 -"Har bruker hatt et sammenhengende arbeidsforhold i Aa-registeret de siste 12 månedene?"
    fun norskMedOverlappendeArbeidsforholdIPeriode() = lesDatagrunnlag("$arbeidsforhold/regel_3/norsk_med_overlappende_arbeidsforhold_i_periode.json")
    fun norskMedEttArbeidsforholdUnder12MndIPeriode() = lesDatagrunnlag("$arbeidsforhold/regel_3/norsk_med_for_kort_arbeidsforhold.json")
    fun norskMedToSammenhengendeArbeidsforholdUnder12MndIPeriode() = lesDatagrunnlag("$arbeidsforhold/regel_3/norsk_med_to_sammenhengende_arbeidsforhold_under_12_mnd_i_periode.json")
    fun norskMedToUsammenhengendeArbeidsforholdIPeriode() = lesDatagrunnlag("$arbeidsforhold/regel_3/norsk_med_to_usammenhengende_arbeidsforhold_i_periode.json")
    fun norskMedOverTiArbeidsforhold() = lesDatagrunnlag("$arbeidsforhold/regel_3/norsk_med_over_ti_arbeidsforhold_i_periode.json")

    // Regel 4 - "Er foretaket registrert i foretaksregisteret?"
    fun norskMedEttArbeidsforholdMedPrivatpersonSomArbeidsgiverIPeriode() = lesDatagrunnlag("$arbeidsforhold/regel_4/norsk_med_ett_arbeidsforhold_til_arbeidsgiver_som_er_privatperson_i_periode.json")

    // Regel 5 -  "Har arbeidsgiver sin hovedaktivitet i Norge?"
    fun norskMedFlereArbeidsforholdHvoravEnArbeidsgiverMedKun4AnsatteIPeriode() = lesDatagrunnlag("$arbeidsforhold/regel_5/norsk_med_flere_arbeidsforhold_hvorav_en_arbeidsgiver_med_kun_4_ansatte_i_periode.json")

    // Regel 6 - "Er foretaket aktivt?"
    fun enkelNorskMedKonkursArbeidsgiver() = lesDatagrunnlag("$arbeidsforhold/regel_6/norsk_med_konkurs_arbeidsgiver.json")

    // Regel 7 - "Er arbeidsforholdet maritimt?"
    fun norskMedFlereArbeidsforholdstyperIPerioder() = lesDatagrunnlag("$arbeidsforhold/regel_7/norsk_og_flere_arbeidsforholdtyper_i_periode.json")
    fun enkelNorskMaritim() = lesDatagrunnlag("$arbeidsforhold/regel_7/norsk_jobb_maritim_norsk_skip.json")

    // Regel 7.1 - "Er bruker ansatt på et NOR-skip?"
    fun brukerArbeiderPaaNISSkip() = lesDatagrunnlag("$arbeidsforhold/regel_7_1/norsk_jobb_maritim_utenlandsk_skip.json")
    fun brukerArbeiderPaaNORSkip() = lesDatagrunnlag("$arbeidsforhold/regel_7_1/norsk_jobb_maritim_norsk_skip.json")

    // Regel 8 - "Er bruker pilot eller kabinansatt?"
    fun brukerErIkkePilotEllerKabinansatt() = lesDatagrunnlag("$arbeidsforhold/regel_8/norsk_jobb_ikke_maritim_eller_pilot.json")
    fun norskMedFlereYrkeskoderIPeriode() = lesDatagrunnlag("$arbeidsforhold/regel_8/norsk_med_flere_yrkestyper_i_periode.json")
    fun brukerErPilot() = lesDatagrunnlag("$arbeidsforhold/regel_8/norsk_jobb_ikke_maritim_men_pilot.json")

    /*  Regelsett norsk lovvalg */

    // Regel 9 - "Har bruker utført arbeid utenfor Norge?"
    fun brukerHarJobbetUtenforNorge() = lesDatagrunnlag("$norskLovvalg/regel_9/bruker_har_jobbet_utenfor_norge.json")
    fun brukerHarIkkeJobbetUtenforNorge() = lesDatagrunnlag("$norskLovvalg/regel_9/bruker_har_ikke_jobbet_utenfor_norge.json")

    // Regel 10 - "Er bruker folkeregistrert som bosatt i Norge og har vært det i 12 mnd?"
    fun brukerHarKunBostedsadresse() = lesDatagrunnlag("$norskLovvalg/regel_10/kun_bostedsadresse.json")
    fun brukerHarIngenBostedsadresse() = lesDatagrunnlag("$norskLovvalg/regel_10/ingen_bostedsadresse.json")
    fun brukerHarAlleAdresserNorsk() = lesDatagrunnlag("$norskLovvalg/regel_10/alle_adresser_er_norsk.json")
    fun brukerHarPostadresseIUtland() = lesDatagrunnlag("$norskLovvalg/regel_10/postadresse_i_utland.json")
    fun brukerHarMidlertidigAdresseIUtland() = lesDatagrunnlag("$norskLovvalg/regel_10/midlertidig_adresse_i_utland.json")

    // Regel 11 - "Er bruker norsk statsborger?",
    fun brukerErNorskStatsborger() = lesDatagrunnlag("$norskLovvalg/regel_11/norsk_statsborger.json")
    fun brukerErIkkeNorskStatsborger() = lesDatagrunnlag("$norskLovvalg/regel_11/utenlands_statsborger.json")

    // Regel 11.2 "Har bruker ektefelle?"
    fun brukerHarEktefelle() = lesDatagrunnlag("$norskLovvalg/regel_11_2/bruker_har_ektefelle_i_tps.json")
    fun brukerHarIkkeEktefelle() = lesDatagrunnlag("$norskLovvalg/regel_11_2/bruker_har_ikke_ektefelle_i_tps.json")

    // Regel 11.2.1 "Har bruker barn, men ikke ektefelle registrert i TPS?
    fun brukerHarIkkeEktefelleMenBarn() = lesDatagrunnlag("$norskLovvalg/regel_11_2_1/bruker_har_ikke_ektefelle_men_barn.json")
    fun brukerHarIkkeEktefelleOgIkkeBarn() = lesDatagrunnlag("$norskLovvalg/regel_11_2_1/bruker_har_ikke_ektefelle_og_ikke_barn.json")

    // Må finne et fnr å denne, brukte mitt eget og testen gikk gjennom. Har fjernet dette nå
    // fun brukerHarBarnOver25RegnesIkkeSomBarn() = lesDatagrunnlag("$norskLovvalg/regel_11_2_1/bruker_med_barn_over_25_registreres_ikke_som_barn.json")

    // Regel 11.2.2 "Har bruker uten ektefelle folkeregistrerte barn?"
    fun brukerHarIkkeEktefelleMenFolkeregistrerteBarn() = lesDatagrunnlag("$norskLovvalg/regel_11_2_2/bruker_har_ikke_ektefelle_men_folkeregistrerte_barn.json")
    fun brukerHarIkkeEktefelleOgIkkeFolkeregistrerteBarn() = lesDatagrunnlag("$norskLovvalg/regel_11_2_2/bruker_har_ikke_ektefelle_og_ikke_folkeregistrerte_barn.json")

    // Regel 11.2.2.1 "Har bruker uten ektefelle og uten folkeregistrerte barn jobbet mer enn 100 prosent?"
    fun brukerHarIkkeEktefelleOgIkkeFolkeregistrerteBarnHarJobbetMerEnn100rosent() = lesDatagrunnlag("$norskLovvalg/regel_11_2_2_1/bruker_har_ikke_folkeregistrerte_barn_men_jobbet_mer_enn_100_prosent.json")
    fun brukerHarIkkeEktefelleOgIkkeFolkeregistrerteBarnHarIkkeJobbetMerEnn100Prosent() = lesDatagrunnlag("$norskLovvalg/regel_11_2_2_1/bruker_har_ikke_folkeregistrerte_barn_og_har_ikke_jobbet_mer_enn_100_prosent.json")

    // Regel 11.2.3 "Har bruker uten ektefelle men med folkeregistrerte barn jobbet mer enn 80 prosent?"
    fun brukerHarIkkeEktefelleMenMedFolkeregistrerteBarnHarJobbetMerEnn80rosent() = lesDatagrunnlag("$norskLovvalg/regel_11_2_3/bruker_uten_ektefelle_og_folkeregistrerte_barn_har_jobbet_mer_enn_80_prosent.json")
    fun brukerHarIkkeEktefelleMenMedFolkeregistrerteBarnHarIkkeJobbetMerEnn80Prosent() = lesDatagrunnlag("$norskLovvalg/regel_11_2_3/bruker_uten_ektefelle_og_folkeregistrerte_barn_har_ikke_jobbet_mer_enn_80_prosent.json")

    // Regel 11.3 "Har bruker ektefelle og barn registrert i TPS?"
    fun brukerHarEktefelleOgBarn() = lesDatagrunnlag("$norskLovvalg/regel_11_3/bruker_har_ektefelle_og_barn.json")
    fun brukerHarEktefelleMenIkkeBarn() = lesDatagrunnlag("$norskLovvalg/regel_11_3/bruker_har_ektefelle_men_ikke_barn.json")

    // Regel 11.3.1 "Har barnløs brukers ektefelle folkeregistrert adresse?"
    fun brukerUtenBarnHarFolkeregistrertEktefelle() = lesDatagrunnlag("$norskLovvalg/regel_11_3_1/bruker_uten_barn_har_folkeregistrert_ektefelle.json")
    fun brukerUtenBarnHarIkkeFolkeregistrertEktefelle() = lesDatagrunnlag("$norskLovvalg/regel_11_3_1/bruker_uten_barn_har_ikke_folkeregistrert_ektefelle.json")

    // Regel 11.3.1.1 "Har bruker uten barn og uten folkeregistrert ektefelle vært i 100% stilling?
    fun brukerUtenBarnOgUtenFolkeregistrertEktefelleHarVeartI100prosent() = lesDatagrunnlag("$norskLovvalg/regel_11_3_1_1/barnloes_bruker_uten_folkeregistrert_ektefelle_har_jobbet_100_prosent.json")
    fun brukerUtenBarnOgUtenFolkeregistrertEktefelleHarIkkeVeartI100prosent() = lesDatagrunnlag("$norskLovvalg/regel_11_3_1_1/barnloes_bruker_uten_folkeregistrert_ektefelle_har_ikke_jobbet_100_prosent.json")

    // Regel 11.4 Er brukers (bruker med barn) ektefelle folkeregistrert som bosatt i Norge?
    fun brukerHarBarnOgEktefelleErRegistrertINorge() = lesDatagrunnlag("$norskLovvalg/regel_11_4/brukers_ektefelle_er_folkeregistrert_i_Norge.json")
    fun brukerHarBarnOgEktefelleErIkkeRegistrertINorge() = lesDatagrunnlag("$norskLovvalg/regel_11_4/brukers_ektefelle_er_ikke_folkeregistrert_i_Norge.json")

    // Regel 11.4.1 Er bruker uten folkeregistrert ektefelle sitt barn folkeregistrert?
    fun brukerHarIkkeFolkeregistrertEktefelleMenFolkeregistrertBarn() = lesDatagrunnlag("$norskLovvalg/regel_11_4_1/bruker_uten_folkeregistrert_ektefelle_har_folkeregistrert_barn.json")
    fun brukerHarIkkeFolkeregistrertEktefelleOgIkkeFolkeregisrertBarn() = lesDatagrunnlag("$norskLovvalg/regel_11_4_1/bruker_uten_folkeregistrert_ektefelle_har_ikke_folkeregistrert_barn.json")

    // Regel 11.5 Er bruker med folkeregistrert ektefelle sitt barn også folkeregistrert?
    fun brukerHarFolkeregistrertEktefelleMenBarnErIkkeFolkeregistrert() = lesDatagrunnlag("$norskLovvalg/regel_11_5/bruker_har_folkeregistrert_ektefelle_men_ikke_barn.json")
    fun brukerHarFolkeregistrertEktefelleOgBarn() = lesDatagrunnlag("$norskLovvalg/regel_11_5/bruker_har_folkeregistrert_ektefelle_og_barn.json")

    // Regel 11.6 Har med folkeregistrerte relasjoner bruker jobbet mer enn 80 prosent?
    fun brukermedFolkeregistrertRelasjonerHarJobbetMerEnn80Prosent() = lesDatagrunnlag("$norskLovvalg/regel_11_6/brukerMedFolkeregistrerteRelasjonerHarJobbetMerEnn80ProsentStilling.json")
    fun brukermedFolkeregistrertRelasjonerHarIkkeJobbetMerEnn80Prosent() = lesDatagrunnlag("$norskLovvalg/regel_11_6/brukerMedFolkeregistrerteRelasjonerHarIkkeJobbetMerEnn80ProsentStilling.json")

    // Regel 12 - "Har bruker vært i minst 25% stilling de siste 12 mnd?"
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
