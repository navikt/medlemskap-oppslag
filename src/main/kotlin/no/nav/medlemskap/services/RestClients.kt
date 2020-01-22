package no.nav.medlemskap.services

import no.nav.medlemskap.services.aareg.AaRegClient
import no.nav.medlemskap.services.inntekt.InntektClient
import no.nav.medlemskap.services.medl.MedlClient
import no.nav.medlemskap.services.oppgave.OppgaveClient
import no.nav.medlemskap.services.saf.SafClient
import no.nav.medlemskap.services.sts.StsRestClient

class RestClients(private val stsClientRest: StsRestClient,
                  private val callIdGenerator: () -> String) {

    fun aaReg(endpointUrl: String) = AaRegClient(endpointUrl, stsClientRest, callIdGenerator)
    fun medl2(endpointBaseUrl: String) = MedlClient(endpointBaseUrl, stsClientRest, callIdGenerator)
    fun inntektskomponenten(endpointBaseUrl: String) = InntektClient(endpointBaseUrl, stsClientRest, callIdGenerator)
    fun saf(endpointBaseUrl: String) = SafClient(endpointBaseUrl, stsClientRest, callIdGenerator)
    fun oppgaver(endpointBaseUrl: String) = OppgaveClient(endpointBaseUrl, stsClientRest, callIdGenerator)

}
