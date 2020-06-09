package no.nav.medlemskap.services.tpsws

import no.nav.medlemskap.domene.PersonAdresse
import no.nav.medlemskap.domene.PersonhistorikkRelatertPerson
import no.nav.medlemskap.domene.RelatertPersonstatus
import no.nav.tjeneste.virksomhet.person.v3.informasjon.MidlertidigPostadresseNorge
import no.nav.tjeneste.virksomhet.person.v3.informasjon.MidlertidigPostadresseUtland
import no.nav.tjeneste.virksomhet.person.v3.informasjon.UstrukturertAdresse
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkResponse
import java.time.LocalDate
import javax.xml.datatype.XMLGregorianCalendar

fun mapPersonhistorikkRelatertPersonResultat(personhistorikkRelatertPerson: HentPersonhistorikkResponse): PersonhistorikkRelatertPerson {
    val personstatuser: List<RelatertPersonstatus> = personhistorikkRelatertPerson.personstatusListe.map {
        RelatertPersonstatus(
                personstatus = it.personstatus.value,
                fom = it.periode.fom.asDate(),
                tom = it.periode.tom.asDate()
        )
    }

    val bostedsadresse: List<PersonAdresse> = personhistorikkRelatertPerson.bostedsadressePeriodeListe.map {
        PersonAdresse(
                adresselinje = it.bostedsadresse.strukturertAdresse.tilleggsadresse ?: "Ukjent adresse",
                landkode = it.bostedsadresse.strukturertAdresse.landkode.value,
                fom = it.periode.fom.asDate(),
                tom = it.periode.tom.asDate()
        )
    }

    val postadresser: List<PersonAdresse> = personhistorikkRelatertPerson.postadressePeriodeListe.map {
        PersonAdresse(
                adresselinje = it.postadresse.ustrukturertAdresse.adresselinje(),
                landkode = it.postadresse.ustrukturertAdresse.landkode.value,
                fom = it.periode.fom.asDate(),
                tom = it.periode.tom.asDate()
        )
    }

    val midlertidigAdresser: List<PersonAdresse> = personhistorikkRelatertPerson.midlertidigAdressePeriodeListe.map {
        when(it) {
            is MidlertidigPostadresseNorge -> PersonAdresse(
                    adresselinje = it.strukturertAdresse.tilleggsadresse ?: "Ukjent adresse",
                    landkode = it.strukturertAdresse.landkode.value,
                    fom = it.postleveringsPeriode.fom.asDate(),
                    tom = it.postleveringsPeriode.tom.asDate()
            )
            is MidlertidigPostadresseUtland -> PersonAdresse(
                    adresselinje = it.ustrukturertAdresse.adresselinje(),
                    landkode = it.ustrukturertAdresse.landkode.value,
                    fom = it.postleveringsPeriode.fom.asDate(),
                    tom = it.postleveringsPeriode.tom.asDate()
            )
            else -> throw Exception("Ukjent adressetype")
        }
    }


    return PersonhistorikkRelatertPerson(personstatuser, bostedsadresse, postadresser, midlertidigAdresser)
}

private fun UstrukturertAdresse?.adresselinje(): String {
    if (this == null) {
        return "Ukjent adresse"
    }

    return "${this.adresselinje1} ${this.adresselinje2}"
}

private fun XMLGregorianCalendar?.asDate(): LocalDate? = this?.toGregorianCalendar()?.toZonedDateTime()?.toLocalDate()
