package no.nav.medlemskap.domene

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import no.nav.medlemskap.domene.Statsborgerskap.Companion.erAnnenStatsborger
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class StatsborgerskapTest {

    @Test
    fun overlapper() {
        val statsborgerskap = Statsborgerskap("NOR", LocalDate.of(2015, 1, 1), null)

        assertThat(statsborgerskap.overlapper(LocalDate.of(2016, 1, 1))).isTrue()
    }

    @Test
    fun erAnnenStatsborger_norsk_borger_er_ikke_annen_statsborger() {
        val statsborgerskap = listOf(
            Statsborgerskap("NOR", null, null),
            Statsborgerskap("USA", null, null)
        )

        val inputPeriode = InputPeriode(LocalDate.of(2019, 1, 1), LocalDate.of(2020, 12, 31))
        val førsteDagForYtelse = LocalDate.of(2019, 1, 1)

        assertThat(statsborgerskap.erAnnenStatsborger(inputPeriode, førsteDagForYtelse)).isFalse()
    }

    @Test
    fun erAnnenStatsborger_belgisk_borger_er_ikke_annen_statsborger() {
        val statsborgerskap = listOf(
            Statsborgerskap("BEL", null, null)
        )

        val inputPeriode = InputPeriode(LocalDate.of(2019, 1, 1), LocalDate.of(2020, 12, 31))
        val førsteDagForYtelse = LocalDate.of(2019, 1, 1)

        assertThat(statsborgerskap.erAnnenStatsborger(inputPeriode, førsteDagForYtelse)).isFalse()
    }

    @Test
    fun erAnnenStatsborger_amerikansk_borger_er_annen_statsborger() {
        val statsborgerskap = listOf(
            Statsborgerskap("USA", null, null)
        )

        val inputPeriode = InputPeriode(LocalDate.of(2019, 1, 1), LocalDate.of(2020, 12, 31))
        val førsteDagForYtelse = LocalDate.of(2019, 1, 1)

        assertThat(statsborgerskap.erAnnenStatsborger(inputPeriode, førsteDagForYtelse)).isTrue()
    }
}
