package no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder

import no.nav.medlemskap.clients.aareg.AaRegPermisjonPermittering
import no.nav.medlemskap.domene.PermisjonPermitteringType

class AaRegPermisjonsPermitteringerBuilder {
    var periodeBuilder = AaregPeriodeBuilder()
    var sporingsinformasjonBuilder = AaRegSporingsinformasjonBuilder()

    var periode = periodeBuilder.build()
    var permisjonsPermitteringId = String()
    var sporingsinformasjon = sporingsinformasjonBuilder.build()
    var type = PermisjonPermitteringType.VELFERDSPERMISJON.kodeverdi.toString()
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
