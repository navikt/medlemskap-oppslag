package no.nav.medlemskap.services.pdl
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
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.services.medl.MedlClient
import no.nav.medlemskap.services.sts.StsRestClient
import org.junit.jupiter.api.*
import java.time.LocalDate


class pdlClientTest {

    companion object {
        val server: WireMockServer = WireMockServer(WireMockConfiguration.options().dynamicPort())
        private val config = Configuration()

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
    fun `tester response 200 OK`() {
        val callId = "12345"
        val stsClient: StsRestClient = mockk()
        coEvery { stsClient.oidcToken() } returns "dummytoken"

        WireMock.stubFor(queryMapping.willReturn(
                WireMock.aResponse()
                        .withStatus(HttpStatusCode.OK.value)
                        .withHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                        .withBody(hentPersonResponse)
        ))

        val client = MedlClient(server.baseUrl(), stsClient, config, cioHttpClient)
        val response = runBlocking { client.hentMedlemskapsunntak("10109000398", callId, LocalDate.of(2010, 1, 1), LocalDate.of(2016, 1, 1)) }

        Assertions.assertEquals("Full", response[0].dekning)
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

        val client = MedlClient(server.baseUrl(), stsClient, config, cioHttpClient)

        Assertions.assertThrows(ServerResponseException::class.java) {
            runBlocking { client.hentMedlemskapsunntak("10109000398", callId, LocalDate.of(2010, 1, 1), LocalDate.of(2016, 1, 1)) }
        }
    }

    @Test
    @Disabled
    fun `tester ClientRequestException`() {

        val callId = "12345"
        val stsClient: StsRestClient = mockk()
        coEvery { stsClient.oidcToken() } returns "dummytoken"

        WireMock.stubFor(queryMapping.willReturn(
                WireMock.aResponse()
                        .withStatus(HttpStatusCode.Forbidden.value)
                        .withHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())

        ))


        val client = PdlClient(server.baseUrl(), stsClient, config, cioHttpClient)


        Assertions.assertThrows(ClientRequestException::class.java) {
            runBlocking { client.hentPerson("10109000398", callId) }
        }
    }


    private val queryMapping: MappingBuilder = WireMock.post(WireMock.urlPathEqualTo("/"))
            .withHeader(HttpHeaders.Authorization, equalTo("Bearer dummytoken"))
         //   .withHeader("Nav-Personident", equalTo("10109000398"))
            .withHeader("Nav-Call-Id", equalTo("12345"))
            .withHeader("Nav-Consumer-Id", equalTo("test"))


    private val hentPersonResponse= """
HentPdlPersonResponse(data=HentPerson(hentPerson=Person(adressebeskyttelse=[Adressebeskyttelse(gradering=UGRADERT)], kjoenn=[Kjoenn(kjoenn=MANN)], navn=[Navn(fornavn=FORFJAMSET, mellomnavn=null, etternavn=TRANFLASKE)], bostedsadresse=[])), errors=null)
""".trimIndent()

}



