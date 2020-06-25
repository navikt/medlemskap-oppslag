package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.regler.common.Funksjoner
import no.nav.medlemskap.regler.common.lagInterval
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.hentAdressetyperIKontrollperiode
import java.time.LocalDate

object AdresseFunksjoner {


    fun Adresse.adresseLandskodeErNorsk() = this.landkode == "NOR"

    fun Adresse.adressensPeriodeOverlapperKontrollPerioden(kontrollPeriode: Periode) =
            Funksjoner.periodefilter(lagInterval(Periode(this.fom, this.tom)), kontrollPeriode)

    fun List<Adresse>.harBrukerNorskAdresseInnenforSiste12Mnd(kontrollPeriode: Periode): Boolean =
            this.any { it.adresseLandskodeErNorsk() && it.adressensPeriodeOverlapperKontrollPerioden(kontrollPeriode) }

    fun List<Adresse>.harSammeAdressePaaGitteDatoer(dato1: LocalDate, dato2: LocalDate): Boolean =
            this.adresseForDato(dato1).size == 1 &&
                    this.adresseForDato(dato2) == this.adresseForDato(dato2)


    infix fun List<Adresse>.hentAdressetyperIKontrollperiode(kontrollPeriode: Periode): List<String> =
            this.filter {
                Funksjoner.periodefilter(lagInterval(Periode(it.fom, it.tom)), kontrollPeriode)
            }.map { it.adresseType.name }






    infix fun List<Adresse>.adresseForDato(dato: LocalDate) =
            this.filter { Funksjoner.periodefilter(lagInterval(Periode(it.fom, it.tom)), Periode(dato, dato)) }

}