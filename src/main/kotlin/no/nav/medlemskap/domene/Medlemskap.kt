package no.nav.medlemskap.domene

import java.time.LocalDate

data class Medlemskap(
        val dekning: String?,
        val fraOgMed: LocalDate,
        val tilOgMed: LocalDate,
        val erMedlem: Boolean,
        val lovvalg: Lovvalg?,
        val lovvalgsland: String?,
        val periodeStatus: PeriodeStatus?
) : Comparable<Medlemskap> {

    override fun compareTo(other: Medlemskap): Int {
        return this.tilOgMed.compareTo(other.tilOgMed)
    }

}

enum class Lovvalg(val lovvalgKodeverdi: String) {
    ENDELIG("ENDL"),
    FORELOPIG("FORL"),
    UNDER_AVKLARING("UAVK");

    companion object {
        fun from(kodeverdi: String?): Lovvalg? = Lovvalg.values().firstOrNull { it.lovvalgKodeverdi == kodeverdi }
    }
}

enum class PeriodeStatus(val periodeStatusKodeverdi: String) {
    GYLDIG("GYLD"),
    AVVIST("AVST"),
    UAVKLART("UAVK");

    companion object {
        fun from(kodeverdi: String?): PeriodeStatus? = values().firstOrNull { it.periodeStatusKodeverdi == kodeverdi }
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

        private val dekningForYtelseMap: Map<Ytelse, List<Dekning>> = hashMapOf(
                Ytelse.SYKEPENGER to dekningForSykepenger(),
                Ytelse.DAGPENGER to dekningForDagpenger(),
                Ytelse.ENSLIG_FORSORGER to dekningForEnsligForsorger()
        )

        private val uavklarteDekningerForYtelseMap: Map<Ytelse, List<Dekning>> = hashMapOf(
                Ytelse.SYKEPENGER to uavklarteDekningerForSykepenger(),
                Ytelse.DAGPENGER to fellesUavklarteDekninger(),
                Ytelse.ENSLIG_FORSORGER to fellesUavklarteDekninger()
        )

        private fun dekningForSykepenger(): List<Dekning> =
                listOf(FOLKETRYGDLOVEN2_6,
                        FOLKETRYGDLOVEN2_7_3A,
                        FOLKETRYGDLOVEN2_9_2_1A,
                        FOLKETRYGDLOVEN2_9_2_1C)

        private fun dekningForDagpenger(): List<Dekning> =
                listOf(FOLKETRYGDLOVEN2_7_3A,
                        FOLKETRYGDLOVEN2_7A_2A,
                        FOLKETRYGDLOVEN2_9_1B,
                        FOLKETRYGDLOVEN2_9_1C,
                        FOLKETRYGDLOVEN2_9_2_1A,
                        FOLKETRYGDLOVEN2_9_2_1C
                )

        private fun dekningForEnsligForsorger(): List<Dekning> =
                listOf(FOLKETRYGDLOVEN2_7_3A,
                        FOLKETRYGDLOVEN2_7A_2A,
                        FOLKETRYGDLOVEN2_9_1B,
                        FOLKETRYGDLOVEN2_9_1C,
                        FOLKETRYGDLOVEN2_9_2_1A,
                        FOLKETRYGDLOVEN2_9_2_1C
                )

        private fun uavklarteDekningerForSykepenger(): List<Dekning> = listOf(FOLKETRYGDLOVEN2_7A_2A, FOLKETRYGDLOVEN2_7A_2B) + fellesUavklarteDekninger()

        fun fellesUavklarteDekninger(): List<Dekning> =
                listOf(FULL,
                        IHT_AVTALE,
                        IKKEPENDEL,
                        PENDEL,
                        OPPHOR
                )

        fun Dekning.uavklartForYtelse(ytelse: Ytelse): Boolean = uavklarteDekningerForYtelseMap[ytelse]?.contains(this) ?: false

        fun Dekning.gjelderForYtelse(ytelse: Ytelse): Boolean = dekningForYtelseMap[ytelse]?.contains(this) ?: false

        fun from(kodeverdi: String?): Dekning? = values().firstOrNull { it.dekningKodeverdi == kodeverdi }

    }

}