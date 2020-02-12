package no.nav.medlemskap.services

import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import io.prometheus.client.Histogram
import mu.KotlinLogging
import no.nav.medlemskap.common.clientCounter
import no.nav.medlemskap.common.clientTimer

private val logger = KotlinLogging.logger { }

suspend fun <T> runWithRetryAndMetrics(service: String, operation: String, retry: Retry?, block: suspend () -> T): T {
    retry?.let {
        return it.executeSuspendFunction {
            runWithMetrics(service, operation, block)
        }
    }
    return runWithMetrics(service, operation, block)
}

suspend fun <T> runWithMetrics(service: String, operation: String, block: suspend () -> T): T {
    var timer: Histogram.Timer? = null
    try {
        timer = clientTimer.labels(service, operation).startTimer()
    } catch (t: Throwable) {
        logger.warn("Feilet under opprettelsen av timer for $service:$operation", t)
    }
    try {
        val result: T = block.invoke()
        try {
            clientCounter.labels(service, operation, "success").inc()
        } catch (t: Throwable) {
            logger.warn("Feilet under inkrementinger av counter for $service:$operation", t)
        }
        return result
    } catch (t: Throwable) {
        try {
            clientCounter.labels(service, operation, "failure").inc()
        } catch (t: Throwable) {
            logger.warn("Feilet under inkrementinger av counter for $service:$operation", t)
        }
        throw t
    } finally {
        try {
            timer?.let { it.observeDuration() }
        } catch (t: Throwable) {
            logger.warn("Feilet under loggingen av timer for $service:$operation", t)
        }
    }
}