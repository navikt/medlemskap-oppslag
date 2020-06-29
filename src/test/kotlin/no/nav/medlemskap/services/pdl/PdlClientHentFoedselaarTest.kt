package no.nav.medlemskap.services.pdl

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.medlemskap.common.cioHttpClient
import no.nav.medlemskap.services.sts.StsRestClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PdlClientHentFoedselaarTest {

    companion object {
        val server: WireMockServer = WireMockServer(WireMockConfiguration.options().dynamicPort())

        @BeforeAll
        @JvmStatic
        fun start() {
            server.start()
        }

        @AfterAll
        @JvmStatic
        fun stop() {
            server.stop()
        }
    }

    @BeforeEach
    fun configure() {
        configureFor(server.port())
    }

    @Test
    fun `henter foedselsaar`() {
        val callId = "123456"
        val username = "whatever"
        val stsClient: StsRestClient = mockk()
        coEvery { stsClient.oidcToken() } returns "dummytoken"

        stubFor(pdlRequestMapping
                .willReturn(
                        aResponse()
                                .withStatus(HttpStatusCode.OK.value)
                                .withHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                .withBody(pdlResponse)
                ))

        val pdlClient = PdlClient(server.baseUrl(), stsClient, username, cioHttpClient)

        val pdlResponse = runBlocking { pdlClient.hentFoedselsaar("1234567890", callId) }

        assertEquals(1980, pdlResponse.data?.hentPerson?.foedsel?.first()?.foedselsaar)
    }


    val pdlRequestMapping: MappingBuilder = post(urlPathEqualTo("/"))
            .withHeader(HttpHeaders.Authorization, equalTo("Bearer dummytoken"))
            .withHeader("Accept", containing("application/json"))
            .withHeader("Nav-Consumer-Token", equalTo("Bearer dummytoken"))
//.withRequestBody()

    val pdlResponse = """{"data":{"hentPerson":{"foedsel":[{"foedselsaar":1980}]}}}""".trimIndent()

}
