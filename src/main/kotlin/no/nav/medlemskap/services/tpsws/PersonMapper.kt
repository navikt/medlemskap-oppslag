package no.nav.medlemskap.services.tpsws

import no.nav.medlemskap.domene.*
import no.nav.tjeneste.virksomhet.person.v3.informasjon.MidlertidigPostadresseNorge
import no.nav.tjeneste.virksomhet.person.v3.informasjon.MidlertidigPostadresseUtland
import no.nav.tjeneste.virksomhet.person.v3.informasjon.UstrukturertAdresse
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkResponse
import java.time.LocalDate
import java.util.*
import javax.xml.datatype.XMLGregorianCalendar

fun mapPersonhistorikkResultat(personhistorikkResponse: HentPersonhistorikkResponse): Personhistorikk {
    val statsborgerskap: List<Statsborgerskap> = personhistorikkResponse.statsborgerskapListe.map {
        Statsborgerskap(
                landkode = it.statsborgerskap.land.value,
                fom = it.periode.fom.asDate(),
                tom = it.periode.tom.asDate()
        )
    }

    val personstatuser: List<Personstatus> = personhistorikkResponse.personstatusListe.map {
        Personstatus(
                personstatus = it.personstatus.value,
                fom = it.periode.fom.asDate(),
                tom = it.periode.tom.asDate()
        )
    }

    val bostedsadresser: List<Adresse> = personhistorikkResponse.bostedsadressePeriodeListe.map {
        Adresse(
                adresselinje = it.bostedsadresse.strukturertAdresse.tilleggsadresse ?: "Ukjent adresse",
                landkode = it.bostedsadresse.strukturertAdresse.landkode.value,
                fom = it.periode.fom.asDate(),
                tom = it.periode.tom.asDate(),
                adresseType = Adressetype.BOAD
        )
    }

    val postadresser: List<Adresse> = personhistorikkResponse.postadressePeriodeListe.map {
            Adresse(
                    adresselinje = it.postadresse.ustrukturertAdresse.adresselinje(),
                    landkode = it.postadresse.ustrukturertAdresse.landkode.value,
                    fom = it.periode.fom.asDate(),
                    tom = it.periode.tom.asDate(),
                    adresseType = Adressetype.POST

            )
    }

    val midlertidigAdresser: List<Adresse> = personhistorikkResponse.midlertidigAdressePeriodeListe.map {
        when(it) {
            is MidlertidigPostadresseNorge -> Adresse(
                    adresselinje = it.strukturertAdresse.tilleggsadresse ?: "Ukjent adresse",
                    landkode = it.strukturertAdresse.landkode.value,
                    fom = it.postleveringsPeriode.fom.asDate(),
                    tom = it.postleveringsPeriode.tom.asDate(),
                    adresseType = Adressetype.TIAD
            )
            is MidlertidigPostadresseUtland -> Adresse(
                    adresselinje = it.ustrukturertAdresse.adresselinje(),
                    landkode = it.ustrukturertAdresse.landkode.value,
                    fom = it.postleveringsPeriode.fom.asDate(),
                    tom = it.postleveringsPeriode.tom.asDate(),
                    adresseType = Adressetype.UTAD
            )
            else -> throw Exception("Ukjent adressetype")
        }
    }

    return Personhistorikk(statsborgerskap, personstatuser, bostedsadresser, postadresser, midlertidigAdresser)
}

private fun UstrukturertAdresse?.adresselinje(): String {
    if (this == null) {
        return "Ukjent adresse"
    }

    return "${this.adresselinje1} ${this.adresselinje2}"
}

private fun XMLGregorianCalendar?.asDate(): LocalDate? = this?.toGregorianCalendar()?.toZonedDateTime()?.toLocalDate()
