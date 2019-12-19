package no.nav.medlemskap

import org.slf4j.MDC

const val MDC_CALL_ID = "callId"

class CorrelationId(val id: String) {
    override fun toString(): String = id
}

internal fun getCorrelationId(): CorrelationId =
        CorrelationId(MDC.get(MDC_CALL_ID))
