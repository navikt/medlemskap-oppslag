package no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder

import no.nav.medlemskap.clients.aareg.AaRegArbeidsavtale

class ArbeidsavtaleBuilder {
    var bruksperiodeBuilder = AaRegBruksperiodeBuilder()
    var gyldighetsperiodeBuilder = AaRegGyldighetsperiodeBuilder()
    var sporingsinformasjonBuilder = SporingsinformasjonBuilder()

    var antallTimerPerUke = null
    var arbeidstidsordning = String()
    var beregnetAntallTimerPrUke = 1.0
    var bruksperiode = bruksperiodeBuilder.build()
    var gyldighetsperiode = gyldighetsperiodeBuilder.build()
    var sistStillingsendring = String()
    var skipsregister = String()
    var skipstype = String()
    var fartsomraade = String()
    var yrke = String()
    var sporingsinformasjon = sporingsinformasjonBuilder.build()
    var stillingsprosent = null
    var sistLoennsendring = String()

    fun build(): AaRegArbeidsavtale =
        AaRegArbeidsavtale(
            antallTimerPrUke = antallTimerPerUke,
            arbeidstidsordning = arbeidstidsordning,
            beregnetAntallTimerPrUke = beregnetAntallTimerPrUke,
            bruksperiode = bruksperiode,
            gyldighetsperiode = gyldighetsperiode,
            sistStillingsendring = stillingsprosent,
            skipsregister = skipsregister,
            skipstype = skipstype,
            fartsomraade = fartsomraade,
            yrke = yrke,
            sporingsinformasjon = sporingsinformasjon,
            stillingsprosent = stillingsprosent,
            sistLoennsendring = String()
        )
}
