package no.nav.medlemskap.common.healthcheck

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.system.measureTimeMillis

fun Routing.healthRoute(path: String, healthService: HealthService) {
    get(path) {
        var healthy: List<Result> = listOf()
        var unhealthy: List<Result> = listOf()

        val duration = Duration.of(
            measureTimeMillis {
                val results = healthService.check()
                healthy = results.filterIsInstance<Healthy>()
                unhealthy = results.filterIsInstance<UnHealthy>()
            },
            ChronoUnit.MILLIS
        )

        call.respond(
            status = if (unhealthy.isEmpty()) HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable,
            message = mapOf(
                "duration" to duration.toString(), // ISO-8601
                "healthy" to healthy,
                "unhealthy" to unhealthy
            )
        )
    }
}
