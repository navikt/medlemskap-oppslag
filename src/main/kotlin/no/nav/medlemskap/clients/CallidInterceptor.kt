package no.nav.medlemskap.clients

import mu.KotlinLogging
import no.udi.common.headers.v2.RequestHeaderType
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
    @Throws(Fault::class)
    override fun handleMessage(message: Message) {
        when (message) {
            is SoapMessage ->
                try {
                    callIdGenerator().let { uuid ->
                        val ep = message.exchange?.endpoint?.endpointInfo

                        val service = ep?.service?.name?.localPart
                        val operation = message.exchange?.bindingOperationInfo?.name?.localPart

                        val qNameUdi = QName("http://udi.no/Common/Headers/v2", "RequestHeader", "v2")
                        val headerUdi = SoapHeader(qNameUdi, RequestHeaderType(), JAXBDataBinding(RequestHeaderType::class.java))
                        message.headers.add(headerUdi)

                        logger.info { "Kaller service=$service operation=$operation med callId=$uuid" }
                        val qName = QName("uri:no.nav.applikasjonsrammeverk", "callId")
                        val header = SoapHeader(qName, uuid, JAXBDataBinding(String::class.java))
                        message.headers.add(header)
                    }
                } catch (ex: JAXBException) {
                    logger.warn(ex) { "Error while getting call id" }
                }
        }
    }
}
