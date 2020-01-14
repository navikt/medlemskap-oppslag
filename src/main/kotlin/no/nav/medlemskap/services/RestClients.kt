package no.nav.medlemskap.services

import no.nav.medlemskap.services.aareg.AaRegClient
import no.nav.medlemskap.services.sts.StsRestClient

class RestClients(private val stsClientRest: StsRestClient,
                  private val callIdGenerator: () -> String) {

    fun aaReg(endpointUrl: String) =
            AaRegClient(endpointUrl, stsClientRest, callIdGenerator)

}
