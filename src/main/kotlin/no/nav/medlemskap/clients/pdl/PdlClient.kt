package no.nav.medlemskap.clients.pdl

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.types.GraphQLResponse
import io.github.resilience4j.retry.Retry
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.options
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import mu.KotlinLogging
import no.nav.medlemskap.client.generated.pdl.HentIdenter
import no.nav.medlemskap.clients.runWithRetryAndMetrics
import no.nav.medlemskap.clients.sts.StsRestClient
import no.nav.medlemskap.common.exceptions.GraphqlError
import no.nav.medlemskap.common.objectMapper
import java.net.URL


class PdlClient(
        private val baseUrl: String,
        private val stsClient: StsRestClient,
        private val username: String,
        private val httpClient: HttpClient,
        private val retry: Retry? = null

) {
    companion object {
        private val logger = KotlinLogging.logger { }
    }
    suspend fun hentIdenter(fnr: String, callId: String): HentIdenter.Result {

        return runWithRetryAndMetrics("PDL", "HentIdenter", retry) {
            val stsToken = stsClient.oidcToken()
            val hentIdenterQuery = HentIdenter(GraphQLClient(url = URL("$baseUrl")))
            val variables = HentIdenter.Variables(fnr, null, false)

            val response: GraphQLResponse<HentIdenter.Result> = hentIdenterQuery.execute(variables){
                header(HttpHeaders.Authorization, "Bearer $stsToken")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("Nav-Consumer-Token", "Bearer $stsToken")
                header("Nav-Consumer-Id", username)
            }
            response.errors?.let { errors ->
                PdlClient.logger.warn { "Fikk følgende feil fra PDL hentIdenter: ${objectMapper.writeValueAsString(errors)}" }
                throw GraphqlError(errors.first(), "PDL")
            }
            response.data!!
        }
    }


    suspend fun hentPerson(fnr: String, callId: String): HentPdlPersonResponse {
        return runWithRetryAndMetrics("PDL", "HentPerson", retry) {
            httpClient.post<HentPdlPersonResponse> {
                url("$baseUrl")
                header(HttpHeaders.Authorization, "Bearer ${stsClient.oidcToken()}")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("TEMA", "MED")
                header("Nav-Consumer-Token", "Bearer ${stsClient.oidcToken()}")
                header("Nav-Consumer-Id", username)
                body = hentPersonQuery(fnr)
            }
        }

    }

    suspend fun hentNasjonalitet(fnr: String, callId: String): HentStatsborgerskapResponse {
        return runWithRetryAndMetrics("PDL", "HentNasjonalitet", retry) {
            httpClient.post<HentStatsborgerskapResponse> {
                url("$baseUrl")
                header(HttpHeaders.Authorization, "Bearer ${stsClient.oidcToken()}")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("Nav-Consumer-Token", "Bearer ${stsClient.oidcToken()}")
                header("Nav-Consumer-Id", username)
                body = hentNasjonalitetQuery(fnr)
            }
        }

    }

    suspend fun hentFoedselsaar(fnr: String, callId: String): HentFoedselsaarResponse {
        return runWithRetryAndMetrics("PDL", "HentFoedselsaar", retry) {
            val oidcToken = stsClient.oidcToken()
            httpClient.post<HentFoedselsaarResponse> {
                url("$baseUrl")
                header(HttpHeaders.Authorization, "Bearer ${oidcToken}")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("TEMA", "MED")
                header("Nav-Consumer-Token", "Bearer ${oidcToken}")
                header("Nav-Consumer-Id", username)
                body = hentFoedselsaarQuery(fnr)
            }
        }
    }

    suspend fun healthCheck(): HttpResponse {
        return httpClient.options {
            url("$baseUrl")
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Consumer-Id", username)
        }
    }
}

