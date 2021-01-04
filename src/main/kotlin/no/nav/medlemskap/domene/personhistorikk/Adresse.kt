package no.nav.medlemskap.domene.personhistorikk

import no.nav.medlemskap.domene.Periode
import java.time.LocalDate

data class Adresse(
    val landkode: String,
    val fom: LocalDate?,
    val tom: LocalDate?
) {
    private val periode = Periode(fom, tom)

    fun overlapper(annenPeriode: Periode): Boolean {
        return periode.overlapper(annenPeriode)
    }
}
