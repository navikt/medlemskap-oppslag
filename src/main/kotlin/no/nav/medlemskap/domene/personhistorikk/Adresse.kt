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
        fun List<Adresse>.adresserForKontrollperiode(kontrollperiode: Kontrollperiode): List<Adresse> =
            this.filter { it.overlapper(kontrollperiode.periode) }

        fun List<Adresse>.landkodeTilAdresserForKontrollPeriode(kontrollperiode: Kontrollperiode): List<String> =
            this.adresserForKontrollperiode(kontrollperiode).map { it.landkode }

        fun List<Adresse>.landkodeForIkkeHistoriskeAdresserForKontrollperiode(kontrollperiode: Kontrollperiode): List<String> =
            this.filter { it.overlapper(kontrollperiode.periode) && !it.historisk }.map { it.landkode }
    }
}
