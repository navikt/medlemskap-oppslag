package no.nav.medlemskap.services.oppgave

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.medlemskap.services.sts.StsRestClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OppgaveClientTest {

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
        WireMock.configureFor(server.port())
    }

    @Test
    fun `henter oppgaver`() {
        val callIdGenerator: () -> String = { "123456" }

        val stsClient: StsRestClient = mockk()
        coEvery { stsClient.oidcToken() } returns "dummytoken"

        stubFor(oppgaveRequestMapping
                .willReturn(
                        aResponse()
                                .withStatus(HttpStatusCode.OK.value)
                                .withHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                .withBody(oppgaveResponse)
                ))

        val oppgaveclient = OppgaveClient(server.baseUrl(), stsClient, callIdGenerator)

        val oppgaveResponse = runBlocking { oppgaveclient.hentOppgaver("1234567890") }
        val oppgave = oppgaveResponse.oppgaver[0]

        assertEquals(1, oppgaveResponse.antallTreffTotalt)
        assertEquals("Z000001", oppgave.tilordnetRessurs)
    }
}

val oppgaveRequestMapping: MappingBuilder = get(urlPathEqualTo("/api/v1/oppgaver"))
        .withHeader(HttpHeaders.Authorization, equalTo("Bearer dummytoken"))
        .withHeader("Accept", equalTo("application/json"))
        .withHeader("X-Correlation-Id", equalTo("123456"))
        .withQueryParam("aktoerId", equalTo("1234567890"))
        .withQueryParam("tema", equalTo("MED"))
        .withQueryParam("tema", equalTo("UFM"))
        .withQueryParam("tema", equalTo("TRY"))

val oppgaveResponse = """
    {
        "antallTreffTotalt": 1,
        "oppgaver": [
            {
                "aktivDato": "2010-01-01",
                "prioritet": "HOY",
                "status": "AAPNET",
                "versjon": 1,
                "tilordnetRessurs": "Z000001",
                "tema": "MED",
                "beskrivelse": "Testbeskrivelse",
                "shouldBeIgnored": "ign"
            }
        ]
    }
""".trimIndent()
