package no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder

import no.nav.medlemskap.clients.aareg.AaRegAnsettelsesperiode

class AaRegAnsettelsesPeriodeBuilder {
    var bruksperiodeBuilder = AaRegBruksperiodeBuilder()
    var periodeBuilder = AaregPeriodeBuilder()
    var sporingsinformasjonBuilder = AaRegSporingsinformasjonBuilder()

    var bruksperiode = bruksperiodeBuilder.build()
    var periode = periodeBuilder.build()
    var varslingskode = String()
    var sporingsinformasjon = sporingsinformasjonBuilder.build()

    fun build(): AaRegAnsettelsesperiode =
        AaRegAnsettelsesperiode(
            bruksperiode = bruksperiode,
            gyldighetsperiode = periode,
            varslingskode = varslingskode,
            sporingsinformasjon = sporingsinformasjon
        )
}
