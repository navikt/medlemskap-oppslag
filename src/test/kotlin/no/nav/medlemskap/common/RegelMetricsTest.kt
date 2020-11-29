package no.nav.medlemskap.common

import assertk.assertThat
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.MockClock
import io.micrometer.core.instrument.MockClock.clock
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.simple.SimpleConfig
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.RegelId.Companion.metricName
import no.nav.medlemskap.regler.evaluer
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.time.Duration

@TestInstance(PER_CLASS)
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

    @BeforeEach
    fun initCollectorRegistry() {
        Metrics.addRegistry(simpleRegistry)
        simpleRegistry.clear()
        gjørNoeMagiJegIkkeForstår()
    }

    @Test
    fun `evaluering av bruker gir en metrikk for medlemskapskonklusjon`() {
        gjørNoeMagiJegIkkeForstår()

        evaluer(personleser.brukerIkkeFolkeregistrertSomBosattINorge())

        gjørNoeMagiJegIkkeForstår()

        assertThat(simpleRegistry.meters.map { it.id.name }).contains("regel_calls_total")

        val meters = simpleRegistry.meters.filter { it.id.name == "regel_calls_total" }

        assertThat(meters.medTagRegelVerdi(RegelId.REGEL_MEDLEM_KONKLUSJON.metricName())).containsExactly(1.0)
        assertThat(meters.medTagRegelVerdi(RegelId.REGEL_2.metricName())).containsExactly(1.0)
    }

    private fun gjørNoeMagiJegIkkeForstår() {
        clock(simpleRegistry).add(step())
        simpleRegistry.forEachMeter { it.measure() }
        clock(simpleRegistry).add(step())
    }
}

private fun List<Meter>.medTagRegelVerdi(s: String): List<Double> =
    filter { it.id.tags.contains(Tag.of("regel", s)) }.flatMap { it.measure() }.map { it.value }
