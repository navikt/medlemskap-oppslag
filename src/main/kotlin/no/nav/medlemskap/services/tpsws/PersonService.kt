package no.nav.medlemskap.services.tpsws

import no.nav.medlemskap.common.exceptions.PersonIkkeFunnet
import no.nav.medlemskap.common.exceptions.Sikkerhetsbegrensing
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkSikkerhetsbegrensning
import java.time.LocalDate

class PersonService(private val personClient: PersonClient) {

    suspend fun personhistorikk(fnr: String, fom: LocalDate) =
            try {
                mapPersonhistorikkResultat(personClient.hentPersonHistorikk(fnr, fom))
            } catch (err: Exception) {
                throw when (err) {
                    is HentPersonhistorikkPersonIkkeFunnet -> PersonIkkeFunnet(err, "TPS")
                    is HentPersonhistorikkSikkerhetsbegrensning -> Sikkerhetsbegrensing(err, "TPS")
                    else -> err
                }
            }
}

