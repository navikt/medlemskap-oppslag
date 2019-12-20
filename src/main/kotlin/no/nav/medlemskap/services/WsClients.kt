package no.nav.medlemskap.services

import no.nav.medlemskap.common.MetricFeature
import no.nav.medlemskap.services.sts.StsRestClient
import no.nav.medlemskap.services.sts.configureFor
import no.nav.medlemskap.services.tpsws.PersonClient
import no.nav.medlemskap.services.tpsws.PersonFactory
import org.apache.cxf.ext.logging.LoggingFeature
import org.apache.cxf.ws.addressing.WSAddressingFeature
import org.apache.cxf.ws.security.trust.STSClient

class WsClients(private val stsClientWs: STSClient,
                private val stsClientRest: StsRestClient,
                private val callIdGenerator: () -> String) {

    private val features get() = listOf(WSAddressingFeature(), LoggingFeature(), MetricFeature())
    private val outInterceptors get() = listOf(CallIdInterceptor(callIdGenerator))

    companion object {
        init {
            System.setProperty("javax.xml.soap.SAAJMetaFactory", "com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl")
        }
    }

    private fun <ServicePort : Any> ServicePort.withSts() =
            apply {
                stsClientWs.configureFor(this)
            }

    fun person(endpointUrl: String) =
            PersonFactory.create(endpointUrl, features, outInterceptors)
                    .withSts().let { port ->
                        PersonClient(port)
                    }

}
