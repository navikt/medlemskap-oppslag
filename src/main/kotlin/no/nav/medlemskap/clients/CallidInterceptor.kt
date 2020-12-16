package no.nav.medlemskap.clients

import mu.KotlinLogging
import org.apache.cxf.binding.soap.SoapHeader
import org.apache.cxf.binding.soap.SoapMessage
import org.apache.cxf.interceptor.Fault
import org.apache.cxf.jaxb.JAXBDataBinding
import org.apache.cxf.message.Message
import org.apache.cxf.phase.AbstractPhaseInterceptor
import org.apache.cxf.phase.Phase
import javax.xml.bind.JAXBException
import javax.xml.namespace.QName

private val logger = KotlinLogging.logger { }

class CallIdInterceptor(private val callIdGenerator: () -> String) : AbstractPhaseInterceptor<Message>(Phase.PRE_STREAM) {

    private val secureLogger = KotlinLogging.logger("tjenestekall")

    @Throws(Fault::class)
    override fun handleMessage(message: Message) {
        when (message) {
            is SoapMessage ->
                try {
                    callIdGenerator().let { uuid ->
                        val ep = message.exchange?.endpoint?.endpointInfo

                        val service = ep?.service?.name?.localPart
                        val operation = message.exchange?.bindingOperationInfo?.name?.localPart

                        logger.info { "Kaller service=$service operation=$operation med callId=$uuid" }
                        val qName = QName("uri:no.nav.applikasjonsrammeverk", "callId")
                        val header = SoapHeader(qName, uuid, JAXBDataBinding(String::class.java))
                        secureLogger.info("HeaderName: ${header.name} declaredNamespaceMappings ${header.dataBinding.declaredNamespaceMappings}")
                        message.headers.add(header)
                    }
                } catch (ex: JAXBException) {
                    logger.warn(ex) { "Error while getting call id" }
                }
        }
    }
}
