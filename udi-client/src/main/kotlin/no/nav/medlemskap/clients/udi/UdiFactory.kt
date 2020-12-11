package no.nav.medlemskap.clients.udi

import no.udi.common.v2.ObjectFactory
import org.apache.cxf.feature.Feature
import org.apache.cxf.interceptor.Interceptor
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean
import org.apache.cxf.message.Message
import v1.mt_1067_nav.no.udi.MT1067NAVV1Interface
import javax.xml.namespace.QName

object UdiFactory {
    //private val ServiceClass = PersonV3::class.java
    private val ServiceClass = MT1067NAVV1Interface::class.java
    private const val Wsdl = "wsdl/no/nav/tjeneste/virksomhet/person/v3/Binding.wsdl"
    private const val Namespace = "http://nav.no/tjeneste/virksomhet/person/v3/Binding"
    private val ServiceName = QName(Namespace, "personStatusService")
    private val EndpointName = QName(Namespace, "Person_v3Port")

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
