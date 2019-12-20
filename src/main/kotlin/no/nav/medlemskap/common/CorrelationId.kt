package no.nav.medlemskap.common

import org.slf4j.MDC

const val MDC_CALL_ID = "callId"

class CorrelationId(private val id: String) {
    override fun toString(): String = id
}

internal fun getCorrelationId(): CorrelationId =
        CorrelationId(MDC.get(MDC_CALL_ID))
