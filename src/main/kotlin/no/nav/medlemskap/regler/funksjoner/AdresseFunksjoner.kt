package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.personhistorikk.Adresse

object AdresseFunksjoner {
    fun List<Adresse>.adresserForKontrollPeriode(kontrollPeriode: Kontrollperiode): List<Adresse> =
        this.filter { it.overlapper(kontrollPeriode.periode) }

    fun List<Adresse>.landkodeTilAdresserForKontrollPeriode(kontrollPeriode: Kontrollperiode): List<String> =
        this.adresserForKontrollPeriode(kontrollPeriode).map { it.landkode }
}
