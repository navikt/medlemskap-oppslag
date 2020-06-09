package no.nav.medlemskap.services

import no.nav.medlemskap.common.callIdGenerator
import no.nav.medlemskap.common.cioHttpClient
import no.nav.medlemskap.common.healthcheck.HealthReporter
import no.nav.medlemskap.common.healthcheck.HealthService
import no.nav.medlemskap.common.healthcheck.HttpResponseHealthCheck
import no.nav.medlemskap.common.healthcheck.TryCatchHealthCheck
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.config.retryRegistry
import no.nav.medlemskap.services.aareg.AaRegClient
import no.nav.medlemskap.services.aareg.AaRegService
import no.nav.medlemskap.services.ereg.EregClient
import no.nav.medlemskap.services.inntekt.InntektClient
import no.nav.medlemskap.services.inntekt.InntektService
import no.nav.medlemskap.services.medl.MedlClient
import no.nav.medlemskap.services.medl.MedlService
import no.nav.medlemskap.services.oppgave.OppgaveClient
import no.nav.medlemskap.services.oppgave.OppgaveService
import no.nav.medlemskap.services.pdl.PdlClient
import no.nav.medlemskap.services.pdl.PdlService
import no.nav.medlemskap.services.saf.SafClient
import no.nav.medlemskap.services.saf.SafService
import no.nav.medlemskap.services.sts.StsRestClient
import no.nav.medlemskap.services.sts.stsClient
import no.nav.medlemskap.services.tpsws.PersonClient
import no.nav.medlemskap.services.tpsws.PersonService

class Services(val configuration: Configuration) {

    private val personClient: PersonClient
    val personService: PersonService
    private val medlClient: MedlClient
    val medlService: MedlService
    private val aaRegClient: AaRegClient
    val aaRegService: AaRegService
    private val inntektClient: InntektClient
    val inntektService: InntektService
    private val safClient: SafClient
    val safService: SafService
    private val oppgaveClient: OppgaveClient
    val oppgaveService: OppgaveService
    private val pdlClient: PdlClient
    val pdlService: PdlService
    private val eregClient: EregClient

    val healthService: HealthService
    private val healthReporter: HealthReporter
    private val healthRetry = retryRegistry.retry("Helsesjekker")

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
                retry = stsRetry,
                httpClient = cioHttpClient
        )

        val wsClients = WsClients(
                stsClientWs = stsWsClient,
                callIdGenerator = callIdGenerator::get
        )

        val restClients = RestClients(
                stsClientRest = stsRestClient,
                configuration = configuration
        )

        personClient = wsClients.person(configuration.register.tpsUrl, tpsRetry)
        personService = PersonService(personClient)
        medlClient = restClients.medl2(configuration.register.medl2BaseUrl)
        medlService = MedlService(medlClient)
        pdlClient = restClients.pdl(configuration.register.pdlBaseUrl)
        pdlService = PdlService(pdlClient)
        eregClient = restClients.ereg(configuration.register.eregBaseUrl)
        aaRegClient = restClients.aaReg(configuration.register.aaRegBaseUrl)
        aaRegService = AaRegService(aaRegClient, eregClient, pdlClient)
        inntektClient = restClients.inntektskomponenten(configuration.register.inntektBaseUrl)
        inntektService = InntektService(inntektClient)
        safClient = restClients.saf(configuration.register.safBaseUrl)
        safService = SafService(safClient)
        oppgaveClient = restClients.oppgaver(configuration.register.oppgaveBaseUrl)
        oppgaveService = OppgaveService(oppgaveClient)

        healthService = HealthService(setOf(
                //HttpResponseHealthCheck("AaReg", { aaRegClient.healthCheck() }),
                HttpResponseHealthCheck("Inntekt", { inntektClient.healthCheck() }, healthRetry),
                HttpResponseHealthCheck("Medl", { medlClient.healthCheck() }, healthRetry),
                HttpResponseHealthCheck("Oppg", { oppgaveClient.healthCheck() }, healthRetry),
                HttpResponseHealthCheck("PDL", { pdlClient.healthCheck() }, healthRetry),
                HttpResponseHealthCheck("SAF", { safClient.healthCheck() }, healthRetry),
                HttpResponseHealthCheck("STS", { stsRestClient.healthCheck() }, healthRetry),
                TryCatchHealthCheck("TPS", { personClient.healthCheck() }, healthRetry)
        ))

        healthReporter = HealthReporter(healthService)
    }

}
