package no.nav.medlemskap.clients.pdl

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import io.github.resilience4j.retry.Retry
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import mu.KotlinLogging
import no.nav.medlemskap.clients.pdl.generated.HentIdenter
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.clients.runWithRetryAndMetrics
import no.nav.medlemskap.clients.sts.StsRestClient
import java.net.URL

class PdlClient(
    private val baseUrl: String,
    private val stsClient: StsRestClient,
    private val username: String,
    private val httpClient: HttpClient,
    private val retry: Retry? = null,
    private val pdlApiKey: String
) {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    suspend fun hentIdenterv2(fnr: String, callId: String): GraphQLClientResponse<HentIdenter.Result> {
        val client = GraphQLKtorClient(
            url = URL(baseUrl),
            httpClient = httpClient
        )

        return runWithRetryAndMetrics("PDL", "HentIdenter", retry) {
            logger.info("Pdl: Henter aktørId for person med fnr $fnr på url $baseUrl")
            val stsToken = stsClient.oidcToken()
            val query = HentIdenter(
                variables = HentIdenter.Variables(
                    fnr,
                    null,
                    true
                )
            )
            val response: GraphQLClientResponse<HentIdenter.Result> = client.execute(query) {
                header(HttpHeaders.Authorization, "Bearer $stsToken")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("Nav-Consumer-Token", "Bearer $stsToken")
                header("Nav-Consumer-Id", username)
                header("x-nav-apiKey", pdlApiKey)
            }

            logger.info("Pdl: Ferdig med kallet")
            logger.info(response.toString())
            logger.info("Pdl: Data: " + response.data)
            logger.info("Pdl: Errors: " + response.errors)
            if (!response.errors.isNullOrEmpty()) {
                logger.error("PDL response errors: ${response.errors}")
                // TODO: utfør feil håndtering. Gjøres utenfor denne koden?
            }
            response
        }
    }

    suspend fun hentPersonV2(fnr: String, callId: String): GraphQLClientResponse<HentPerson.Result> {
        val client = GraphQLKtorClient(
            url = URL(baseUrl),
            httpClient = httpClient
        )
        return runWithRetryAndMetrics("PDL", "HentPerson", retry) {
            val stsToken = stsClient.oidcToken()

            val query = HentPerson(
                variables = HentPerson.Variables(fnr, true, true)
            )
            val response: GraphQLClientResponse<HentPerson.Result> = client.execute(query) {
                header(HttpHeaders.Authorization, "Bearer $stsToken")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("Nav-Consumer-Token", "Bearer $stsToken")
                header("Nav-Consumer-Id", username)
                header("x-nav-apiKey", pdlApiKey)
            }

            logger.info("Pdl: Ferdig med kallet")
            logger.info(response.toString())
            logger.info("Pdl: Data: " + response.data)
            logger.info("Pdl: Errors: " + response.errors)
            if (!response.errors.isNullOrEmpty()) {
                logger.error("PDL response errors: ${response.errors}")
                // TODO: utfør feil håndtering. Gjøres utenfor denne koden?
            }
            response
        }
    }

    suspend fun healthCheck(): HttpResponse {
        return httpClient.options {
            url(baseUrl)
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("x-nav-apiKey", pdlApiKey)
            header("Nav-Consumer-Id", username)
        }
    }
}
