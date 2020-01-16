package no.nav.medlemskap.services.inntekt

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.modell.inntekt.ArbeidsinntektMaaned
import no.nav.medlemskap.services.sts.StsRestClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InntektClient(val baseUrl: String, val stsClient: StsRestClient, val callIdGenerator: () -> String) {

    suspend fun HentInntektListe(ident: String, fraOgMed: LocalDate? = null, tilOgMed: LocalDate? = null): List<ArbeidsinntektMaaned> {
        with(stsClient.oidcToken()) {
            return defaultHttpClient.get<List<ArbeidsinntektMaaned>> {
                url("$baseUrl/hentinntektliste")
                header(HttpHeaders.Authorization, "Bearer ${this}")
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callIdGenerator.invoke())
                header("Nav-Personident", ident)
                fraOgMed?.let { parameter("maanedFom", fraOgMed.tilIsoFormat()) }
                tilOgMed?.let { parameter("maanedTom", tilOgMed.tilIsoFormat()) }
            }
        }
    }

    private fun LocalDate.tilIsoFormat() = this.format(DateTimeFormatter.ISO_DATE)

}