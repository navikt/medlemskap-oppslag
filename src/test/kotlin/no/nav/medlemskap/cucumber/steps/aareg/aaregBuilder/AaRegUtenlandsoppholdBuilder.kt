package no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder

import no.nav.medlemskap.clients.aareg.AaRegUtenlandsopphold
import java.time.YearMonth

class AaRegUtenlandsoppholdBuilder {
    var sportingsinformasjonBuilder = AaRegSporingsinformasjonBuilder()
    var periodeBuilder = AaregPeriodeBuilder()

    var landkode = String()
    var rapporteringsperiode = YearMonth.now()
    var sportingsinformasjon = sportingsinformasjonBuilder.build()
    var periode = periodeBuilder.build()

    fun build(): AaRegUtenlandsopphold =
        AaRegUtenlandsopphold(
            landkode = landkode,
            rapporteringsperiode = rapporteringsperiode,
            sporingsinformasjon = sportingsinformasjon,
            periode = periode
        )
}
