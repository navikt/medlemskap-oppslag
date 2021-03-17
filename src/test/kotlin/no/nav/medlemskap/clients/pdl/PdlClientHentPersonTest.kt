package no.nav.medlemskap.clients.pdl

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.medlemskap.clients.sts.StsRestClient
import no.nav.medlemskap.common.cioHttpClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PdlClientHentPersonTest {

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
    fun `henter person som ikke finnes`() {
        val callId = "123455"
        val username = "whatever"
        val stsClient: StsRestClient = mockk()
        coEvery { stsClient.oidcToken() } returns "dummytoken"
        stubFor(
            pdlRequestMapping
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatusCode.OK.value)
                        .withHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                        .withBody(pdlResponseNotFound)
                )
        )

        val pdlClient = createPdlClient(stsClient, username)
        val pdlResponse = runBlocking { pdlClient.hentPerson("1234", callId) }
        assertEquals("Fant ikke person", pdlResponse.errors?.get(0)?.message)
    }

    private fun createPdlClient(stsClient: StsRestClient, username: String): PdlClient {
        return PdlClient(server.baseUrl(), stsClient, username, cioHttpClient, null, "123")
    }

    @Test
    fun `henter person`() {
        val callId = "123456"
        val username = "whatever"
        val stsClient: StsRestClient = mockk()
        coEvery { stsClient.oidcToken() } returns "dummytoken"

        stubFor(
            pdlRequestMapping
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatusCode.OK.value)
                        .withHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                        .withBody(pdlResponse)
                )
        )

        val pdlClient = createPdlClient(stsClient, username)

        val pdlResponse = runBlocking { pdlClient.hentPerson("1234567890", callId) }

        assertEquals("NOR", pdlResponse.data?.hentPerson?.statsborgerskap?.first()?.land)
    }

    val pdlRequestMapping: MappingBuilder = post(urlPathEqualTo("/"))
        .withHeader(HttpHeaders.Authorization, equalTo("Bearer dummytoken"))
        .withHeader("Accept", containing("application/json"))
        .withHeader("Nav-Consumer-Token", equalTo("Bearer dummytoken"))
// .withRequestBody()

    val pdlResponseNotFound =
        """
      {
            "errors": [
            {
                "message": "Fant ikke person",
                "locations": [
                {
                    "line": 2,
                    "column": 5
                }
                ],
                "path": [
                "hentPerson"
                ],
                "extensions": {
                "code": "not_found",
                "classification": "ExecutionAborted"
            }
            }
            ],
            "data": {
            "hentPerson": null
        }
        }
        """.trimIndent()

    val pdlResponse =
        """
        {
            "data": {
                "hentPerson": {
                    "familierelasjoner":[],
                    "statsborgerskap": [
                            {
                                "land":"NOR",
                                "gyldigFraOgMed": "2010-10-21",
                                "gyldigTilOgMed":  null,
                                "metadata": {
                                    "historisk": "true"
                                }
                            }
                    ],
                    "sivilstand": [
                            {
                            "type": "UGIFT",
                            "gyldigFraOgMed": "2010-02-21",
                            "relatertVedSivilstand": null
                            }
                    ],
                    "bostedsadresse":[],
                    "kontaktadresse":[], 
                    "oppholdsadresse":[],
                    "doedsfall": []
            
                 }   
             }     
         }
        """.trimIndent()
}
