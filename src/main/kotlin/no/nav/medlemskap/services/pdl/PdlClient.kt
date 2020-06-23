package no.nav.medlemskap.services.pdl

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
import no.nav.medlemskap.common.exceptions.GraphqlError
import no.nav.medlemskap.common.exceptions.IdenterIkkeFunnet
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.domene.Personhistorikk
import no.nav.medlemskap.services.runWithRetryAndMetrics
import no.nav.medlemskap.services.sts.StsRestClient

private val logger = KotlinLogging.logger { }

class PdlClient(
        private val baseUrl: String,
        private val stsClient: StsRestClient,
        private val configuration: Configuration,
        private val httpClient: HttpClient,
        private val retry: Retry? = null
) {
    suspend fun hentIdenter(fnr: String, callId: String): HentIdenterResponse {

        return runWithRetryAndMetrics("PDL", "HentIdenter", retry) {
            httpClient.post<HentIdenterResponse> {
                url("$baseUrl")
                header(HttpHeaders.Authorization, "Bearer ${stsClient.oidcToken()}")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("Nav-Consumer-Token", "Bearer ${stsClient.oidcToken()}")
                header("Nav-Consumer-Id", configuration.sts.username)
                body = hentIndenterQuery(fnr)
            }
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
                header("Nav-Consumer-Id", configuration.sts.username)
                body = hentPersonQuery(fnr)
            }
        }

    }

    suspend fun hentNasjonalitet(fnr: String, callId: String): String {
        return runWithRetryAndMetrics("PDL", "HentNasjonalitet", retry) {
            httpClient.post<String> {
                url("$baseUrl")
                header(HttpHeaders.Authorization, "Bearer ${stsClient.oidcToken()}")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("Nav-Consumer-Token", "Bearer ${stsClient.oidcToken()}")
                header("Nav-Consumer-Id", configuration.sts.username)
                body = hentNasjonalitetQuery(fnr)
            }
        }

    }

    suspend fun hentFoedselsaar(fnr: String, callId: String): HentFoedselsaarResponse {
        return runWithRetryAndMetrics("PDL", "HentFoedselsaar", retry) {
            httpClient.post<HentFoedselsaarResponse> {
                header(HttpHeaders.Authorization, "Bearer ${stsClient.oidcToken()}")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("Nav-Consumer-Token", "Bearer ${stsClient.oidcToken()}")
                header("Nav-Consumer-Id", configuration.sts.username)
                body = hentFoedselsaarQuery(fnr)
            }
        }
    }

    suspend fun healthCheck(): HttpResponse {
        return httpClient.options {
            url("$baseUrl")
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Consumer-Id", configuration.sts.username)
        }
    }
}

class PdlService(private val pdlClient: PdlClient, private val clusterName: String = "dev-fss") {

    suspend fun hentAktorId(fnr: String, callId: String): String {
        val pdlResponse = pdlClient.hentIdenter(fnr, callId)

        // Hack for å overleve manglende aktørID i ikke-konsistente data i Q2
        if (pdlResponse.errors != null && clusterName == "dev-fss") {
            return "111111111111"
        }

        pdlResponse.errors?.let { errors ->
            logger.warn { "Fikk følgende feil fra PDL: ${objectMapper.writeValueAsString(errors)}" }
            throw GraphqlError(errors.first(), "PDL")
        }

        return pdlResponse.data.hentIdenter?.identer?.first {
            !it.historisk && it.gruppe == IdentGruppe.AKTORID
        }?.ident ?: throw IdenterIkkeFunnet()
    }

    suspend fun hentAlleAktorIder(fnr: String, callId: String): List<String> {
        val pdlResponse = pdlClient.hentIdenter(fnr, callId)

        // Hack for å overleve manglende aktørID i ikke-konsistente data i Q2
        if (pdlResponse.errors != null && clusterName == "dev-fss") {
            return listOf("111111111111")
        }

        pdlResponse.errors?.let { errors ->
            logger.warn { "Fikk følgende feil fra PDL: ${objectMapper.writeValueAsString(errors)}" }
            throw GraphqlError(errors.first(), "PDL")
        }

        return pdlResponse.data.hentIdenter?.identer
                ?.filter { it.gruppe == IdentGruppe.AKTORID }
                ?.map { it.ident } ?: throw IdenterIkkeFunnet()

    }

    suspend fun hentPersonHistorikk(fnr: String, callId: String): Personhistorikk {
        return mapTilPersonHistorikk(pdlClient.hentPerson(fnr, callId))

/*        // Hack for å overleve manglende aktørID i ikke-konsistente data i Q2
        if (pdlResponse.errors != null && clusterName == "dev-fss") {
            return "111111111111"
        }

        pdlResponse.errors?.let { errors ->
            logger.warn { "Fikk følgende feil fra PDL: ${objectMapper.writeValueAsString(errors)}" }
            throw GraphqlError(errors.first(), "PDL")
        }*/

    }

    suspend fun hentFoedselsaar(fnr:String, callId: String): HentFoedselsaarResponse {
        return pdlClient.hentFoedselsaar(fnr, callId)
    }
}


