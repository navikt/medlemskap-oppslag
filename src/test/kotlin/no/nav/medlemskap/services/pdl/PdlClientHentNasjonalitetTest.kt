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

class PdlClientHentNasjonalitetTest {

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
    fun `henter nasjonalitetskode`() {
        val callId = "123456"
        val username = "Hatsune Miku"
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

        val pdlResponse = runBlocking { pdlClient.hentNasjonalitet("12345678910", callId) }

        assertEquals("NOR", pdlResponse)
    }


    val pdlRequestMapping: MappingBuilder = post(urlPathEqualTo("/"))
            .withHeader(HttpHeaders.Authorization, equalTo("Bearer dummytoken"))
            .withHeader("Accept", containing("application/json"))
            .withHeader("Nav-Consumer-Token", equalTo("Bearer dummytoken"))
//.withRequestBody()

    val pdlResponse =
            """{"data": {
                "hentPerson": {
                "statsborgerskap": [{"land": "NOR","gyldigFraOgMed": "2010-10-21","gyldigTilOgMed": null}]
    }}}""".trimIndent()

}
