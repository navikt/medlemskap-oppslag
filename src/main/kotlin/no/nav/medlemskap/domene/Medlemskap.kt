package no.nav.medlemskap.domene

import java.time.LocalDate

data class Medlemskap (
        val dekning: String?,
        val fraOgMed: LocalDate,
        val tilOgMed: LocalDate,
        val erMedlem: Boolean,
        val lovvalg: String,
        val lovvalgsland: String?
)

enum class DekningForSykepenger(val dekning: String) {
    FOLKETRYGDLOVEN2_6("FTL_2-6"),
    FOLKETRYGDLOVEN2_7B("FTL_2-7a_2_ledd_b"),
    FOLKETRYGDLOVEN2_7_3A("FTL_2-7_3_ledd_a"),
    FOLKETRYGDLOVEN2_9LEDD2_1A("FTL_2-9_2_ld_jfr_1a"),
    FOLKETRYGDLOVEN2_9LEDD2_1C("FTL_2-9_2_ld_jfr_1c")
}