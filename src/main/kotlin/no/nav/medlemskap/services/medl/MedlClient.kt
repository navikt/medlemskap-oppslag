package no.nav.medlemskap.services.medl

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.modell.medl.Medlemskapsunntak
import no.nav.medlemskap.services.sts.StsRestClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedlClient(val baseUrl: String, val stsClient: StsRestClient, val callIdGenerator: () -> String) {

    suspend fun hentMedlemskapsunntak(ident: String, fraOgMed: LocalDate? = null, tilOgMed: LocalDate? = null): List<Medlemskapsunntak> {
        with(stsClient.oidcToken()) {
            return defaultHttpClient.get<List<Medlemskapsunntak>> {
                url("$baseUrl/api/v1/medlemskapsunntak")
                header(HttpHeaders.Authorization, "Bearer ${this}")
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callIdGenerator.invoke())
                header("Nav-Personident", ident)
                fraOgMed?.let { parameter("fraOgMed", fraOgMed.tilIsoFormat()) }
                tilOgMed?.let { parameter("tilOgMed", tilOgMed.tilIsoFormat()) }
            }
        }
    }

    private fun LocalDate.tilIsoFormat() = this.format(DateTimeFormatter.ISO_DATE)

}
