package no.nav.medlemskap.services

import no.nav.medlemskap.common.cioHttpClient
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.config.retryRegistry
import no.nav.medlemskap.services.aareg.AaRegClient
import no.nav.medlemskap.services.ereg.EregClient
import no.nav.medlemskap.services.inntekt.InntektClient
import no.nav.medlemskap.services.medl.MedlClient
import no.nav.medlemskap.services.oppgave.OppgaveClient
import no.nav.medlemskap.services.pdl.PdlClient
import no.nav.medlemskap.services.saf.SafClient
import no.nav.medlemskap.services.sts.StsRestClient

class RestClients(private val stsClientRest: StsRestClient,
                  private val configuration: Configuration) {

    private val aaRegRetry = retryRegistry.retry("AaReg")
    private val inntektRetry = retryRegistry.retry("Inntekt")
    private val medlRetry = retryRegistry.retry("Medl")
    private val oppgaveRetry = retryRegistry.retry("Oppgave")
    private val pdlRetry = retryRegistry.retry("PDL")
    private val safRetry = retryRegistry.retry("Saf")
    private val eregRetry = retryRegistry.retry("Ereg")

    private val httpClient = cioHttpClient

    fun aaReg(endpointUrl: String) = AaRegClient(endpointUrl, stsClientRest, httpClient, aaRegRetry)
    fun medl2(endpointBaseUrl: String) = MedlClient(endpointBaseUrl, stsClientRest, configuration, httpClient, medlRetry)
    fun inntektskomponenten(endpointBaseUrl: String) = InntektClient(endpointBaseUrl, stsClientRest, configuration, httpClient, inntektRetry)
    fun saf(endpointBaseUrl: String) = SafClient(endpointBaseUrl, stsClientRest, configuration, httpClient, safRetry)
    fun oppgaver(endpointBaseUrl: String) = OppgaveClient(endpointBaseUrl, stsClientRest, httpClient, oppgaveRetry)
    fun pdl(endpointBaseURl: String) = PdlClient(endpointBaseURl, stsClientRest, configuration, httpClient, pdlRetry)
    fun ereg(endpointBaseUrl: String) = EregClient(endpointBaseUrl, eregRetry)
}
