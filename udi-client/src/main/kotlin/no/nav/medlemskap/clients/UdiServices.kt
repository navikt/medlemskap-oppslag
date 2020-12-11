package no.nav.medlemskap.clients

import no.nav.medlemskap.clients.udi.UdiClient
import no.nav.medlemskap.clients.udi.sts.stsClient
import no.nav.medlemskap.services.udi.UdiService
import no.nav.medlemskap.wsClients.WsClients

class UdiServices(val configuration: Configuration) {

    private val udiClient: UdiClient
    val udiService: UdiService

    init {
        val stsWsClient = stsClient(
                stsUrl = configuration.sts.endpointUrl,
                username = configuration.sts.username,
                password = configuration.sts.password
        )

        val wsClients = WsClients(
                stsClientWs = stsWsClient,
                callIdGenerator = callIdGenerator::get
        )

        udiClient = wsClients.oppholdstillatelse(configuration.register.udiBaseUrl)
        udiService = UdiService(udiClient)

    }
}
