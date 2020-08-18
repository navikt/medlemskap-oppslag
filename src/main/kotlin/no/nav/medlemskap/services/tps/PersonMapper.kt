package no.nav.medlemskap.services.tps

import mu.KotlinLogging
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Familierelasjon
import no.nav.medlemskap.domene.Sivilstand
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkResponse
import java.time.LocalDate
import javax.xml.datatype.XMLGregorianCalendar

private val logger = KotlinLogging.logger { }

fun mapPersonhistorikkResultat(personhistorikkResponse: HentPersonhistorikkResponse): Personhistorikk {
    val statsborgerskap: List<Statsborgerskap> = personhistorikkResponse.statsborgerskapListe.map {
        mapStatsborgerskapPeriode(it)
    }

    val personstatuser: List<FolkeregisterPersonstatus> = personhistorikkResponse.personstatusListe.map {
        mapPersonstatusPeriode(it)
    }

    val bostedsadresser: List<Adresse> = personhistorikkResponse.bostedsadressePeriodeListe.map {
        mapBostedsadressePeriode(it)
    }

    val postadresser: List<Adresse> = personhistorikkResponse.postadressePeriodeListe.map {
        mapPostadressePeriode(it)
    }

    val midlertidigAdresser: List<Adresse> = personhistorikkResponse.midlertidigAdressePeriodeListe.map {
        mapMidlertidigPostadresse(it)
    }

    val sivilstand: List<Sivilstand> = emptyList()

    val familierelasjoner: List<Familierelasjon> = emptyList()

    return Personhistorikk(statsborgerskap, personstatuser, bostedsadresser, postadresser, midlertidigAdresser, sivilstand, familierelasjoner)
}

fun mapPersonhistorikkRelatertPersonResultat(ident: String, personhistorikkRelatertPerson: HentPersonhistorikkResponse): PersonhistorikkRelatertPerson {

    val personstatuser: List<FolkeregisterPersonstatus> = personhistorikkRelatertPerson.personstatusListe.map {
        mapPersonstatusPeriode(it)
    }

    val bostedsadresser: List<Adresse> = personhistorikkRelatertPerson.bostedsadressePeriodeListe.map {
        mapBostedsadressePeriode(it)
    }

    val postadresser: List<Adresse> = personhistorikkRelatertPerson.postadressePeriodeListe.map {
        mapPostadressePeriode(it)
    }

    val midlertidigAdresser: List<Adresse> = personhistorikkRelatertPerson.midlertidigAdressePeriodeListe.map {
        mapMidlertidigPostadresse(it)
    }

    return PersonhistorikkRelatertPerson(ident, personstatuser, bostedsadresser, postadresser, midlertidigAdresser)
}

fun mapMidlertidigPostadresse(it: MidlertidigPostadresse?): Adresse {
    return when (it) {
        is MidlertidigPostadresseNorge -> Adresse(
                landkode = it.strukturertAdresse.landkode.value,
                fom = it.postleveringsPeriode.fom.asDate(),
                tom = it.postleveringsPeriode.tom.asDate()
        )
        is MidlertidigPostadresseUtland -> Adresse(
                landkode = it.ustrukturertAdresse.landkode.value,
                fom = it.postleveringsPeriode.fom.asDate(),
                tom = it.postleveringsPeriode.tom.asDate()
        )
        else -> throw Exception("Ukjent adressetype")
    }
}

fun mapStatsborgerskapPeriode(it: StatsborgerskapPeriode): Statsborgerskap {
    return Statsborgerskap(
            landkode = it.statsborgerskap.land.value,
            fom = it.periode.fom.asDate(),
            tom = it.periode.tom.asDate()
    )
}

fun mapPostadressePeriode(it: PostadressePeriode): Adresse {
    return Adresse(
            landkode = it.postadresse.ustrukturertAdresse.landkode.value,
            fom = it.periode.fom.asDate(),
            tom = it.periode.tom.asDate()
    )
}

fun mapBostedsadressePeriode(it: BostedsadressePeriode): Adresse {
    return Adresse(
            landkode = it.bostedsadresse.strukturertAdresse.landkode.value,
            fom = it.periode.fom.asDate(),
            tom = it.periode.tom.asDate()
    )
}

fun mapPersonstatusPeriode(it: PersonstatusPeriode): FolkeregisterPersonstatus {
    return FolkeregisterPersonstatus(
            personstatus = mapPersonstatus(it.personstatus),
            fom = it.periode.fom.asDate(),
            tom = it.periode.tom.asDate()
    )
}

fun mapPersonstatus(status: Personstatuser): PersonStatus {
    return try {
        status.let { PersonStatus.valueOf(it.value) }
    } catch (e: Exception) {
        logger.warn { "Klarte ikke å mappe personStatus " + status }
        PersonStatus.UKJENT
    }
}

private fun XMLGregorianCalendar?.asDate(): LocalDate? = this?.toGregorianCalendar()?.toZonedDateTime()?.toLocalDate()


