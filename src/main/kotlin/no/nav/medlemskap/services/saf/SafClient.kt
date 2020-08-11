package no.nav.medlemskap.services.saf

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.types.GraphQLResponse
import io.github.resilience4j.retry.Retry
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.options
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import mu.KotlinLogging
import no.nav.medlemskap.client.generated.Dokumenter
import no.nav.medlemskap.common.exceptions.GraphqlError
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.services.runWithRetryAndMetrics
import no.nav.medlemskap.services.sts.StsRestClient
import java.net.URL

class SafClient(
        private val baseUrl: String,
        private val stsClient: StsRestClient,
        private val configuration: Configuration,
        private val httpClient: HttpClient,
        private val retry: Retry? = null
) {
    companion object {
        private val logger = KotlinLogging.logger { }
        private const val ANTALL_JOURNALPOSTER = 10
    }

    suspend fun hentJournaldata(fnr: String, callId: String): Dokumenter.Result {
        return runWithRetryAndMetrics("SAF", "DokumentoversiktBruker", retry) {

            val dokumenterQuery = Dokumenter(GraphQLClient(url = URL(baseUrl)))

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
                header("Nav-Consumer-Id", configuration.sts.username)
            }

            response.errors?.let { errors ->
                logger.warn { "Fikk følgende feil fra Saf: ${objectMapper.writeValueAsString(errors)}" }
                throw GraphqlError(errors.first(), "Saf")
            }
            response.data!!
        }
    }

    suspend fun healthCheck(): HttpResponse {
        return httpClient.options {
            url(baseUrl)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Consumer-Id", configuration.sts.username)
        }
    }
}

class SafService(private val safClient: SafClient) {
    suspend fun hentJournaldata(fnr: String, callId: String) =
            mapDokumentoversiktBrukerResponse(safClient.hentJournaldata(fnr, callId))
}


