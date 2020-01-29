package no.nav.medlemskap.services.aareg

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.services.medl.MedlClient
import no.nav.medlemskap.services.sts.StsRestClient
import org.junit.jupiter.api.*
import java.time.LocalDate

class medlClientTest {

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
    fun `tester response`() {
        val callId: () -> String = { "12345" }


        val stsClient: StsRestClient = mockk()
        coEvery { stsClient.oidcToken() } returns "dummytoken"

        WireMock.stubFor(queryMapping.willReturn(
                WireMock.aResponse()
                        .withStatus(HttpStatusCode.OK.value)
                        .withHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                        .withBody(medlResponse)
        ))
        val client = MedlClient(server.baseUrl(), stsClient, callId, config)

        val response = runBlocking { client.hentMedlemskapsunntak("10109000398", LocalDate.of(2010, 1, 1), LocalDate.of(2016, 1, 1)) }
        println(response)


        Assertions.assertEquals("Full", response[0].dekning)
    }

    private val config = Configuration()

    private val medlResponse = """
[
  {
    "unntakId": 100064642,
    "ident": "25109209177",
    "fraOgMed": "2018-08-16",
    "tilOgMed": "2019-06-15",
    "status": "GYLD",
    "dekning": "Full",
    "helsedel": true,
    "medlem": true,
    "lovvalgsland": "NOR",
    "lovvalg": "ENDL",
    "grunnlag": "FTL_2-5",
    "sporingsinformasjon": {
      "versjon": 0,
      "registrert": "2018-06-03",
      "besluttet": "2018-07-16",
      "kilde": "LAANEKASSEN",
      "kildedokument": "Henv_Soknad",
      "opprettet": "2017-01-05T12:20:38",
      "opprettetAv": "BMEDL2003",
      "sistEndret": "2018-06-28T12:20:38",
      "sistEndretAv": "REG-150"
    },
    "studieinformasjon": {
      "statsborgerland": "NOR",
      "studieland": "USA",
      "delstudie": true,
      "soeknadInnvilget": true
    }
  }
]
""".trimIndent()

    private val queryMapping: MappingBuilder = WireMock.get(WireMock.urlPathEqualTo("/api/v1/medlemskapsunntak"))
            .withHeader(HttpHeaders.Authorization, equalTo("Bearer dummytoken"))
            .withHeader("Nav-Personident", equalTo("10109000398"))
            .withHeader("Nav-Call-Id", equalTo("12345"))
            .withHeader("Nav-Consumer-Id", equalTo("test"))
            .withQueryParam("fraOgMed", equalTo("2010-01-01"))
            .withQueryParam("fraOgMed", equalTo("2010-01-01"))


}


