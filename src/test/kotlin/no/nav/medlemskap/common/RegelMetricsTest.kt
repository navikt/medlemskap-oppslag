package no.nav.medlemskap.common

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsExactly
import assertk.assertions.containsOnly
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.MockClock
import io.micrometer.core.instrument.MockClock.clock
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.simple.SimpleConfig
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import no.nav.medlemskap.regler.common.Personfakta.Companion.initialiserFakta
import no.nav.medlemskap.regler.personer.Personleser
import no.nav.medlemskap.regler.v1.RegelsettForGrunnforordningen
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.time.Duration

@TestInstance(PER_CLASS)
@Disabled
class RegelMetricsTest {


    /*
     * Inspirasjon tatt fra
     * https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-test/src/main/java/io/micrometer/core/tck/CounterTest.java
     */
    private fun step(): Duration {
        return SimpleConfig.DEFAULT.step()
    }

    private val personleser = Personleser()
    private val simpleRegistry = SimpleMeterRegistry(SimpleConfig.DEFAULT, MockClock())

    @BeforeAll
    fun initCollectorRegistry() {
        Metrics.addRegistry(simpleRegistry)
        simpleRegistry.clear()
        clock(simpleRegistry).add(step());
    }

    @Test
    fun `evaluering av regelsett for eøs forordningen for amerikansk statsborgerskap gir to metrikker`() {
        RegelsettForGrunnforordningen().evaluer(initialiserFakta(personleser.enkelAmerikansk()))
        clock(simpleRegistry).add(step());

        assertThat(simpleRegistry.meters.map { it.id.name }).contains("regel_calls_total")

        val meters = simpleRegistry.meters.filter { it.id.name == "regel_calls_total" }
        assertThat(meters.map { it.id }.flatMap { it.tags })
                .containsOnly(
                        Tag.of("regel", "Er brukeren statsborger i et EØS land"),
                        Tag.of("regel", "Regelsett for grunnforordningen"),
                        Tag.of("status", "NEI"),
                        Tag.of("status", "NEI")
                )
        assertThat(meters.counterVerdi("Er brukeren statsborger i et EØS land")).containsExactly(1.0)
        assertThat(meters.counterVerdi("Regelsett for grunnforordningen")).containsExactly(1.0)

    }

}

private fun List<Meter>.counterVerdi(s: String): List<Double> =
        filter { it.id.tags.contains(Tag.of("regel", s)) }.flatMap { it.measure() }.map { it.value }
