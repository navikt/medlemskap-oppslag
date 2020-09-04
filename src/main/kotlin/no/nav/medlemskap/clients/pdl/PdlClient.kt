package no.nav.medlemskap.clients.pdl

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.types.GraphQLResponse
import io.github.resilience4j.retry.Retry
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import mu.KotlinLogging
import no.nav.medlemskap.clients.pdl.generated.HentFoedselsaar
import no.nav.medlemskap.clients.pdl.generated.HentIdenter
import no.nav.medlemskap.clients.pdl.generated.HentNasjonalitet
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.clients.runWithRetryAndMetrics
import no.nav.medlemskap.clients.sts.StsRestClient
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

    suspend fun hentIdenter(fnr: String, callId: String): GraphQLResponse<HentIdenter.Result> {
        return runWithRetryAndMetrics("PDL", "HentIdenter", retry) {
            val stsToken = stsClient.oidcToken()
            val hentIdenterQuery = HentIdenter(GraphQLClient(url = URL("$baseUrl")))
            val variables = HentIdenter.Variables(fnr, null, false)

            val response: GraphQLResponse<HentIdenter.Result> = hentIdenterQuery.execute(variables) {
                header(HttpHeaders.Authorization, "Bearer $stsToken")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("Nav-Consumer-Token", "Bearer $stsToken")
                header("Nav-Consumer-Id", username)
            }
            response
        }
    }


    suspend fun hentPerson(fnr: String, callId: String): GraphQLResponse<HentPerson.Result> {
        return runWithRetryAndMetrics("PDL", "HentPerson", retry) {
            val stsToken = stsClient.oidcToken()
            val hentPersonQuery = HentPerson(GraphQLClient(url = URL("$baseUrl")))
            val variables = HentPerson.Variables(fnr, false)

            val response: GraphQLResponse<HentPerson.Result> = hentPersonQuery.execute(variables) {
                header(HttpHeaders.Authorization, "Bearer $stsToken")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("Nav-Consumer-Token", "Bearer $stsToken")
                header("Nav-Consumer-Id", username)
            }
            response
        }

    }

    suspend fun hentNasjonalitet(fnr: String, callId: String): GraphQLResponse<HentNasjonalitet.Result> {
        return runWithRetryAndMetrics("PDL", "HentNasjonalitet", retry) {
            val stsToken = stsClient.oidcToken()
            val hentNasjonalitetQuery = HentNasjonalitet(GraphQLClient(url = URL("$baseUrl")))
            val variables = HentNasjonalitet.Variables(fnr, false)

            val response: GraphQLResponse<HentNasjonalitet.Result> = hentNasjonalitetQuery.execute(variables) {
                header(HttpHeaders.Authorization, "Bearer $stsToken")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("Nav-Consumer-Token", "Bearer $stsToken")
                header("Nav-Consumer-Id", username)
            }
            response
        }
    }

    suspend fun hentFoedselsaar(fnr: String, callId: String): GraphQLResponse<HentFoedselsaar.Result> {
        return runWithRetryAndMetrics("PDL", "HentFoedselsaar", retry) {
            val stsToken = stsClient.oidcToken()
            val hentFoedselsaarQuery = HentFoedselsaar(GraphQLClient(url = URL("$baseUrl")))
            val variables = HentFoedselsaar.Variables(fnr)

            val response: GraphQLResponse<HentFoedselsaar.Result> = hentFoedselsaarQuery.execute(variables) {
                header(HttpHeaders.Authorization, "Bearer $stsToken")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("Nav-Consumer-Token", "Bearer $stsToken")
                header("Nav-Consumer-Id", username)
            }
            response
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

