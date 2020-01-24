package no.nav.medlemskap.services.aareg

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.medlemskap.config.AzureAdOpenIdConfiguration
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.services.sts.StsRestClient
import org.junit.jupiter.api.*
import org.opensaml.saml.saml1.core.Assertion
import java.time.LocalDate
import java.util.*

class AaregClientTest {

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
                    .withBody(aaRegResponse)
    ))

    val client = AaRegClient(server.baseUrl(), stsClient, callId)

    val response = runBlocking { client.hentArbeidsforhold("10109000398", LocalDate.of(2010, 1, 1), LocalDate.of(2016, 1, 1)) }
    println(response)


    Assertions.assertEquals("ERKONK", response[0].ansettelsesperiode.varslingskode)
    Assertions.assertEquals("Z990693", response[0].sporingsinformasjon.endretAv)



}

private val aaRegResponse = """
   [
     {
    "ansettelsesperiode": {
      "bruksperiode": {
        "fom": "2015-01-06T21:44:04.748",
        "tom": "2015-12-06T19:45:04"
      },
      "periode": {
        "fom": "2014-07-01",
        "tom": "2015-12-31"
      },
      "sporingsinformasjon": {
        "endretAv": "Z990693",
        "endretKilde": "AAREG",
        "endretKildereferanse": "referanse-fra-kilde",
        "endretTidspunkt": "2018-09-19T12:11:20.79",
        "opprettetAv": "srvappserver",
        "opprettetKilde": "EDAG",
        "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
        "opprettetTidspunkt": "2018-09-19T12:10:58.059"
      },
      "varslingskode": "ERKONK"
    },
    "antallTimerForTimeloennet": [
      {
        "antallTimer": 37.5,
        "periode": {
          "fom": "2014-07-01",
          "tom": "2015-12-31"
        },
        "rapporteringsperiode": "2018-05",
        "sporingsinformasjon": {
          "endretAv": "Z990693",
          "endretKilde": "AAREG",
          "endretKildereferanse": "referanse-fra-kilde",
          "endretTidspunkt": "2018-09-19T12:11:20.79",
          "opprettetAv": "srvappserver",
          "opprettetKilde": "EDAG",
          "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
          "opprettetTidspunkt": "2018-09-19T12:10:58.059"
        }
      }
    ],
    "arbeidsavtaler": [
      {
        "antallTimerPrUke": 37.5,
        "arbeidstidsordning": "ikkeSkift",
        "beregnetAntallTimerPrUke": 37.5,
        "bruksperiode": {
          "fom": "2015-01-06T21:44:04.748",
          "tom": "2015-12-06T19:45:04"
        },
        "gyldighetsperiode": {
          "fom": "2014-07-01",
          "tom": "2015-12-31"
        },
        "sistLoennsendring": "string",
        "sistStillingsendring": "string",
        "sporingsinformasjon": {
          "endretAv": "Z990693",
          "endretKilde": "AAREG",
          "endretKildereferanse": "referanse-fra-kilde",
          "endretTidspunkt": "2018-09-19T12:11:20.79",
          "opprettetAv": "srvappserver",
          "opprettetKilde": "EDAG",
          "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
          "opprettetTidspunkt": "2018-09-19T12:10:58.059"
        },
        "stillingsprosent": 49.5,
        "yrke": 2130123
      }
    ],
    "arbeidsforholdId": "abc-321",
    "arbeidsgiver": {
      "type": "Organisasjon"
    },
    "arbeidstaker": {
      "type": "Person",
      "aktoerId": 1234567890,
      "offentligIdent": 31126700000
    },
    "innrapportertEtterAOrdningen": true,
    "navArbeidsforholdId": 123456,
    "opplysningspliktig": {
      "type": "Organisasjon"
    },
    "permisjonPermitteringer": [
      {
        "periode": {
          "fom": "2014-07-01",
          "tom": "2015-12-31"
        },
        "permisjonPermitteringId": "123-xyz",
        "prosent": 50.5,
        "sporingsinformasjon": {
          "endretAv": "Z990693",
          "endretKilde": "AAREG",
          "endretKildereferanse": "referanse-fra-kilde",
          "endretTidspunkt": "2018-09-19T12:11:20.79",
          "opprettetAv": "srvappserver",
          "opprettetKilde": "EDAG",
          "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
          "opprettetTidspunkt": "2018-09-19T12:10:58.059"
        },
        "type": "permisjonMedForeldrepenger",
        "varslingskode": "string"
      }
    ],
    "registrert": "2018-09-18T11:12:29",
    "sistBekreftet": "2018-09-19T12:10:31",
    "sporingsinformasjon": {
      "endretAv": "Z990693",
      "endretKilde": "AAREG",
      "endretKildereferanse": "referanse-fra-kilde",
      "endretTidspunkt": "2018-09-19T12:11:20.79",
      "opprettetAv": "srvappserver",
      "opprettetKilde": "EDAG",
      "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
      "opprettetTidspunkt": "2018-09-19T12:10:58.059"
    },
    "type": "ordinaertArbeidsforhold",
    "utenlandsopphold": [
      {
        "landkode": "JPN",
        "periode": {
          "fom": "2014-07-01",
          "tom": "2015-12-31"
        },
        "rapporteringsperiode": "2017-12",
        "sporingsinformasjon": {
          "endretAv": "Z990693",
          "endretKilde": "AAREG",
          "endretKildereferanse": "referanse-fra-kilde",
          "endretTidspunkt": "2018-09-19T12:11:20.79",
          "opprettetAv": "srvappserver",
          "opprettetKilde": "EDAG",
          "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
          "opprettetTidspunkt": "2018-09-19T12:10:58.059"
        }
      }
    ]
  }
]
""".trimIndent()

private val queryMapping: MappingBuilder = WireMock.get(WireMock.urlPathEqualTo("/v1/arbeidstaker/arbeidsforhold"))
        .withHeader(HttpHeaders.Authorization, equalTo("Bearer dummytoken"))
        .withHeader("Nav-Consumer-Token", equalTo("Bearer dummytoken"))
        .withHeader("Nav-Personident", equalTo("10109000398"))
        .withHeader("Nav-Call-Id", equalTo("12345"))
        .withQueryParam("ansettelsesperiodeFom", equalTo("2010-01-01"))
        .withQueryParam("ansettelsesperiodeTom", equalTo("2016-01-01"))
        .withQueryParam("historikk", equalTo("true"))
        .withQueryParam("regelverk", equalTo("ALLE"))


}


