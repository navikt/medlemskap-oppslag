package no.nav.medlemskap.common

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.extracting
import assertk.assertions.isEqualTo
import assertk.assertions.size
import io.prometheus.client.Collector
import io.prometheus.client.CollectorRegistry
import no.nav.medlemskap.regler.common.Fakta.Companion.initialiserFakta
import no.nav.medlemskap.regler.personer.Personleser
import no.nav.medlemskap.regler.v1.RegelsettForGrunnforordningen
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class RegelMetricsTest {

    private val personleser = Personleser()

    @BeforeEach
    fun initCollectorRegistry() {
        CollectorRegistry.defaultRegistry.clear()
    }

    @Test
    fun `evaluering av regelsett for eøs forordningen for amerikansk statsborgerskap gir to metrikker`() {
        RegelsettForGrunnforordningen(initialiserFakta(personleser.enkelAmerikansk())).evaluer()

        val sampleList = CollectorRegistry.defaultRegistry.metricFamilySamples().toList().flatMap { it.samples.toList() }

        assertThat(sampleList.map { it.name }.distinct()).contains("regel_calls_total")
        assertThat(sampleList.map { it.name }.toList()).size().isEqualTo(2)

        assertThat(sampleList).extracting(Collector.MetricFamilySamples.Sample::labelNames, Collector.MetricFamilySamples.Sample::labelValues).contains(listOf("regel", "status") to listOf("Er personen statsborger i et EØS land?", "NEI"))
        assertThat(sampleList).extracting(Collector.MetricFamilySamples.Sample::labelNames, Collector.MetricFamilySamples.Sample::labelValues).contains(listOf("regel", "status") to listOf("Regelsett for grunnforordningen", "NEI"))

        assertThat(sampleList).extracting(Collector.MetricFamilySamples.Sample::labelNames, Collector.MetricFamilySamples.Sample::value).contains(listOf("regel", "status") to 1.0)
        assertThat(sampleList).extracting(Collector.MetricFamilySamples.Sample::labelNames, Collector.MetricFamilySamples.Sample::value).contains(listOf("regel", "status") to 1.0)
    }

}
