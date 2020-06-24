package no.nav.medlemskap.domene

import java.time.LocalDate

data class Medlemskap(
        val dekning: String?,
        val fraOgMed: LocalDate,
        val tilOgMed: LocalDate,
        val erMedlem: Boolean,
        val lovvalg: String,
        val lovvalgsland: String?
) : Comparable<Medlemskap> {

    override fun compareTo(other: Medlemskap): Int {
        return this.tilOgMed.compareTo(other.tilOgMed)
    }

}

enum class Dekning(val dekningKodeverdi: String) {
    FOLKETRYGDLOVEN2_6("FTL_2-6"),
    FOLKETRYGDLOVEN2_7A_2A("FTL_2-7a_2_ledd_a"),
    FOLKETRYGDLOVEN2_7A_2B("FTL_2-7a_2_ledd_b"),
    FOLKETRYGDLOVEN2_7_3A("FTL_2-7_3_ledd_a"),
    FOLKETRYGDLOVEN2_7_3B("FTL_2-7_3_ledd_b"),
    FOLKETRYGDLOVEN2_9_1A("FTL_2-9_1_ledd_a"),
    FOLKETRYGDLOVEN2_9_1B("FTL_2-9_1_ledd_b"),
    FOLKETRYGDLOVEN2_9_1C("FTL_2-9_1_ledd_c"),
    FOLKETRYGDLOVEN2_9_2_1A("FTL_2-9_2_ld_jfr_1a"),
    FOLKETRYGDLOVEN2_9_2_1C("FTL_2-9_2_ld_jfr_1c"),
    FULL("Full"),
    IHT_AVTALE("IHT_Avtale"),
    IKKEPENDEL("IKKEPENDEL"),
    OPPHOR("Opphor"),
    PENDEL("PENDEL"),
    UNNTATT("Unntatt");

    companion object {
        fun dekningForSykepenger(): List<Dekning> =
                listOf(FOLKETRYGDLOVEN2_6,
                        FOLKETRYGDLOVEN2_7A_2B,
                        FOLKETRYGDLOVEN2_7_3A,
                        FOLKETRYGDLOVEN2_9_2_1A,
                        FOLKETRYGDLOVEN2_9_2_1C)

        fun dekningForDagpenger(): List<Dekning> = listOf(FOLKETRYGDLOVEN2_6, FOLKETRYGDLOVEN2_7A_2B)
    }

}