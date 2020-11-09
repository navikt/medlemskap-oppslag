package no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder

import no.nav.medlemskap.clients.aareg.AaRegAnsettelsesperiode

class AnsettelsesPeriodeBuilder {
    var bruksperiodeBuilder = AaRegBruksperiodeBuilder()
    var periodeBuilder = AaregPeriodeBuilder()
    var sporingsinformasjonBuilder = SporingsinformasjonBuilder()

    var bruksperiode = bruksperiodeBuilder.build()
    var periode = periodeBuilder.build()
    var varslingskode = String()
    var sporingsinformasjon = sporingsinformasjonBuilder.build()

    fun build(): AaRegAnsettelsesperiode =
        AaRegAnsettelsesperiode(
            bruksperiode = bruksperiode,
            periode = periode,
            varslingskode = varslingskode,
            sporingsinformasjon = sporingsinformasjon
        )
}
