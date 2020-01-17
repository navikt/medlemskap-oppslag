package no.nav.medlemskap.services.inntekt

import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.modell.inntekt.ArbeidsinntektMaaned
import no.nav.medlemskap.modell.inntekt.HentInntektListeRequest
import no.nav.medlemskap.modell.inntekt.Ident
import no.nav.medlemskap.services.sts.StsRestClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InntektClient(val baseUrl: String, val stsClient: StsRestClient, val callIdGenerator: () -> String) {

    suspend fun hentInntektListe(ident: String, fraOgMed: LocalDate? = null, tilOgMed: LocalDate? = null): List<ArbeidsinntektMaaned> {
            val token = stsClient.oidcToken()
            return defaultHttpClient.post<List<ArbeidsinntektMaaned>> {
                url("$baseUrl/hentinntektliste")
                header(HttpHeaders.Authorization, "Bearer $token")
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header("Nav-Call-Id", callIdGenerator.invoke())
                body = HentInntektListeRequest(
                        Ident(ident, "NATURLIG_IDENT"),
                        fraOgMed?.tilIsoFormat()?.tilAarOgMnd(),
                        tilOgMed?.tilIsoFormat()?.tilAarOgMnd())
            }

    }

    private fun LocalDate.tilIsoFormat() = this.format(DateTimeFormatter.ISO_DATE)



    private fun String.tilAarOgMnd() =  this.format(DateTimeFormatter.ofPattern("yyyy-mm"))
}









