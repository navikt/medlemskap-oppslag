package no.nav.medlemskap.services.tpsws

import mu.KotlinLogging
import no.nav.medlemskap.common.exceptions.PersonIkkeFunnet
import no.nav.medlemskap.common.exceptions.Sikkerhetsbegrensing
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.services.pdl.PdlService
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkSikkerhetsbegrensning
import java.time.LocalDate
import java.util.*

class PersonService(private val personClient: PersonClient, private val pdlService: PdlService) {

    suspend fun personhistorikk(fnr: String, fom: LocalDate) =
            try {
                mapPersonhistorikkResultat(personClient.hentPersonHistorikk(fnr, fom))
            } catch (err: Exception) {
                when (err) {
                    is HentPersonhistorikkPersonIkkeFunnet -> {
                        Companion.secureLogger.info("Fikk HentPersonhistorikkPersonIkkeFunnet med fnr {} og fom {}", fnr, fom)
                        throw PersonIkkeFunnet(err, "TPS")
                    }
                    is HentPersonhistorikkSikkerhetsbegrensning -> throw Sikkerhetsbegrensing(err, "TPS")
                    else -> throw err
                }
            }

    suspend fun hentPersonhistorikkForRelevantFamilie(personHistorikkFraPdl: Personhistorikk, periode: InputPeriode): List<PersonhistorikkRelatertPerson> {
        val personHistorikkForBarn = hentPersonhistorikkBarn(personHistorikkFraPdl.familierelasjoner, periode)
        val personhistorikkRelatertPersonGjennomSivilstand = hentPersonhistorikkSivilstand(personHistorikkFraPdl.sivilstand, periode.fom)

        return personHistorikkForBarn.plus(personhistorikkRelatertPersonGjennomSivilstand)
    }

    private suspend fun hentPersonhistorikkSivilstand(sivilstand: List<Sivilstand>, fom: LocalDate): List<PersonhistorikkRelatertPerson> =
            sivilstand
                    .filter { it.relatertVedSivilstand != null }
                    .map { personhistorikkRelatertPerson(it.relatertVedSivilstand!!, fom) }

    private suspend fun hentPersonhistorikkBarn(familierelasjoner: List<Familierelasjon>, periode: InputPeriode): List<PersonhistorikkRelatertPerson> {
        return familierelasjoner
                .filter { it.erBarn() }
                .filter { it.erUnder18aar(periode.tom.year) }
                .map { personhistorikkRelatertPerson(it.relatertPersonsIdent, periode.fom) }
    }

    private suspend fun personhistorikkRelatertPerson(fnr: String, fom: LocalDate) =
            try {
                mapPersonhistorikkRelatertPersonResultat(personClient.hentPersonHistorikk(fnr, fom))
            } catch (err: Exception) {
                throw when (err) {
                    is HentPersonhistorikkPersonIkkeFunnet -> PersonIkkeFunnet(err, "TPS")
                    is HentPersonhistorikkSikkerhetsbegrensning -> Sikkerhetsbegrensing(err, "TPS")
                    else -> err
                }
            }

    private suspend fun Familierelasjon.erUnder18aar(aarstallAlderSkalVurderes: Int): Boolean {

        val foedselsaarTilRelatertPerson = pdlService.hentFoedselsaar(relatertPersonsIdent, UUID.randomUUID().toString())
        return (aarstallAlderSkalVurderes - foedselsaarTilRelatertPerson) < aldersGrense
    }

    private fun Familierelasjon.erBarn() = this.relatertPersonsRolle == Familierelasjonsrolle.BARN

    companion object {
        val aldersGrense = 26
        private val secureLogger = KotlinLogging.logger("tjenestekall")
    }
}

