package no.nav.medlemskap.services.medl

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.modell.medl.Medlemskapsunntak
import no.nav.medlemskap.services.sts.StsRestClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedlClient(
        private val baseUrl: String,
        private val stsClient: StsRestClient,
        private val callIdGenerator: () -> String,
        private val config: Configuration) {

    suspend fun hentMedlemskapsunntak(ident: String, fraOgMed: LocalDate? = null, tilOgMed: LocalDate? = null): List<Medlemskapsunntak> {
        val token = stsClient.oidcToken()
        return defaultHttpClient.get {
            url("$baseUrl/api/v1/medlemskapsunntak")
            header(HttpHeaders.Authorization, "Bearer $token")
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Call-Id", callIdGenerator.invoke())
            header("Nav-Personident", ident)
            header("Nav-Consumer-Id", config.sts.username)
            fraOgMed?.let { parameter("fraOgMed", fraOgMed.tilIsoFormat()) }
            tilOgMed?.let { parameter("tilOgMed", tilOgMed.tilIsoFormat()) }
        }

    }

    private fun LocalDate.tilIsoFormat() = this.format(DateTimeFormatter.ISO_DATE)

}
