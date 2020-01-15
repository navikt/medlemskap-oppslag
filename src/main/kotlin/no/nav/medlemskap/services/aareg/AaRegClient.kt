package no.nav.medlemskap.services.aareg

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.services.sts.StsRestClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AaRegClient(val baseUrl: String, val stsClient: StsRestClient, val callIdGenerator: () -> String) {

    suspend fun hentArbeidsforhold(fnr: String, fraOgMed: LocalDate? = null, tilOgMed: LocalDate? = null): List<Arbeidsforhold> {
        with(stsClient.oidcToken()) {
            return defaultHttpClient.get<List<Arbeidsforhold>> {
                url("$baseUrl/v1/arbeidstaker/arbeidsforhold")
                header(HttpHeaders.Authorization, "Bearer ${this}")
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callIdGenerator.invoke())
                header("Nav-Personident", fnr)
                header("Nav-Consumer-Token", "Bearer ${this}")
                //header(HttpHeaders.AcceptCharset, Charsets)
                fraOgMed?.let { parameter("ansettelsesperiodeFom", fraOgMed.tilIsoFormat()) }
                tilOgMed?.let { parameter("ansettelsesperiodeTom", tilOgMed.tilIsoFormat()) }
                parameter("historikk", "true")
                parameter("regelverk", "ALLE")
            }
        }
    }

    private fun LocalDate.tilIsoFormat() = this.format(DateTimeFormatter.ISO_LOCAL_DATE)
}
