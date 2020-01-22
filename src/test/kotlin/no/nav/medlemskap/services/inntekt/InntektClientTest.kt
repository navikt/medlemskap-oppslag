package no.nav.medlemskap.services.inntekt

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
import no.nav.medlemskap.config.AzureAdOpenIdConfiguration
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.services.sts.StsRestClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth

class InntektClientTest {

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

        stubFor(queryMapping.willReturn(
                aResponse()
                        .withStatus(HttpStatusCode.OK.value)
                        .withHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                        .withBody(inntektResponse)
        ))

        val client = InntektClient(server.baseUrl(), stsClient, callId, config)

        val response = runBlocking { client.hentInntektListe("10108000398", LocalDate.of(2016, 1, 1), LocalDate.of(2016, 8, 1)) }
        println(response)

        assertEquals(YearMonth.of(2016, 1), response.arbeidsInntektMaaned?.get(0)?.aarMaaned)
        assertEquals("LOENNSINNTEKT", response.arbeidsInntektMaaned?.get(0)?.arbeidsInntektInformasjon?.inntektListe?.get(0)?.inntektType)
        assertEquals(25000.0, response.arbeidsInntektMaaned?.get(0)?.arbeidsInntektInformasjon?.inntektListe?.get(0)?.beloep)
        assertEquals(YearMonth.of(2020, 1), response.arbeidsInntektMaaned?.get(0)?.arbeidsInntektInformasjon?.inntektListe?.get(0)?.leveringstidspunkt)
        assertEquals(null, response.arbeidsInntektMaaned?.get(0)?.avvikListe)
    }


}

private val config = Configuration(
        azureAd = Configuration.AzureAd(
                openIdConfiguration = AzureAdOpenIdConfiguration("", "", "", "")
        )
)

private val inntektRequest = """
    {
        "ident": {
            "identifikator": "10108000398",
            "aktoerType": "NATURLIG_IDENT"
        },
        "ainntektsfilter": "MedlemskapA-inntekt",
        "maanedFom": "2016-01",
        "maanedTom": "2016-08",
        "formaal": "Medlemskap"
    }
""".trimIndent()

private val queryMapping: MappingBuilder = post(urlPathEqualTo("/hentinntektliste"))
        .withHeader(HttpHeaders.Accept, equalTo("application/json"))
        .withHeader(HttpHeaders.ContentType, equalTo("application/json"))
        .withHeader(HttpHeaders.Authorization, equalTo("Bearer dummytoken"))
        .withHeader("Nav-Consumer-Id", equalTo("test"))
        .withHeader("Nav-Call-Id", equalTo("12345"))
        .withRequestBody(equalToJson(inntektRequest))

private val inntektResponse = """
    {
        "arbeidsInntektMaaned": [
            {
                "aarMaaned": "2016-01",
                "arbeidsInntektInformasjon": {
                    "inntektListe": [
                        {
                            "inntektType": "LOENNSINNTEKT",
                            "beloep": 25000,
                            "fordel": "kontantytelse",
                            "inntektskilde": "A-ordningen",
                            "inntektsperiodetype": "Maaned",
                            "inntektsstatus": "LoependeInnrapportert",
                            "leveringstidspunkt": "2020-01",
                            "utbetaltIMaaned": "2016-01",
                            "opplysningspliktig": {
                                "identifikator": "873152362",
                                "aktoerType": "ORGANISASJON"
                            },
                            "virksomhet": {
                                "identifikator": "873152362",
                                "aktoerType": "ORGANISASJON"
                            },
                            "inntektsmottaker": {
                                "identifikator": "10108000398",
                                "aktoerType": "NATURLIG_IDENT"
                            },
                            "inngaarIGrunnlagForTrekk": true,
                            "utloeserArbeidsgiveravgift": true,
                            "informasjonsstatus": "InngaarAlltid",
                            "beskrivelse": "fastloenn",
                            "skatteOgAvgiftsregel": "nettoloenn"
                        },
                        {
                            "inntektType": "LOENNSINNTEKT",
                            "beloep": 25000,
                            "fordel": "kontantytelse",
                            "inntektskilde": "A-ordningen",
                            "inntektsperiodetype": "Maaned",
                            "inntektsstatus": "LoependeInnrapportert",
                            "leveringstidspunkt": "2020-01",
                            "utbetaltIMaaned": "2016-01",
                            "opplysningspliktig": {
                                "identifikator": "973063804",
                                "aktoerType": "ORGANISASJON"
                            },
                            "virksomhet": {
                                "identifikator": "973063804",
                                "aktoerType": "ORGANISASJON"
                            },
                            "inntektsmottaker": {
                                "identifikator": "10108000398",
                                "aktoerType": "NATURLIG_IDENT"
                            },
                            "inngaarIGrunnlagForTrekk": true,
                            "utloeserArbeidsgiveravgift": true,
                            "informasjonsstatus": "InngaarAlltid",
                            "beskrivelse": "fastloenn"
                        }
                    ]
                }
            }
        ],
        "ident": {
            "identifikator": "10108000398",
            "aktoerType": "NATURLIG_IDENT"
        }
    }
""".trimIndent()