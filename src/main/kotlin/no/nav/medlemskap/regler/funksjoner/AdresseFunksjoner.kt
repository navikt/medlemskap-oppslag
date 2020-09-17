package no.nav.medlemskap.regler.funksjoner

import mu.KotlinLogging
import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.regler.common.Funksjoner
import no.nav.medlemskap.regler.common.lagInterval
import java.time.DateTimeException
import java.time.LocalDate

object AdresseFunksjoner {

    private val logger = KotlinLogging.logger { }

    fun String.landkodeErNorsk() = this == "NOR"

    fun Adresse.adressensPeriodeOverlapperKontrollPerioden(kontrollPeriode: Kontrollperiode): Boolean =
        try {
            Funksjoner.periodefilter(lagInterval(Periode(this.fom, this.tom)), kontrollPeriode.tilPeriode())
        } catch (e: DateTimeException) {
            logger.warn("Klarer ikke å lage intervall for adresse med landkode {} med fradato {} og tildato {}", this.landkode, this.fom, this.tom, e)
            // TODO: Må sjekke om det er korrekt å returnere false her når det er noe galt med perioden
            false
        }

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
