package no.nav.medlemskap.common

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.HttpClientCall
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.util.AttributeKey
import io.prometheus.client.Histogram

/**
 * [HttpClient] Prometheus metrics feature.
 */
class HttpClientMetrics {

    /**
     * [HttpClientMetrics] feature configuration
     */
    class Config {}

    private suspend fun logRequest(request: HttpRequestBuilder) {
        val service = request.url.encodedPath
        val operation = request.method.value
        val timer = restClientTimer.labels(service, operation).startTimer()
        request.attributes.put(timerKey, timer)
    }

    private suspend fun logResponse(response: HttpResponse) {
        val status = response.status.value.toString()
        val operation = response.call.request.method.value
        val service = response.call.request.url.encodedPath
        restClientCounter.labels(service, operation, status).inc()
        println("logResponse $status, $operation, $service")

        val tidsbruk = response.call.attributes.getOrNull<Histogram.Timer>(timerKey)?.observeDuration()
        println("logResponse tidsbruk $tidsbruk")

    }

    private fun logRequestException(context: HttpRequestBuilder, cause: Throwable) {
        val service = context.url.encodedPath
        val operation = context.method.value
        restClientCounter.labels(service, operation, Companion.REQUEST_EXCEPTION_STATUS).inc()
        println("logRequestException ${Companion.REQUEST_EXCEPTION_STATUS}, $operation, $service")
    }

    private fun logResponseException(context: HttpClientCall, cause: Throwable) {
        val service = context.request.url.encodedPath
        val operation = context.request.method.value
        restClientCounter.labels(service, operation, Companion.RESPONSE_EXCEPTION_STATUS).inc()
        println("logResponseException ${Companion.RESPONSE_EXCEPTION_STATUS}, $operation, $service")
    }

    companion object : HttpClientFeature<Config, HttpClientMetrics> {
        override val key: AttributeKey<HttpClientMetrics> = AttributeKey("HttpClientMetrics")

        override fun prepare(block: Config.() -> Unit): HttpClientMetrics {
            //val config = Config().apply(block)
            return HttpClientMetrics()
        }

        override fun install(feature: HttpClientMetrics, scope: HttpClient) {
            scope.sendPipeline.intercept(HttpSendPipeline.Monitoring) {
                try {
                    feature.logRequest(context)
                } catch (_: Throwable) {

                }

                try {
                    proceedWith(subject)
                } catch (cause: Throwable) {
                    feature.logRequestException(context, cause)
                    throw cause
                }
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.Receive) {
                try {
                    proceedWith(subject)
                    try {
                        feature.logResponse(context.response)
                    } catch (_: Throwable) {

                    }
                } catch (cause: Throwable) {
                    feature.logResponseException(context, cause)
                    throw cause
                }
            }
        }

        private val timerKey: AttributeKey<Histogram.Timer> = AttributeKey("HttpClientMetricsHistogramTimer")

        private const val RESPONSE_EXCEPTION_STATUS: String = "902"
        private const val REQUEST_EXCEPTION_STATUS: String = "901"
    }
}

/**
 * Configure and install [HttpClientMetrics] in [HttpClient].
 */
fun HttpClientConfig<*>.httpClientMetrics(block: HttpClientMetrics.Config.() -> Unit = {}) {
    install(HttpClientMetrics, block)
}