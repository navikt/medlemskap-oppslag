package no.nav.medlemskap.clients.udi

import org.apache.cxf.feature.Feature
import org.apache.cxf.interceptor.Interceptor
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean
import org.apache.cxf.message.Message
import v1.mt_1067_nav.no.udi.MT1067NAVV1Interface
import javax.xml.namespace.QName

object UdiFactory {
    private val ServiceClass = MT1067NAVV1Interface::class.java
    private const val Wsdl = "udi-client/src/main/wsdl/MT_1067_NAV_Data_v1.xsd"
    private const val Namespace = "http://udi.no/MT_1067_NAV_Data/v1"
    private val ServiceName = QName(Namespace, "MT_1067_NAV_v1Service")
    private val EndpointName = QName(Namespace, "MT_1067_NAV_v1Port")

    fun create(endpointUrl: String, features: List<Feature> = emptyList(), outInterceptors: List<Interceptor<Message>> = emptyList()) =
            JaxWsProxyFactoryBean().apply {
                address = endpointUrl
                wsdlURL = Wsdl
                serviceName = ServiceName
                endpointName = EndpointName
                serviceClass = ServiceClass
                this.features.addAll(features)
                this.outInterceptors.addAll(outInterceptors)
            }.create(ServiceClass)

}