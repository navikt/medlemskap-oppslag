package no.nav.medlemskap.common

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.extracting
import assertk.assertions.isEqualTo
import assertk.assertions.size
import io.prometheus.client.Collector
import no.nav.medlemskap.regler.common.Personfakta.Companion.initialiserFakta
import no.nav.medlemskap.regler.personer.Personleser
import no.nav.medlemskap.regler.v1.RegelsettForGrunnforordningen
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
class RegelMetricsTest {

    private val personleser = Personleser()

    @BeforeAll
    fun initCollectorRegistry() {
        regelCounter.clear()
    }

    @Test
    fun `evaluering av regelsett for eøs forordningen for amerikansk statsborgerskap gir to metrikker`() {
        RegelsettForGrunnforordningen().evaluer(initialiserFakta(personleser.enkelAmerikansk()))

        val sampleList = regelCounter.collect().flatMap { it.samples.toList() }
        
        assertThat(sampleList.map { it.name }.distinct()).contains("regel_calls_total")
        assertThat(sampleList.map { it.name }.toList()).size().isEqualTo(2)

        assertThat(sampleList).extracting(Collector.MetricFamilySamples.Sample::labelNames, Collector.MetricFamilySamples.Sample::labelValues).contains(listOf("regel", "status") to listOf("Er personen statsborger i et EØS land", "NEI"))
        assertThat(sampleList).extracting(Collector.MetricFamilySamples.Sample::labelNames, Collector.MetricFamilySamples.Sample::labelValues).contains(listOf("regel", "status") to listOf("Regelsett for grunnforordningen", "NEI"))

        assertThat(sampleList).extracting(Collector.MetricFamilySamples.Sample::labelNames, Collector.MetricFamilySamples.Sample::value).contains(listOf("regel", "status") to 1.0)
        assertThat(sampleList).extracting(Collector.MetricFamilySamples.Sample::labelNames, Collector.MetricFamilySamples.Sample::value).contains(listOf("regel", "status") to 1.0)
    }

}
