package no.nav.medlemskap.clients.aareg

import io.github.resilience4j.retry.Retry
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import mu.KotlinLogging
import no.nav.medlemskap.clients.runWithRetryAndMetrics
import no.nav.medlemskap.clients.sts.StsRestClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AaRegClient(
    private val baseUrl: String,
    private val username: String,
    private val stsClient: StsRestClient,
    private val httpClient: HttpClient,
    private val aaRegApiKey: String,
    private val retry: Retry? = null
) {

    companion object {
        private val logger = KotlinLogging.logger { }
        private const val IKKE_EKSISTERENDE_FNR = "01010100000"
    }

    suspend fun hentArbeidsforhold(fnr: String, callId: String, fraOgMed: LocalDate? = null, tilOgMed: LocalDate? = null): List<AaRegArbeidsforhold> {
        val oidcToken = stsClient.oidcToken()
        return runCatching {
            runWithRetryAndMetrics("AaReg", "ArbeidsforholdV1", retry) {
                httpClient.get() {
                    url("$baseUrl/v1/arbeidstaker/arbeidsforhold")
                    header(HttpHeaders.Authorization, "Bearer $oidcToken")
                    header(HttpHeaders.Accept, ContentType.Application.Json)
                    header("Nav-Call-Id", callId)
                    header("Nav-Personident", fnr)
                    header("Nav-Consumer-Token", "Bearer $oidcToken")
                    header("x-nav-apiKey", aaRegApiKey)
                    fraOgMed?.let { parameter("ansettelsesperiodeFom", fraOgMed.tilIsoFormat()) }
                    tilOgMed?.let { parameter("ansettelsesperiodeTom", tilOgMed.tilIsoFormat()) }
                    parameter("historikk", "true")
                    parameter("regelverk", "ALLE")
                }.body<List<AaRegArbeidsforhold>>()
            }
        }.fold(
            onSuccess = { it },
            onFailure = { error ->
                when (error) {
                    is ClientRequestException -> {
                        if (error.response.status.value == 404) {
                            logger.warn("404, fra AAREG på url (GET) $baseUrl/v1/arbeidstaker/arbeidsforhold")
                            listOf()
                        } else {
                            throw error
                        }
                    }
                    else -> throw error
                }
            }
        )
    }

    suspend fun hentArbeidsforholdV2(fnr: String, callId: String, fraOgMed: LocalDate? = null, tilOgMed: LocalDate? = null): List<AaRegArbeidsforhold> {
        val oidcToken = stsClient.oidcToken()
        return runCatching {
            runWithRetryAndMetrics("AaReg", "ArbeidsforholdV1", retry) {
                httpClient.get() {
                    url("$baseUrl/v2/arbeidstaker/arbeidsforhold")
                    header(HttpHeaders.Authorization, "Bearer $oidcToken")
                    header(HttpHeaders.Accept, ContentType.Application.Json)
                    header("Nav-Call-Id", callId)
                    header("Nav-Personident", fnr)
                    header("Nav-Consumer-Token", "Bearer $oidcToken")
                    header("x-nav-apiKey", aaRegApiKey)
                    parameter("historikk", "true")
                    parameter("arbeidsforholdstatus", listOf("AKTIV", "AVSLUTTET", "FREMTIDIG"))
                    parameter("rapporteringsordning", listOf("FOER_A_ORDNINGEN", "A_ORDNINGEN"))
                }.body<List<AaRegArbeidsforhold>>()
            }
        }.fold(
            onSuccess = { it },
            onFailure = { error ->
                when (error) {
                    is ClientRequestException -> {
                        if (error.response.status.value == 404) {
                            logger.warn("404, fra AAREG på url (GET) $baseUrl/v1/arbeidstaker/arbeidsforhold")
                            listOf()
                        } else {
                            throw error
                        }
                    }
                    else -> throw error
                }
            }
        )
    }

    // Deaktivert pga. closed channel exceptions
    /*
    suspend fun healthCheck(): HttpResponse {
        return httpClient.get {
            url("$baseUrl/ping")
            header(HttpHeaders.Accept, ContentType.Text.Plain)
            header("Nav-Consumer-Id", username)
            header("x-nav-apiKey", aaRegApiKey)
        }
    }
*/
    private fun LocalDate.tilIsoFormat() = this.format(DateTimeFormatter.ISO_LOCAL_DATE)
}
