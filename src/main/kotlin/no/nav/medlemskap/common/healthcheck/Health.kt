package no.nav.medlemskap.common.healthcheck

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement

interface Result {
    val name: String
    val result: String
}

data class Healthy(override val name: String, override val result: String) : Result

data class UnHealthy(override val name: String, override val result: String) : Result

interface HealthCheck {
    val name: String
    suspend fun check(): Result
}

class TryCatchHealthCheck(override val name: String,
                          private val block: () -> Unit) : HealthCheck {
    override suspend fun check(): Result {
        return try {
            block.invoke()
            Healthy(name = name, result = "Healthy!")
        } catch (cause: Throwable) {
            UnHealthy(name = name, result = if (cause.message == null) "Unhealthy!" else cause.message!!)
        }
    }
}

class HttpResponseHealthCheck(override val name: String,
                              private val block: suspend () -> HttpResponse) : HealthCheck {
    override suspend fun check(): Result {
        return try {
            val httpResponse = block.invoke()
            if (httpResponse.status.value in 200..299)
                Healthy(name = name, result = "${httpResponse.status.value} (${httpResponse.status.description})")
            else
                UnHealthy(name = name, result = "${httpResponse.status.value} (${httpResponse.status.description})")
        } catch (cause: Throwable) {
            UnHealthy(name = name, result = cause.message ?: "Unhealthy!")
        }
    }
}