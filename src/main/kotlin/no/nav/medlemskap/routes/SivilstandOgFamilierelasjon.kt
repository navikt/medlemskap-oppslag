package no.nav.medlemskap.routes

import mu.KotlinLogging
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.services.Services
import java.util.*

private val logger = KotlinLogging.logger { }


suspend fun barnUnder18aar(familierelasjon: Familierelasjon, services: Services, periode: InputPeriode): Boolean {
    val aarstall = periode.tom.year
    val aldersGrense = 18
    val hentFoedselsaarTilBarn = services.pdlService.hentFoedselsaar(familierelasjon.relatertPersonIdent, UUID.randomUUID().toString())

    return (aarstall - hentFoedselsaarTilBarn) < aldersGrense
}

suspend fun hentRelatertPersonHistorikk(familierelasjon: Familierelasjon, periode: InputPeriode, services: Services): PersonhistorikkRelatertPerson {
    return try {
        services.personService.personhistorikkRelatertPerson(familierelasjon.relatertPersonIdent, periode.fom)
    } catch (e: Exception) {
        logger.error { e }
        PersonhistorikkRelatertPerson(
                bostedsadresser = emptyList(),
                personstatuser = emptyList(),
                postadresser = emptyList(),
                midlertidigAdresser = emptyList()
        )
    }
}

suspend fun folkeregistrertSivilstand(personhistorikk: Personhistorikk, periode: InputPeriode, services: Services) : List<PersonhistorikkRelatertPerson> {
    return personhistorikk.sivilstand.map { services.personService.personhistorikkRelatertPerson(it.relatertVedSivilstand, periode.fom) }
}

suspend fun folkeregistrertFamilierelasjonBarn(personhistorikk: Personhistorikk, periode: InputPeriode, services: Services): List<PersonhistorikkRelatertPerson> {
    return personhistorikk.familierelasjoner
            .filter { it.erBarn() }
            .filter { barnUnder18aar(it, services, periode) }
            .map { hentRelatertPersonHistorikk(it, periode, services) }
}

private fun Familierelasjon.erBarn() = this.relatertPersonsRolle == Familierelasjonsrolle.BARN
