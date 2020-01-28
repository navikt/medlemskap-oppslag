package no.nav.medlemskap.services.tpsws

import no.nav.medlemskap.common.exceptions.PersonBleIkkeFunnet
import no.nav.medlemskap.common.exceptions.PersonHarSikkerhetsbegrensing
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkSikkerhetsbegrensning

class PersonService(private val personClient: PersonClient) {

    fun personhistorikk(fnr: String) =
            try {
                personClient.hentPersonHistorikk(fnr)
            } catch (err: Exception) {
                throw when (err) {
                    is HentPersonhistorikkPersonIkkeFunnet -> PersonBleIkkeFunnet(err, "TPS")
                    is HentPersonhistorikkSikkerhetsbegrensning -> PersonHarSikkerhetsbegrensing(err, "TPS")
                    else -> err
                }
            }
}

