package no.nav.medlemskap.services

import no.nav.medlemskap.common.callIdGenerator
import no.nav.medlemskap.configuration
import no.nav.medlemskap.services.aareg.AaRegClient
import no.nav.medlemskap.services.inntekt.InntektClient
import no.nav.medlemskap.services.medl.MedlClient
import no.nav.medlemskap.services.sts.StsRestClient
import no.nav.medlemskap.services.sts.stsClient
import no.nav.medlemskap.services.tpsws.PersonService

object Services {

    var personService: PersonService
    val medlClient: MedlClient
    val aaRegClient: AaRegClient
    val inntektClient: InntektClient

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

        val restClients = RestClients(
                stsClientRest = stsRestClient,
                callIdGenerator = callIdGenerator::get
        )

        personService = PersonService(wsClients.person(configuration.register.tpsUrl))
        medlClient = restClients.medl2(configuration.register.medl2BaseUrl)
        aaRegClient = restClients.aaReg(configuration.register.aaRegBaseUrl)
        inntektClient = restClients.inntektskomponenten(configuration.register.inntektBaseUrl)
    }

}
