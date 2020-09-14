package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.regler.common.Funksjoner
import no.nav.medlemskap.regler.common.lagInterval
import java.time.LocalDate

object AdresseFunksjoner {

    fun String.landkodeErNorsk() = this == "NOR"

    fun Adresse.adressensPeriodeOverlapperKontrollPerioden(kontrollPeriode: Kontrollperiode) =
        Funksjoner.periodefilter(lagInterval(Periode(this.fom, this.tom)), kontrollPeriode.tilPeriode())

    fun List<Adresse>.adresserForKontrollPeriode(kontrollPeriode: Kontrollperiode): List<Adresse> =
        this.filter { it.adressensPeriodeOverlapperKontrollPerioden(kontrollPeriode) }

    fun List<Adresse>.landkodeTilAdresserForKontrollPeriode(kontrollPeriode: Kontrollperiode): List<String> =
        this.adresserForKontrollPeriode(kontrollPeriode).map { it.landkode }

    fun List<Adresse>.harSammeAdressePaaGitteDatoer(dato1: LocalDate, dato2: LocalDate): Boolean =
        this.adresseForDato(dato1).size == 1 &&
            this.adresseForDato(dato2) == this.adresseForDato(dato2)

    infix fun List<Adresse>.adresseForDato(dato: LocalDate) =
        this.filter { Funksjoner.periodefilter(lagInterval(Periode(it.fom, it.tom)), Periode(dato, dato)) }
}
