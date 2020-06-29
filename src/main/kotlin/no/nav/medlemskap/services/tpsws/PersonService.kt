package no.nav.medlemskap.services.tpsws

import mu.KotlinLogging
import no.nav.medlemskap.common.exceptions.PersonIkkeFunnet
import no.nav.medlemskap.common.exceptions.Sikkerhetsbegrensing
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkSikkerhetsbegrensning
import java.time.LocalDate

class PersonService(private val personClient: PersonClient) {

    private val secureLogger = KotlinLogging.logger("tjenestekall")

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
}

