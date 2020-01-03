package no.nav.medlemskap.services

import no.nav.medlemskap.common.callIdGenerator
import no.nav.medlemskap.configuration
import no.nav.medlemskap.services.sts.StsRestClient
import no.nav.medlemskap.services.sts.stsClient
import no.nav.medlemskap.services.tpsws.PersonService

object Services {

    var personService: PersonService

    init {
        val stsWsClient = stsClient(
                stsUrl = configuration.sts.endpointUrl,
                username = configuration.sts.username,
                password = configuration.sts.password
        )

        val stsRestClient = StsRestClient(
                baseUrl = configuration.sts.restUrl,
                username = configuration.sts.username,
                password = configuration.sts.password
        )

        val wsClients = WsClients(
                stsClientWs = stsWsClient,
                stsClientRest = stsRestClient,
                callIdGenerator = callIdGenerator::get
        )

        personService = PersonService(wsClients.person(configuration.register.tpsUrl))
    }

}
