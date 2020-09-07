package no.nav.medlemskap.clients.pdl

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

        assertEquals("TESTFAMILIEN", pdlResponse.data?.hentPerson?.navn?.first()?.etternavn)
    }

    val pdlRequestMapping: MappingBuilder = post(urlPathEqualTo("/"))
        .withHeader(HttpHeaders.Authorization, equalTo("Bearer dummytoken"))
        .withHeader("Accept", containing("application/json"))
        .withHeader("Nav-Consumer-Token", equalTo("Bearer dummytoken"))
// .withRequestBody()

    val pdlResponse =
        """
            {"data": {"hentPerson":{"adressebeskyttelse":[],"kjoenn":[{"kjoenn":"MANN"}],"familierelasjoner":[],"statsborgerskap":[{"land":"NOR","gyldigFraOgMed":"2010-10-21","gyldigTilOgMed":null}],"navn":[{"fornavn":"AREMARK","mellomnavn":null,"etternavn":"TESTFAMILIEN"}],"sivilstand":[{"type":"UGIFT","gyldigFraOgMed":"2010-02-21","relatertVedSivilstand":null,"folkeregistermetadata":{"ajourholdstidspunkt":"2020-02-19T10:36:54","gyldighetstidspunkt":"2020-02-19T10:36:54","opphoerstidspunkt":null}}],"bostedsadresse":[],"kontaktadresse":[], "oppholdsadresse":[]}}}
        """.trimIndent()
}
