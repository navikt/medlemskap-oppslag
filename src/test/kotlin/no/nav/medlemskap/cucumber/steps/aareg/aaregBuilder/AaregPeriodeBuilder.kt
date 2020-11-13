package no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder

import no.nav.medlemskap.clients.aareg.AaRegPeriode
import java.time.LocalDate

class AaregPeriodeBuilder {
    var fom = LocalDate.MIN
    var tom = LocalDate.MAX

    fun build(): AaRegPeriode =
        AaRegPeriode(
            fom = fom,
            tom = tom
        )
}
