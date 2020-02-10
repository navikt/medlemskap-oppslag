package no.nav.medlemskap.services.pdl

import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import mu.KotlinLogging
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.common.exceptions.IdenterIkkeFunnet
import no.nav.medlemskap.common.exceptions.GraphqlError
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.modell.pdl.HentIdenterResponse
import no.nav.medlemskap.modell.pdl.IdentGruppe
import no.nav.medlemskap.modell.pdl.hentIndenterQuery
import no.nav.medlemskap.services.sts.StsRestClient

private val logger = KotlinLogging.logger { }

class PdlClient(
        private val baseUrl: String,
        private val stsClient: StsRestClient,
        private val callIdGenerator: () -> String,
        private val configuration: Configuration,
        private val retry: Retry? = null
) {

    suspend fun hentIdenter(fnr: String): HentIdenterResponse {
        retry?.let {
            return it.executeSuspendFunction {
                hentIdenterRequest(fnr)
            }
        }
        return hentIdenterRequest(fnr)
    }

    private suspend fun hentIdenterRequest(fnr: String): HentIdenterResponse {

        return defaultHttpClient.post {
            url("$baseUrl")
            header(HttpHeaders.Authorization, "Bearer ${stsClient.oidcToken()}")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Call-Id", callIdGenerator.invoke())
            header("Nav-Consumer-Token", "Bearer ${stsClient.oidcToken()}")
            header("Nav-Consumer-Id", configuration.sts.username)
            body = hentIndenterQuery(fnr)
        }
    }

    suspend fun healthCheck(): HttpResponse {
        return defaultHttpClient.options {
            url("$baseUrl")
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Consumer-Id", configuration.sts.username)
        }
    }
}

class PdlService(val pdlClient: PdlClient) {
    suspend fun hentAktorId(fnr: String): String {
        val pdlResponse = pdlClient.hentIdenter(fnr)
        pdlResponse.errors?.let { errors ->
            logger.warn { "Fikk følgende feil fra PDL: ${objectMapper.writeValueAsString(errors)}" }
            throw GraphqlError(errors.first())
        }

        return pdlClient.hentIdenter(fnr).data.hentIdenter?.identer?.first {
            !it.historisk && it.type == IdentGruppe.AKTORID
        }?.ident ?: throw IdenterIkkeFunnet()
    }

}


