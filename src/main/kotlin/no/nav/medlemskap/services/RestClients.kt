package no.nav.medlemskap.services

import no.nav.medlemskap.config.*
import no.nav.medlemskap.services.aareg.AaRegClient
import no.nav.medlemskap.services.inntekt.InntektClient
import no.nav.medlemskap.services.medl.MedlClient
import no.nav.medlemskap.services.oppgave.OppgaveClient
import no.nav.medlemskap.services.pdl.PdlClient
import no.nav.medlemskap.services.saf.SafClient
import no.nav.medlemskap.services.sts.StsRestClient

class RestClients(private val stsClientRest: StsRestClient,
                  private val callIdGenerator: () -> String,
                  private val configuration: Configuration) {

    private val aaRegRetry = retryRegistry.retry("AaReg")
    private val inntektRetry = retryRegistry.retry("Inntekt")
    private val medlRetry = retryRegistry.retry("Medl")
    private val oppgaveRetry = retryRegistry.retry("Oppgave")
    private val pdlRetry = retryRegistry.retry("PDL")
    private val safRetry = retryRegistry.retry("Saf")

    fun aaReg(endpointUrl: String) = AaRegClient(endpointUrl, stsClientRest, callIdGenerator, aaRegRetry)
    fun medl2(endpointBaseUrl: String) = MedlClient(endpointBaseUrl, stsClientRest, callIdGenerator, configuration, medlRetry)
    fun inntektskomponenten(endpointBaseUrl: String) = InntektClient(endpointBaseUrl, stsClientRest, callIdGenerator, configuration, inntektRetry)
    fun saf(endpointBaseUrl: String) = SafClient(endpointBaseUrl, stsClientRest, callIdGenerator, configuration, safRetry)
    fun oppgaver(endpointBaseUrl: String) = OppgaveClient(endpointBaseUrl, stsClientRest, callIdGenerator, oppgaveRetry)
    fun pdl(endpointBaseURl: String) = PdlClient(endpointBaseURl, stsClientRest, callIdGenerator, configuration, pdlRetry)

}
