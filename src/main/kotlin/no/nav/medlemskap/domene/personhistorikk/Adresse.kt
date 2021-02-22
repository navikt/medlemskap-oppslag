package no.nav.medlemskap.domene.personhistorikk

import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.Periode
import java.time.LocalDate

data class Adresse(
    val landkode: String,
    val fom: LocalDate?,
    val tom: LocalDate?,
    val historisk: Boolean
) {
    private val periode = Periode(fom, tom)

    fun overlapper(annenPeriode: Periode): Boolean {
        return periode.overlapper(annenPeriode)
    }

    companion object {
        fun List<Adresse>.adresserForKontrollPeriode(kontrollPeriode: Kontrollperiode): List<Adresse> =
            this.filter { it.overlapper(kontrollPeriode.periode) }

        fun List<Adresse>.landkodeTilAdresserForKontrollPeriode(kontrollPeriode: Kontrollperiode): List<String> =
            this.adresserForKontrollPeriode(kontrollPeriode).map { it.landkode }
    }
}
