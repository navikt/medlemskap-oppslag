package no.nav.medlemskap.common

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.HttpClientCall
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.features.observer.ResponseHandler
import io.ktor.client.features.observer.ResponseObserver
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.HttpResponsePipeline
import io.ktor.util.AttributeKey
import io.prometheus.client.Histogram
import kotlinx.io.core.use


val timerKey: AttributeKey<Histogram.Timer> = AttributeKey("Histogram.Timer")

/**
 * [HttpClient] metrikker feature.
 */
class HttpClientMetrikker {
    private val RESPONSE_EXCEPTION_STATUS: String = "902"
    private val REQUEST_EXCEPTION_STATUS: String = "901"

    /**
     * [Logging] feature configuration
     */
    class Config {
    }

    private suspend fun startTimer(request: HttpRequestBuilder) {
        val service = request.url.encodedPath
        val operation = request.method.value
        val timer = restClientTimer.labels(service, operation).startTimer()
        request.attributes.put(timerKey, timer)
    }

    private suspend fun countCallAndStopTimer(response: HttpResponse): Unit = response.use {

        val status = response.status.value.toString()
        val operation = response.call.request.method.value
        val service = response.call.request.url.encodedPath
        restClientCounter.labels(service, operation, status).inc()

        it.call.attributes.getOrNull<Histogram.Timer>(timerKey)?.observeDuration()
    }

    private fun countExceptionInRequest(context: HttpRequestBuilder, cause: Throwable) {
        val service = context.url.encodedPath
        val operation = context.method.value
        restClientCounter.labels(service, operation, REQUEST_EXCEPTION_STATUS).inc()
    }

    private fun countExceptionInResponse(context: HttpClientCall, cause: Throwable) {
        val service = context.request.url.encodedPath
        val operation = context.request.method.value
        restClientCounter.labels(service, operation, RESPONSE_EXCEPTION_STATUS).inc()
    }

    companion object : HttpClientFeature<Config, HttpClientMetrikker> {
        override val key: AttributeKey<HttpClientMetrikker> = AttributeKey("HttpClientMetrikker")

        override fun prepare(block: Config.() -> Unit): HttpClientMetrikker {
            val config = Config().apply(block)
            return HttpClientMetrikker()
        }

        override fun install(feature: HttpClientMetrikker, scope: HttpClient) {
            scope.sendPipeline.intercept(HttpSendPipeline.Before) {
                try {
                    feature.startTimer(context)
                } catch (_: Throwable) {

                }

                try {
                    proceedWith(subject)
                } catch (cause: Throwable) {
                    feature.countExceptionInRequest(context, cause)
                    throw cause
                }
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.Receive) {
                try {
                    proceedWith(subject)
                } catch (cause: Throwable) {
                    feature.countExceptionInResponse(context, cause)
                    throw cause
                }
            }

            val observer: ResponseHandler = {
                try {
                    feature.countCallAndStopTimer(it)
                } catch (_: Throwable) {
                }
            }

            ResponseObserver.install(ResponseObserver(observer), scope)
        }
    }
}

/**
 * Configure and install [Logging] in [HttpClient].
 */
fun HttpClientConfig<*>.Metrikker() {
    install(HttpClientMetrikker) {}
}