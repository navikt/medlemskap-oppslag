package no.nav.medlemskap.clients.wsClients

import io.github.resilience4j.retry.Retry
import mu.KotlinLogging
import no.nav.medlemskap.clients.CallIdInterceptor
import no.nav.medlemskap.clients.sts.configureFor
import no.nav.medlemskap.clients.udi.UdiClient
import no.nav.medlemskap.clients.udi.UdiFactory
import no.nav.medlemskap.common.MetricFeature
import org.apache.cxf.ext.logging.LoggingFeature
import org.apache.cxf.feature.AbstractFeature
import org.apache.cxf.ws.addressing.WSAddressingFeature
import org.apache.cxf.ws.security.trust.STSClient

class WsClients(
    private val stsClientWs: STSClient,
    private val callIdGenerator: () -> String
) {
    private val wsAddressingFeature = createWsAdressingFeature()
    private val features = listOf(LoggingFeature(), MetricFeature())
    private val outInterceptors get() = listOf(CallIdInterceptor(callIdGenerator))
    private val logger = KotlinLogging.logger { }

    companion object {
        init {
            System.setProperty("javax.xml.soap.SAAJMetaFactory", "com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl")
        }
    }

    private fun <ServicePort : Any> ServicePort.withSts() =
        apply {
            stsClientWs.configureFor(this)
        }

    fun oppholdstillatelse(endpointUrl: String, retry: Retry?): UdiClient {

        logger.info { "wsAddressingFeature isEnabled " + wsAddressingFeature.isEnabled }
        logger.info { "wsAddressingFeature isAddressingRequired " + wsAddressingFeature.isAddressingRequired }
        logger.info { "wsAddressingFeature allowDuplicates " + wsAddressingFeature.isAllowDuplicates }
        logger.info { "wsAddressingFeature responses " + wsAddressingFeature.responses }

        return UdiFactory.create(endpointUrl, features, outInterceptors)
            .withSts().let { port ->
                UdiClient(port, retry)
            }
    }

    private fun createWsAdressingFeature(): WSAddressingFeature {
        val wsAddressingFeature = WSAddressingFeature()
        wsAddressingFeature.isAddressingRequired = true
        return wsAddressingFeature
    }
}

