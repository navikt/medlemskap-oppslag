package no.nav.medlemskap.clients.saf

import com.expediagroup.graphql.client.serialization.types.KotlinxGraphQLResponse
import io.github.resilience4j.retry.Retry
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import mu.KotlinLogging
import no.nav.medlemskap.clients.azuread.AzureAdClient
import no.nav.medlemskap.clients.runWithRetryAndMetrics
import no.nav.medlemskap.clients.saf.generated.Dokumenter
import no.nav.medlemskap.clients.saf.generated.enums.BrukerIdType
import no.nav.medlemskap.clients.saf.generated.enums.Journalstatus
import no.nav.medlemskap.clients.saf.generated.enums.Tema
import no.nav.medlemskap.clients.saf.generated.inputs.BrukerIdInput
import no.nav.medlemskap.common.exceptions.GraphqlError
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.common.ytelse
import no.nav.medlemskap.config.Configuration
import kotlin.coroutines.coroutineContext

class SafClient(
    private val baseUrl: String,
    private val azureAdClient: AzureAdClient,
    private val configuration: Configuration,
    private val httpClient: HttpClient,
    private val retry: Retry? = null
) {
    companion object {
        private val logger = KotlinLogging.logger { }
        private const val ANTALL_JOURNALPOSTER = 10
    }

    suspend fun hentJournaldatav2(fnr: String, callId: String): Dokumenter.Result {

        return runWithRetryAndMetrics("SAF", "DokumentoversiktBruker", retry) {

            val token = azureAdClient.hentToken(configuration.register.safScope)
            val dokumenterQuery = Dokumenter(
                variables = Dokumenter.Variables(
                    brukerId = BrukerIdInput(id = fnr, type = BrukerIdType.FNR),
                    foerste = ANTALL_JOURNALPOSTER,
                    tema = listOf(Tema.MED, Tema.UFM, Tema.TRY),
                    journalstatuser = listOf(Journalstatus.MOTTATT, Journalstatus.JOURNALFOERT, Journalstatus.FERDIGSTILT, Journalstatus.EKSPEDERT, Journalstatus.UNDER_ARBEID, Journalstatus.RESERVERT, Journalstatus.OPPLASTING_DOKUMENT, Journalstatus.UKJENT)
                )

            )

            val response: KotlinxGraphQLResponse<Dokumenter.Result> = httpClient.post() {
                url(baseUrl)
                setBody(dokumenterQuery)
                header(HttpHeaders.Authorization, "Bearer ${token.token}")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Callid", callId)
                header("Nav-Consumer-Id", configuration.azureAd.clientId)
            }.body()

            response.errors?.let { errors ->
                logger.warn { "Fikk følgende feil fra Saf: ${objectMapper.writeValueAsString(errors)}" }
                throw GraphqlError(errors.first(), "Saf", coroutineContext.ytelse())
            }
            response.data!!
        }
    }

    suspend fun healthCheck(): HttpResponse {
        val token = azureAdClient.hentToken(configuration.register.safScope)
        return httpClient.options {
            url(baseUrl)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer ${token.token}")
            header("Nav-Consumer-Id", configuration.azureAd.clientId)
        }
    }
}
