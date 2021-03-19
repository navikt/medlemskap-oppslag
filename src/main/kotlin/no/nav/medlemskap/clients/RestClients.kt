package no.nav.medlemskap.clients

import no.nav.medlemskap.clients.aareg.AaRegClient
import no.nav.medlemskap.clients.ereg.EregClient
import no.nav.medlemskap.clients.medl.MedlClient
import no.nav.medlemskap.clients.oppgave.OppgaveClient
import no.nav.medlemskap.clients.pdl.PdlClient
import no.nav.medlemskap.clients.saf.SafClient
import no.nav.medlemskap.clients.sts.StsRestClient
import no.nav.medlemskap.common.cioHttpClient
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.config.retryRegistry

class RestClients(
    private val stsClientRest: StsRestClient,
    private val configuration: Configuration
) {

    private val aaRegRetry = retryRegistry.retry("AaReg")
    private val medlRetry = retryRegistry.retry("Medl")
    private val oppgaveRetry = retryRegistry.retry("Oppgave")
    private val pdlRetry = retryRegistry.retry("PDL")
    private val safRetry = retryRegistry.retry("Saf")
    private val eregRetry = retryRegistry.retry("Ereg")

    private val httpClient = cioHttpClient

    fun aaReg(endpointUrl: String) = AaRegClient(endpointUrl, configuration.sts.username, stsClientRest, httpClient, configuration.register.aaRegApiKey, aaRegRetry)
    fun medl2(endpointBaseUrl: String) = MedlClient(endpointBaseUrl, stsClientRest, configuration, httpClient, configuration.register.medl2ApiKey, medlRetry)
    fun saf(endpointBaseUrl: String) = SafClient(endpointBaseUrl, stsClientRest, configuration.sts.username, httpClient, configuration.register.safApiKey, safRetry)
    fun oppgaver(endpointBaseUrl: String) = OppgaveClient(endpointBaseUrl, stsClientRest, httpClient, configuration.register.oppgaveApiKey, oppgaveRetry)
    fun pdl(endpointBaseURl: String) = PdlClient(endpointBaseURl, stsClientRest, configuration.sts.username, httpClient, pdlRetry, configuration.register.pdlApiKey)
    fun ereg(endpointBaseUrl: String) = EregClient(endpointBaseUrl, httpClient, configuration, configuration.register.eregApiKey, eregRetry)
}
