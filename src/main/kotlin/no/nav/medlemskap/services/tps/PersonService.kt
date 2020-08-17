package no.nav.medlemskap.services.tps

import mu.KotlinLogging
import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.medlemskap.clients.tpsws.PersonClient
import no.nav.medlemskap.common.exceptions.PersonIkkeFunnet
import no.nav.medlemskap.common.exceptions.Sikkerhetsbegrensing
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.services.pdl.PdlService
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkSikkerhetsbegrensning
import java.time.LocalDate
import java.util.*
import javax.xml.ws.soap.SOAPFaultException

class PersonService(private val personClient: PersonClient, private val pdlService: PdlService) {

    suspend fun personhistorikk(fnr: String, fom: LocalDate) =
            try {
                mapPersonhistorikkResultat(personClient.hentPersonHistorikk(fnr, fom))
            } catch (err: Exception) {
                when (err) {
                    is HentPersonhistorikkPersonIkkeFunnet -> {
                        secureLogger.info("Fikk HentPersonhistorikkPersonIkkeFunnet med fnr {} og fom {}", fnr, fom)
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
                    .filter { FodselsnummerValidator.isValid(it.relatertVedSivilstand) }
                    .mapNotNull { personhistorikkRelatertPerson(it.relatertVedSivilstand!!, fom) }

    private suspend fun hentPersonhistorikkBarn(familierelasjoner: List<Familierelasjon>, periode: InputPeriode): List<PersonhistorikkRelatertPerson> {
        return familierelasjoner
                .filter { FodselsnummerValidator.isValid(it.relatertPersonsIdent) }
                .filter { it.erBarn() }
                .filter { it.erUnder18aar(periode.tom.year) }
                .mapNotNull { personhistorikkRelatertPerson(it.relatertPersonsIdent, periode.fom) }
    }

    private suspend fun personhistorikkRelatertPerson(fnr: String, fom: LocalDate): PersonhistorikkRelatertPerson? =
            try {
                mapPersonhistorikkRelatertPersonResultat(fnr, personClient.hentPersonHistorikk(fnr, fom))
            } catch (err: Exception) {
                if ((err is HentPersonhistorikkPersonIkkeFunnet || err is SOAPFaultException) && err.message != null && err.message!!.contains("S016007F")) {
                    //javax.xml.ws.soap.SOAPFaultException: TPS svarte med FEIL, folgende status: S016007F og folgende melding: FØDSELSNR ER IKKE ENTYDIG
                    logger.warn("Fikk en feil med ikke entydig fnr, de skal helst ikke komme!")
                    secureLogger.warn("Fnr {} er visst ikke entydig i TPS", fnr)
                    null
                } else {
                    throw when (err) {
                        is HentPersonhistorikkPersonIkkeFunnet -> PersonIkkeFunnet(err, "TPS")
                        is HentPersonhistorikkSikkerhetsbegrensning -> Sikkerhetsbegrensing(err, "TPS")
                        else -> err
                    }
                }
            }

    private suspend fun Familierelasjon.erUnder18aar(aarstallAlderSkalVurderes: Int): Boolean {

        val foedselsaarTilRelatertPerson = pdlService.hentFoedselsaar(relatertPersonsIdent, UUID.randomUUID().toString())
        return (aarstallAlderSkalVurderes - foedselsaarTilRelatertPerson) < ALDERSGRENSE
    }

    private fun Familierelasjon.erBarn() = this.relatertPersonsRolle == Familierelasjonsrolle.BARN

    companion object {
        const val ALDERSGRENSE = 26
        private val logger = KotlinLogging.logger { }
        private val secureLogger = KotlinLogging.logger("tjenestekall")
    }
}

