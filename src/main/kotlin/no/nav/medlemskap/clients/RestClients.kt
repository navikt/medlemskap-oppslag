package no.nav.medlemskap.clients

import no.nav.medlemskap.clients.aareg.AaRegClient
import no.nav.medlemskap.clients.azuread.AzureAdClient
import no.nav.medlemskap.clients.ereg.EregClient
import no.nav.medlemskap.clients.medl.MedlClient
import no.nav.medlemskap.clients.oppgave.OppgaveClient
import no.nav.medlemskap.clients.pdl.PdlClient
import no.nav.medlemskap.clients.saf.SafClient
import no.nav.medlemskap.clients.udi.UdiClient
import no.nav.medlemskap.common.cioHttpClient
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.config.retryRegistry

class RestClients(
    private val azureAdClient: AzureAdClient,
    private val configuration: Configuration
) {

    private val aaRegRetry = retryRegistry.retry("AaReg")
    private val medlRetry = retryRegistry.retry("Medl")
    private val oppgaveRetry = retryRegistry.retry("Oppgave")
    private val pdlRetry = retryRegistry.retry("PDL")
    private val safRetry = retryRegistry.retry("Saf")
    private val eregRetry = retryRegistry.retry("Ereg")
    private val udiRetry = retryRegistry.retry("UDI")

    private val httpClient = cioHttpClient

    fun aaReg(endpointUrl: String) = AaRegClient(endpointUrl, azureAdClient, httpClient, configuration, aaRegRetry)
    fun medl2(endpointBaseUrl: String) = MedlClient(endpointBaseUrl, azureAdClient, configuration, httpClient, medlRetry)
    fun saf(endpointBaseUrl: String) = SafClient(endpointBaseUrl, azureAdClient, configuration, httpClient, safRetry)
    fun oppgaver(endpointBaseUrl: String) = OppgaveClient(endpointBaseUrl, azureAdClient, configuration, httpClient, oppgaveRetry)
    fun pdl(endpointBaseURl: String) = PdlClient(endpointBaseURl, azureAdClient, configuration, httpClient, pdlRetry)
    fun ereg(endpointBaseUrl: String) = EregClient(endpointBaseUrl, httpClient, configuration, eregRetry)
    fun udi(endpointBaseUrl: String) = UdiClient(endpointBaseUrl, azureAdClient, httpClient, udiRetry)
}
