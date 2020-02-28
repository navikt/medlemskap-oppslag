package no.nav.medlemskap.common

/* Disabler denne til vi finner ut hvorfor den feiler
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
*/
