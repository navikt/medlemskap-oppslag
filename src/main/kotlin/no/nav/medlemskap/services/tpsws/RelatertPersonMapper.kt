package no.nav.medlemskap.services.tpsws

import no.nav.medlemskap.domene.PersonAdresse
import no.nav.medlemskap.domene.PersonhistorikkRelatertPerson
import no.nav.medlemskap.domene.PersonstatusType
import no.nav.medlemskap.domene.RelatertPersonstatus
import no.nav.tjeneste.virksomhet.person.v3.informasjon.MidlertidigPostadresseNorge
import no.nav.tjeneste.virksomhet.person.v3.informasjon.MidlertidigPostadresseUtland
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkResponse
import java.time.LocalDate
import javax.xml.datatype.XMLGregorianCalendar

fun mapPersonhistorikkRelatertPersonResultat(personhistorikkRelatertPerson: HentPersonhistorikkResponse): PersonhistorikkRelatertPerson {
    val personstatuser: List<RelatertPersonstatus> = personhistorikkRelatertPerson.personstatusListe.map {
        RelatertPersonstatus(
                folkeregisterPersonstatus = mapPersonstatusType(it.personstatus.value),
                fom = it.periode.fom.asDate(),
                tom = it.periode.tom.asDate()
        )
    }

    val bostedsadresse: List<PersonAdresse> = personhistorikkRelatertPerson.bostedsadressePeriodeListe.map {
        PersonAdresse(
                landkode = it.bostedsadresse.strukturertAdresse.landkode.value,
                fom = it.periode.fom.asDate(),
                tom = it.periode.tom.asDate()
        )
    }

    val postadresser: List<PersonAdresse> = personhistorikkRelatertPerson.postadressePeriodeListe.map {
        PersonAdresse(
                landkode = it.postadresse.ustrukturertAdresse.landkode.value,
                fom = it.periode.fom.asDate(),
                tom = it.periode.tom.asDate()
        )
    }

    val midlertidigAdresser: List<PersonAdresse> = personhistorikkRelatertPerson.midlertidigAdressePeriodeListe.map {
        when(it) {
            is MidlertidigPostadresseNorge -> PersonAdresse(
                    landkode = it.strukturertAdresse.landkode.value,
                    fom = it.postleveringsPeriode.fom.asDate(),
                    tom = it.postleveringsPeriode.tom.asDate()
            )
            is MidlertidigPostadresseUtland -> PersonAdresse(
                    landkode = it.ustrukturertAdresse.landkode.value,
                    fom = it.postleveringsPeriode.fom.asDate(),
                    tom = it.postleveringsPeriode.tom.asDate()
            )
            else -> throw Exception("Ukjent adressetype")
        }
    }


    return PersonhistorikkRelatertPerson(personstatuser, bostedsadresse, postadresser, midlertidigAdresser)
}

fun mapPersonstatusType(value: String?): PersonstatusType {
    when (value) {
        "Aktivt BOSTNR" -> return PersonstatusType.ABNR
        "Aktivt" -> return PersonstatusType.ADNR
        "Bosatt" -> return PersonstatusType.BOSA
        "Død" -> return PersonstatusType.DØD
        "Fødselregistrert" -> return PersonstatusType.FØDR
        "Forsvunnet/savnet" -> return PersonstatusType.FOSV
        "Ufullstendig fødselsnr" -> return PersonstatusType.UFUL
        "Uregistrert person" -> return PersonstatusType.UREG
        "Utgått person annullert tilgang Fnr" -> return PersonstatusType.UTAN
        "Utgått person" -> return PersonstatusType.UTPE
        "Utvandret" -> return PersonstatusType.UTVA
        else -> {
            return PersonstatusType.UKJENT
        }
    }
}

private fun XMLGregorianCalendar?.asDate(): LocalDate? = this?.toGregorianCalendar()?.toZonedDateTime()?.toLocalDate()
