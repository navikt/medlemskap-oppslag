package no.nav.medlemskap.services

import no.nav.medlemskap.common.callIdGenerator
import no.nav.medlemskap.common.healthcheck.HealthReporter
import no.nav.medlemskap.common.healthcheck.HealthService
import no.nav.medlemskap.common.healthcheck.HttpResponseHealthCheck
import no.nav.medlemskap.common.healthcheck.TryCatchHealthCheck
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.services.aareg.AaRegClient
import no.nav.medlemskap.services.inntekt.InntektClient
import no.nav.medlemskap.services.medl.MedlClient
import no.nav.medlemskap.services.oppgave.OppgaveClient
import no.nav.medlemskap.services.saf.SafClient
import no.nav.medlemskap.services.sts.StsRestClient
import no.nav.medlemskap.services.sts.stsClient
import no.nav.medlemskap.services.tpsws.PersonService

class Services(val configuration: Configuration) {

    val personService: PersonService
    val medlClient: MedlClient
    val aaRegClient: AaRegClient
    val inntektClient: InntektClient
    val safClient: SafClient
    val oppgaveClient: OppgaveClient
    val healthService: HealthService
    val healthReporter: HealthReporter

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
                callIdGenerator = callIdGenerator::get,
                configuration = configuration
        )

        personService = PersonService(wsClients.person(configuration.register.tpsUrl))
        medlClient = restClients.medl2(configuration.register.medl2BaseUrl)
        aaRegClient = restClients.aaReg(configuration.register.aaRegBaseUrl)
        inntektClient = restClients.inntektskomponenten(configuration.register.inntektBaseUrl)
        safClient = restClients.saf(configuration.register.safBaseUrl)
        oppgaveClient = restClients.oppgaver(configuration.register.oppgaveBaseUrl)

        healthService = HealthService(setOf(
                HttpResponseHealthCheck("Gosys", { oppgaveClient.healthCheckQuery() }),
                HttpResponseHealthCheck("AaReg", { aaRegClient.healthCheckQuery() }),
                TryCatchHealthCheck("TPS", { personService.healthCheckQuery() })
        ))

        healthReporter = HealthReporter(healthService)
    }

}
