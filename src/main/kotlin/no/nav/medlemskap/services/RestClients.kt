package no.nav.medlemskap.services

import no.nav.medlemskap.services.medl.MedlClient
import no.nav.medlemskap.services.sts.StsRestClient

class RestClients(private val stsClientRest: StsRestClient,
                  private val callIdGenerator: () -> String) {

    fun medl2(endpointBaseUrl: String) = MedlClient(endpointBaseUrl, stsClientRest, callIdGenerator)

}
