package no.nav.medlemskap.services.ereg

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.ServerResponseException
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.medlemskap.common.cioHttpClient
import no.nav.medlemskap.services.aareg.AaRegClient
import no.nav.medlemskap.services.sts.StsRestClient
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate

class EregClientTest {

    companion object {
        val server: WireMockServer = WireMockServer(WireMockConfiguration.options().dynamicPort())
        val httpClient = cioHttpClient


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
    fun `tester response`() {
        val callId = "12345"

        val stsClient: StsRestClient = mockk()
        coEvery { stsClient.oidcToken() } returns "dummytoken"

        WireMock.stubFor(queryMapping.willReturn(
                WireMock.aResponse()
                        .withStatus(HttpStatusCode.OK.value)
                        .withHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                        .withBody(eregResponse)
        ))

        val client = EregClient(server.baseUrl(), httpClient)

        val response = runBlocking { client.hentEnhetstype("977074010", callId, "test") }
        println(response)
        assertEquals(response, "NUF")

    }


    @Test
    fun `tester ServerResponseException`() {
        val callId = "12345"
        val stsClient: StsRestClient = mockk()
        coEvery { stsClient.oidcToken() } returns "dummytoken"

        WireMock.stubFor(queryMapping.willReturn(
                WireMock.aResponse()
                        .withStatus(HttpStatusCode.InternalServerError.value)
                        .withHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())

        ))
        val client = EregClient(server.baseUrl(), httpClient)

        Assertions.assertThrows(ServerResponseException::class.java) {
            runBlocking { client.hentEnhetstype("977074010", callId, "test") }
        }
    }

    @Test
    fun `tester ClientRequestException`() {
        val callId = "12345"
        val stsClient: StsRestClient = mockk()
        coEvery { stsClient.oidcToken() } returns "dummytoken"

        WireMock.stubFor(queryMapping.willReturn(
                WireMock.aResponse()
                        .withStatus(HttpStatusCode.Forbidden.value)
                        .withHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())

        ))

        val client = EregClient(server.baseUrl(), httpClient)

        Assertions.assertThrows(ClientRequestException::class.java) {
            runBlocking { client.hentEnhetstype("977074010", callId, "test") }
        }
    }

    @Test
    fun `404 gir null`() {
        val callId = "12345"
        val stsClient: StsRestClient = mockk()
        coEvery { stsClient.oidcToken() } returns "dummytoken"

        WireMock.stubFor(queryMapping.willReturn(
                WireMock.aResponse()
                        .withStatus(HttpStatusCode.NotFound.value)
                        .withHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())

        ))

        val client = EregClient(server.baseUrl(), httpClient)
        val response = runBlocking { client.hentEnhetstype("977074010", callId, "test") }
        Assertions.assertNull(response)
    }


    private val eregResponse = """
        {
          "organisasjonsnummer": "977074010",
          "navn": {
            "redigertnavn": "DANSKE BANK",
            "navnelinje1": "DANSKE BANK",
            "bruksperiode": {
              "fom": "2015-02-23T08:04:53.2"
            },
            "gyldighetsperiode": {
              "fom": "2012-11-21"
            }
          },
          "enhetstype": "NUF",
          "adresse": {
            "type": "Forretningsadresse",
            "adresselinje1": "Søndre gate 15",
            "postnummer": "7011",
            "landkode": "NO",
            "kommunenummer": "5001",
            "bruksperiode": {
              "fom": "2018-01-04T04:03:03.576"
            },
            "gyldighetsperiode": {
              "fom": "2018-01-01"
            }
          }
        }
    """.trimIndent()

    private val orgnummer = "977074010"
    private val queryMapping: MappingBuilder = WireMock.get(WireMock.urlPathEqualTo("/v1/organisasjon/$orgnummer/noekkelinfo"))
            .withHeader("Nav-Call-Id", equalTo("12345"))
            .withHeader("Nav-Consumer-Id", equalTo("test"))

}