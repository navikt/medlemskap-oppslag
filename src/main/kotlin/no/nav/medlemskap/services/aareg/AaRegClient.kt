package no.nav.medlemskap.services.aareg

import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
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

    suspend fun healthCheck(): HttpResponse {
        val oidcToken = stsClient.oidcToken()
        return defaultHttpClient.options {
            url("$baseUrl/v1")
            header(HttpHeaders.Authorization, "Bearer $oidcToken")
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Call-Id", callIdGenerator.invoke())
            header("Nav-Consumer-Token", "Bearer $oidcToken")
        }
    }

    private fun LocalDate.tilIsoFormat() = this.format(DateTimeFormatter.ISO_LOCAL_DATE)
}
