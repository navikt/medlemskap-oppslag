package no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder

import no.nav.medlemskap.clients.aareg.AaRegAntallTimerForTimeloennet
import java.time.YearMonth

class AntallTimerForLoennetBuilder {
    var sporingsinformasjonBuilder = SporingsinformasjonBuilder()

    var sporingsinformasjon = sporingsinformasjonBuilder.build()
    var aaregPeriodeBuilder = AaregPeriodeBuilder()
    var antallTimerForTimeloennet = 1.0
    var periode = aaregPeriodeBuilder.build()
    var rapporteringsperiode = YearMonth.now()

    fun build(): AaRegAntallTimerForTimeloennet {
        return AaRegAntallTimerForTimeloennet(
            antallTimer = antallTimerForTimeloennet,
            periode = periode,
            rapporteringsperiode = rapporteringsperiode,
            sporingsinformasjon = sporingsinformasjon
        )
    }
}
