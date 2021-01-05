package no.nav.medlemskap.domene.personhistorikk

import no.nav.medlemskap.domene.InputPeriode
import java.time.LocalDate

data class Personhistorikk(
    val statsborgerskap: List<Statsborgerskap>,
    val bostedsadresser: List<Adresse>,
    val kontaktadresser: List<Adresse>,
    val oppholdsadresser: List<Adresse>,
    val sivilstand: List<Sivilstand>,
    val familierelasjoner: List<Familierelasjon>,
    val doedsfall: List<LocalDate>
) {
    companion object {
        fun List<LocalDate>.erBrukerDoedEtterPeriode(periode: InputPeriode): Boolean {
            return this.stream().allMatch { it.isAfter(periode.tom) }
        }
    }
}
