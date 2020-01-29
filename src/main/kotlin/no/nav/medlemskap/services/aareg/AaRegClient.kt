package no.nav.medlemskap.services.aareg

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.response.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import mu.KotlinLogging
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.modell.aareg.Arbeidsforhold
import no.nav.medlemskap.services.sts.StsRestClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AaRegClient(val baseUrl: String, val stsClient: StsRestClient, val callIdGenerator: () -> String) {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    suspend fun hentArbeidsforhold(fnr: String, fraOgMed: LocalDate? = null, tilOgMed: LocalDate? = null): List<Arbeidsforhold> {
        val oidcToken = stsClient.oidcToken()
        return defaultHttpClient.get<List<Arbeidsforhold>> {
            url("$baseUrl/v1/arbeidstaker/arbeidsforhold")
            header(HttpHeaders.Authorization, "Bearer ${oidcToken}")
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Call-Id", callIdGenerator.invoke())
            header("Nav-Personident", fnr)
            header("Nav-Consumer-Token", "Bearer ${oidcToken}")
            fraOgMed?.let { parameter("ansettelsesperiodeFom", fraOgMed.tilIsoFormat()) }
            tilOgMed?.let { parameter("ansettelsesperiodeTom", tilOgMed.tilIsoFormat()) }
            parameter("historikk", "true")
            parameter("regelverk", "ALLE")
        }
    }

    suspend fun healthCheck(): Boolean {
        return try {
            val response: HttpResponse = healthCheckQuery()
            logger.info("Healthcheck mot AaReg returnerte status ${response.status.value} (${response.status.description})")
            response.status.isSuccess()
        } catch (t: Throwable) {
            logger.warn("Healthcheck mot AaReg feilet", t)
            false
        }
    }

    suspend fun healthCheckQuery(): HttpResponse {
        val oidcToken = stsClient.oidcToken()
        return defaultHttpClient.get {
            url("$baseUrl/v1")
            header(HttpHeaders.Authorization, "Bearer $oidcToken")
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Call-Id", callIdGenerator.invoke())
            header("Nav-Consumer-Token", "Bearer $oidcToken")
        }
    }

    private fun LocalDate.tilIsoFormat() = this.format(DateTimeFormatter.ISO_LOCAL_DATE)
}
