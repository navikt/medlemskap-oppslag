package no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder

import no.nav.medlemskap.clients.aareg.AaRegPermisjonPermittering

class PermisjonsPermitteringerBuilder {
    var periodeBuilder = AaregPeriodeBuilder()
    var sporingsinformasjonBuilder = SporingsinformasjonBuilder()

    var periode = periodeBuilder.build()
    var permisjonsPermitteringId = String()
    var sporingsinformasjon = sporingsinformasjonBuilder.build()
    var type = String()
    var varslingkode = String()
    var prosent = 1.0

    fun build(): AaRegPermisjonPermittering =
        AaRegPermisjonPermittering(
            periode = periode,
            permisjonPermitteringId = permisjonsPermitteringId,
            sporingsinformasjon = sporingsinformasjon,
            type = type,
            varslingskode = varslingkode,
            prosent = prosent
        )
}
