package no.nav.medlemskap.services.ereg

import java.time.LocalDate

data class Organisasjon(val enhetstype: String?)

data class OrganisasjonsInfo(val organisasjonDetaljer: Detaljer?)

data class Detaljer(val ansatte: List<Ansatte>, val opphoersdato: LocalDate?)

data class Ansatte(val antall: Int, val bruksPeriode: Bruksperiode, val gyldighetsperiode: Gyldighetsperiode)

data class Bruksperiode(val fom: LocalDate, val tom: LocalDate?)

data class Gyldighetsperiode(val fom: LocalDate?, val tom: LocalDate?)




