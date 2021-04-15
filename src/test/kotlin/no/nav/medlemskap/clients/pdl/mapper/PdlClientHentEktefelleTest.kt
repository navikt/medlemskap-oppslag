package no.nav.medlemskap.clients.pdl.mapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.medlemskap.clients.pdl.PdlClient
import no.nav.medlemskap.clients.sts.StsRestClient
import no.nav.medlemskap.common.cioHttpClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PdlClientHentEktefelleTest {

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

        val pdlClient = PdlClient(server.baseUrl(), stsClient, username, cioHttpClient)

        val pdlResponse = runBlocking { pdlClient.hentPerson("1234567890", callId) }

        assertEquals("NOR", pdlResponse.data?.hentPerson?.statsborgerskap?.first()?.land)
    }

    val pdlRequestMapping: MappingBuilder = post(urlPathEqualTo("/"))
        .withHeader(HttpHeaders.Authorization, equalTo("Bearer dummytoken"))
        .withHeader("Accept", containing("application/json"))
        .withHeader("Nav-Consumer-Token", equalTo("Bearer dummytoken"))
// .withRequestBody()

    val pdlResponse =
        """
     {
            "data": {
              "hentPerson": {
                "forelderBarnRelasjon": [
                  {
                    "relatertPersonsIdent": "20041276216",
                    "relatertPersonsRolle": "BARN",
                    "minRolleForPerson": "FAR"
                  }
                ],
                "statsborgerskap": [
                  {
                    "land": "NOR",
                    "gyldigFraOgMed": "1973-11-11",
                    "gyldigTilOgMed": null,
                    "metadata": {
                        "historisk": true
                    }
                  }
                ],
                "sivilstand": [
                  {
                    "type": "GIFT",
                    "gyldigFraOgMed": "2020-09-15",
                    "relatertVedSivilstand": "13128619857"
                  }
                ],
                "bostedsadresse": [
                  {
                    "angittFlyttedato": "1973-11-11",
                    "vegadresse": {
                      "postnummer": "4994"
                    },
                    "matrikkeladresse": null,
                    "ukjentBosted": null,
                    "metadata": {
                        "historisk": false
                    }
                  }
                ],
                "doedsfall": [],
                "kontaktadresse": [],
                "oppholdsadresse": []
              }
            }
}  
        """.trimIndent()
}
