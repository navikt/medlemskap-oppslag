package no.nav.medlemskap.services

import no.nav.medlemskap.common.callIdGenerator
import no.nav.medlemskap.common.healthcheck.HealthReporter
import no.nav.medlemskap.common.healthcheck.HealthService
import no.nav.medlemskap.common.healthcheck.HttpResponseHealthCheck
import no.nav.medlemskap.common.healthcheck.TryCatchHealthCheck
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.config.retryRegistry
import no.nav.medlemskap.services.aareg.AaRegClient
import no.nav.medlemskap.services.inntekt.InntektClient
import no.nav.medlemskap.services.medl.MedlClient
import no.nav.medlemskap.services.oppgave.OppgaveClient
import no.nav.medlemskap.services.pdl.PdlClient
import no.nav.medlemskap.services.pdl.PdlService
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
    val pdlClient: PdlClient
    val pdlService : PdlService

    private val stsRetry = retryRegistry.retry("STS")
    private val tpsRetry = retryRegistry.retry("TPS")

    init {
        val stsWsClient = stsClient(
                stsUrl = configuration.sts.endpointUrl,
                username = configuration.sts.username,
                password = configuration.sts.password
        )

        val stsRestClient = StsRestClient(
                baseUrl = configuration.sts.restUrl,
                username = configuration.sts.username,
                password = configuration.sts.password,
                retry = stsRetry
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

        personService = PersonService(wsClients.person(configuration.register.tpsUrl, tpsRetry))
        medlClient = restClients.medl2(configuration.register.medl2BaseUrl)
        aaRegClient = restClients.aaReg(configuration.register.aaRegBaseUrl)
        inntektClient = restClients.inntektskomponenten(configuration.register.inntektBaseUrl)
        safClient = restClients.saf(configuration.register.safBaseUrl)
        oppgaveClient = restClients.oppgaver(configuration.register.oppgaveBaseUrl)
        pdlClient = restClients.pdl(configuration.register.pdlBaseUrl)
        pdlService = PdlService(pdlClient)

        healthService = HealthService(setOf(
                HttpResponseHealthCheck("AaReg", { aaRegClient.healthCheck() }),
                HttpResponseHealthCheck("Inntekt", { inntektClient.healthCheck() }),
                HttpResponseHealthCheck("Medl", { medlClient.healthCheck() }),
                HttpResponseHealthCheck("Oppg", { oppgaveClient.healthCheck() }),
                HttpResponseHealthCheck("PDL", { pdlClient.healthCheck() }),
                HttpResponseHealthCheck("SAF", { safClient.healthCheck() }),
                HttpResponseHealthCheck("STS", { stsRestClient.healthCheck() }),
                TryCatchHealthCheck("TPS", { personService.healthCheck() })
        ))

        healthReporter = HealthReporter(healthService)
    }

}
