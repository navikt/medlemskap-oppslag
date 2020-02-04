package no.nav.medlemskap.services.oppgave

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.ServerResponseException
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.medlemskap.services.aareg.medlClientTest
import no.nav.medlemskap.services.medl.MedlClient
import no.nav.medlemskap.services.sts.StsRestClient
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate

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


    @Test
    fun `tester ServerResponseException`() {


        val callIdGenerator: () -> String = { "123456" }
        val stsClient: StsRestClient = mockk()
        coEvery { stsClient.oidcToken() } returns "dummytoken"

        WireMock.stubFor(oppgaveRequestMapping.willReturn(
                WireMock.aResponse()
                        .withStatus(HttpStatusCode.InternalServerError.value)
                        .withHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())

        ))

        val oppgaveclient = OppgaveClient(server.baseUrl(), stsClient, callIdGenerator)

        Assertions.assertThrows(ServerResponseException::class.java) {
            runBlocking { oppgaveclient.hentOppgaver("1234567890") }
        }
    }

    @Test
    fun `tester ClientRequestException`() {

        val callIdGenerator: () -> String = { "123456" }
        val stsClient: StsRestClient = mockk()
        coEvery { stsClient.oidcToken() } returns "dummytoken"

        WireMock.stubFor(oppgaveRequestMapping.willReturn(
                WireMock.aResponse()
                        .withStatus(HttpStatusCode.Forbidden.value)
                        .withHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())

        ))

        val oppgaveclient = OppgaveClient(server.baseUrl(), stsClient, callIdGenerator)

        Assertions.assertThrows(ClientRequestException::class.java) {
            runBlocking { oppgaveclient.hentOppgaver("1234567890") }
        }
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
