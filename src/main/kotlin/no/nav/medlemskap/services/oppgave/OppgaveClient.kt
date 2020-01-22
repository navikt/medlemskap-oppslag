package no.nav.medlemskap.services.oppgave

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.modell.oppgave.FinnOppgaverResponse
import no.nav.medlemskap.services.sts.StsRestClient

private const val TEMA_MEDLEMSKAP = "MED"
private const val TEMA_UNNTAK_FRA_MEDLEMSKAP = "UFM"
private const val TEMA_TRYGDEAVGIFT = "TRY"

class OppgaveClient (
        private val baseUrl: String,
        private val stsClient: StsRestClient,
        private val callIdGenerator: () -> String
) {

    suspend fun hentOppgaver(ident: String): FinnOppgaverResponse {
        val token = stsClient.oidcToken()
        return defaultHttpClient.get{
            url("$baseUrl/api/v1/oppgaver")
            header(HttpHeaders.Authorization, "Bearer $token")
            header("X-Correlation-Id", callIdGenerator.invoke())
            parameter("aktoerId", ident)
            parameter("tema", TEMA_MEDLEMSKAP)
            parameter("tema", TEMA_UNNTAK_FRA_MEDLEMSKAP)
            parameter("tema", TEMA_TRYGDEAVGIFT)
        }
    }

}
