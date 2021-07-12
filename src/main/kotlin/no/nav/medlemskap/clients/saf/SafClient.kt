package no.nav.medlemskap.clients.saf

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.types.GraphQLResponse
import io.github.resilience4j.retry.Retry
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import mu.KotlinLogging
import no.nav.medlemskap.clients.runWithRetryAndMetrics
import no.nav.medlemskap.clients.saf.generated.Dokumenter
import no.nav.medlemskap.clients.sts.StsRestClient
import no.nav.medlemskap.common.exceptions.GraphqlError
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.common.ytelse
import java.net.URL
import kotlin.coroutines.coroutineContext

class SafClient(
    private val baseUrl: String,
    private val stsClient: StsRestClient,
    private val username: String,
    private val httpClient: HttpClient,
    private val safApiKey: String,
    private val retry: Retry? = null
) {
    companion object {
        private val logger = KotlinLogging.logger { }
        private const val ANTALL_JOURNALPOSTER = 10
    }

    suspend fun hentJournaldata(fnr: String, callId: String): Dokumenter.Result {
        return runWithRetryAndMetrics("SAF", "DokumentoversiktBruker", retry) {

            val dokumenterQuery = Dokumenter(GraphQLKtorClient(url = URL(baseUrl)))

            val stsToken = stsClient.oidcToken()
            val variables = Dokumenter.Variables(
                brukerId = Dokumenter.BrukerIdInput(id = fnr, type = Dokumenter.BrukerIdType.FNR),
                foerste = ANTALL_JOURNALPOSTER,
                tema = listOf(Dokumenter.Tema.MED, Dokumenter.Tema.UFM, Dokumenter.Tema.TRY),
                journalstatuser = listOf(Dokumenter.Journalstatus.MOTTATT, Dokumenter.Journalstatus.JOURNALFOERT, Dokumenter.Journalstatus.FERDIGSTILT, Dokumenter.Journalstatus.EKSPEDERT, Dokumenter.Journalstatus.UNDER_ARBEID, Dokumenter.Journalstatus.RESERVERT, Dokumenter.Journalstatus.OPPLASTING_DOKUMENT, Dokumenter.Journalstatus.UKJENT)
            )

            val response: GraphQLResponse<Dokumenter.Result> = dokumenterQuery.execute(variables) {
                header(HttpHeaders.Authorization, "Bearer $stsToken")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Callid", callId)
                header("Nav-Consumer-Id", username)
                header("x-nav-apiKey", safApiKey)
            }

            response.errors?.let { errors ->
                logger.warn { "Fikk følgende feil fra Saf: ${objectMapper.writeValueAsString(errors)}" }
                throw GraphqlError(errors.first(), "Saf", coroutineContext.ytelse())
            }
            response.data!!
        }
    }

    suspend fun healthCheck(): HttpResponse {
        return httpClient.options {
            url(baseUrl)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Consumer-Id", username)
            header("x-nav-apiKey", safApiKey)
        }
    }
}
