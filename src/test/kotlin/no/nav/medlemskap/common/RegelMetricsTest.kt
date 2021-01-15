package no.nav.medlemskap.common

import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.MockClock
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.simple.SimpleConfig
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.RegelId.Companion.metricName
import no.nav.medlemskap.regler.evaluer
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.math.absoluteValue

class RegelMetricsTest {

    /*
     * Inspirasjon tatt fra
     * https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-test/src/main/java/io/micrometer/core/tck/CounterTest.java
     */
    private fun step(): Duration {
        return SimpleConfig.DEFAULT.step()
    }

    private val personleser = Personleser()

    @Test
    fun `evaluering av bruker gir en metrikk for medlemskapskonklusjon`() {
        val simpleRegistry = SimpleMeterRegistry(SimpleConfig.DEFAULT, MockClock())
        Metrics.addRegistry(simpleRegistry)
        simpleRegistry.clear()
        evaluer(personleser.brukerIkkeFolkeregistrertSomBosattINorge())

        // assertEquals(57, simpleRegistry.meters.size) //Blir 68 i GA
        assertTrue(simpleRegistry.meters.map { it.id.name }.contains("regel_calls_total"))
        assertTrue(simpleRegistry.meters.map { it.id.name }.contains("regel_calls_influx"))

        val registeredRegelCallsTotal = simpleRegistry.meters.filter { it.id.name == "regel_calls_total" }
        val registeredRegelCallsInflux = simpleRegistry.meters.filter { it.id.name == "regel_calls_influx" }

        assertEquals(28, registeredRegelCallsTotal.size)
        assertEquals(28, registeredRegelCallsInflux.size)

        assertEquals(1, registeredRegelCallsTotal.medTagRegelVerdi(RegelId.REGEL_MEDLEM_KONKLUSJON.metricName()).size)
        assertEquals(1.0, registeredRegelCallsTotal.medTagRegelVerdi(RegelId.REGEL_MEDLEM_KONKLUSJON.metricName())[0].absoluteValue, 1.0)

        assertEquals(1, registeredRegelCallsTotal.medTagRegelVerdi(RegelId.REGEL_2.metricName()).size)
        assertEquals(1.0, registeredRegelCallsTotal.medTagRegelVerdi(RegelId.REGEL_2.metricName())[0].absoluteValue, 1.0)
    }

    private fun List<Meter>.medTagRegelVerdi(s: String): List<Double> =
        filter { it.id.tags.contains(Tag.of("regel", s)) }.flatMap { it.measure() }.map { it.value }
}
