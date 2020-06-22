package no.nav.medlemskap.services.tpsws

import mu.KotlinLogging
import no.nav.medlemskap.domene.*
import no.nav.tjeneste.virksomhet.person.v3.informasjon.MidlertidigPostadresseNorge
import no.nav.tjeneste.virksomhet.person.v3.informasjon.MidlertidigPostadresseUtland
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personstatuser
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkResponse
import java.time.LocalDate
import javax.xml.datatype.XMLGregorianCalendar

private val logger = KotlinLogging.logger { }

fun mapPersonhistorikkResultat(personhistorikkResponse: HentPersonhistorikkResponse): Personhistorikk {
    val statsborgerskap: List<Statsborgerskap> = personhistorikkResponse.statsborgerskapListe.map {
        Statsborgerskap(
                landkode = it.statsborgerskap.land.value,
                fom = it.periode.fom.asDate(),
                tom = it.periode.tom.asDate()
        )
    }

    val personstatuser: List<FolkeregisterPersonstatus> = personhistorikkResponse.personstatusListe.map {
        FolkeregisterPersonstatus(
                personstatus = mapPersonstatus(it.personstatus),
                fom = it.periode.fom.asDate(),
                tom = it.periode.tom.asDate()
        )
    }

    val bostedsadresser: List<Adresse> = personhistorikkResponse.bostedsadressePeriodeListe.map {
        Adresse(
                landkode = it.bostedsadresse.strukturertAdresse.landkode.value,
                fom = it.periode.fom.asDate(),
                tom = it.periode.tom.asDate()
        )
    }

    val postadresser: List<Adresse> = personhistorikkResponse.postadressePeriodeListe.map {
        Adresse(
                landkode = it.postadresse.ustrukturertAdresse.landkode.value,
                fom = it.periode.fom.asDate(),
                tom = it.periode.tom.asDate()
        )
    }

    val midlertidigAdresser: List<Adresse> = personhistorikkResponse.midlertidigAdressePeriodeListe.map {
        when (it) {
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

    val sivilstand: List<Sivilstand> = emptyList()

    val familierelasjoner: List<Familierelasjon> = emptyList()

    return Personhistorikk(statsborgerskap, personstatuser, bostedsadresser, postadresser, midlertidigAdresser, sivilstand, familierelasjoner)
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
