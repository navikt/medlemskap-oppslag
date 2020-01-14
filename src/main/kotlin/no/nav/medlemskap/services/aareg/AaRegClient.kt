package no.nav.medlemskap.services.aareg

import com.google.gson.annotations.SerializedName
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.services.sts.StsRestClient


class AaRegClient(val baseUrl: String, val stsClient: StsRestClient, val callIdGenerator: () -> String) {

    suspend fun kall(fnr: String): List<AaRegClient.Arbeidsforhold> {
        with(stsClient.oidcToken()) {
            return defaultHttpClient.get<List<AaRegClient.Arbeidsforhold>> {
                url("$baseUrl/api/v1/arbeidstaker/arbeidsforhold")
                header(HttpHeaders.Authorization, "Bearer ${this}")
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callIdGenerator.invoke())
                header("Nav-Personident", fnr)
                header("Nav-Consumer-Token", "Bearer ${this}")
                //header(HttpHeaders.AcceptCharset, Charsets)
                //parameter("ansettelsesperiodeFom", "client_credentials")
                //parameter("ansettelsesperiodeFom", "openid")
                parameter("historikk", "true")
                parameter("regelverk", "ALLE")
            }
        }
    }

    data class Arbeidsforhold(
            @SerializedName("navArbeidsforholdId")
            val navArbeidsforholdId: Int
    ) {
    }
}
