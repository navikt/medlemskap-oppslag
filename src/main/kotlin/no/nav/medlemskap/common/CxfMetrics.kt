package no.nav.medlemskap.common

import io.micrometer.core.instrument.Timer
import org.apache.cxf.Bus
import org.apache.cxf.feature.AbstractFeature
import org.apache.cxf.interceptor.InterceptorProvider
import org.apache.cxf.message.Message
import org.apache.cxf.phase.AbstractPhaseInterceptor
import org.apache.cxf.phase.Phase

class MetricFeature : AbstractFeature() {
    override fun initializeProvider(provider: InterceptorProvider?, bus: Bus?) {
        provider?.outInterceptors?.add(MetricInterceptor())
        provider?.outInterceptors?.add(TimerStartInterceptor())
        provider?.inInterceptors?.add(TimerEndInterceptor())
    }
}

internal class MetricInterceptor : AbstractPhaseInterceptor<Message>(Phase.SETUP) {
    override fun handleMessage(message: Message?) {
        message?.interceptorChain?.doIntercept(message)

        val ep = message?.exchange?.endpoint?.endpointInfo

        val service = ep?.service?.name?.localPart
        val operation = message?.exchange?.bindingOperationInfo?.name?.localPart

        if (operation != "ping") {
            val status = message?.exchange?.inFaultMessage?.getContent(Exception::class.java)?.let {
                "error"
            } ?: message?.exchange?.get(Exception::class.java)?.let {
                "failure"
            } ?: "success"

            clientCounter(service, operation, status).increment()
        }
    }

    override fun handleFault(message: Message?) {
        stopTimer(message)
    }
}

internal class TimerStartInterceptor : AbstractPhaseInterceptor<Message>(Phase.PREPARE_SEND) {
    override fun handleMessage(message: Message?) {
        val operation = message?.exchange?.bindingOperationInfo?.name?.localPart

        if (operation != "ping") {
            message?.exchange?.put(MetricInterceptor::class.java.name + ".timer", Timer.start())
        }
    }
}

internal class TimerEndInterceptor : AbstractPhaseInterceptor<Message>(Phase.RECEIVE) {
    override fun handleMessage(message: Message?) {
        stopTimer(message)
    }
}

private fun stopTimer(message: Message?) {
    message?.exchange?.get(MetricInterceptor::class.java.name + ".timer")?.let {
        message.exchange[MetricInterceptor::class.java.name + ".timer"] = null
        val ep = message?.exchange?.endpoint?.endpointInfo

        val service = ep?.service?.name?.localPart
        val operation = message?.exchange?.bindingOperationInfo?.name?.localPart
        (it as Timer.Sample).stop(clientTimer(service, operation))
    }
}
