package no.nav.medlemskap.common

import io.ktor.server.engine.*
import io.restassured.RestAssured
import io.restassured.config.ObjectMapperConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.http.Header
import io.restassured.response.ResponseBodyExtractionOptions
import io.restassured.specification.RequestSpecification
import kotlinx.coroutines.runBlocking
import no.nav.medlemskap.ApplicationState
import no.nav.medlemskap.clients.Services
import no.nav.medlemskap.config.AzureAdOpenIdConfiguration
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.createHttpServer
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.Customization
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.skyscreamer.jsonassert.comparator.CustomComparator
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class TjenesteKallFeilmeldingerTest {

    protected fun RequestSpecification.When(): RequestSpecification {
        return this.`when`()
    }

    protected inline fun <reified T> ResponseBodyExtractionOptions.to(): T {
        return this.`as`(T::class.java)
    }

    companion object {

        private val configuration = Configuration(
            azureAd = Configuration.AzureAd(
                authorityEndpoint = "http://localhost/"
            )
        )
        private val services = Services(configuration)
        private val openIdConfiguration = AzureAdOpenIdConfiguration("http://localhost", "", "", "")

        private var applicationState = ApplicationState(false, false)

        private lateinit var server: ApplicationEngine

        @BeforeAll
        @JvmStatic
        fun startServer() {

            if (!applicationState.running) {
                val applicationServer = createHttpServer(
                    applicationState = applicationState,
                    useAuthentication = false,
                    configuration = configuration,
                    azureAdOpenIdConfiguration = openIdConfiguration,
                    services = services,
                    port = 7072,
                    createDatagrunnlag = ::mockCreateDatagrunnlag
                )

                Runtime.getRuntime().addShutdownHook(
                    Thread {
                        applicationState.initialized = false
                        applicationServer.stop(5000, 5000)
                    }
                )

                applicationServer.start()
                applicationState.initialized = true
                applicationState.running = true

                RestAssured.baseURI = "http://localhost"
                RestAssured.basePath = "/"
                RestAssured.port = 7072
                RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
                    ObjectMapperConfig.objectMapperConfig()
                        .jackson2ObjectMapperFactory { _, _ -> objectMapper }
                )
            }
        }
    }

    @Test
    fun `Request body med fom som null får 400 respons`() {
        val faktiskResponse = RestAssured.given()
            .body(inputMedFomLikeNull)
            .header(Header("Content-Type", "application/json"))
            .post("/")
            .then()
            .statusCode(400)
            .extract().asString()

        val forventetResponse =
            """
            {
              "url" : "/",
              "message" : "Instantiation of [simple type, class no.nav.medlemskap.domene.InputPeriode] value failed for JSON property fom due to missing (therefore NULL) value for creator parameter fom which is a non-nullable type\n at [Source: (InputStreamReader); line: 6, column: 5] (through reference chain: no.nav.medlemskap.domene.Request[\"periode\"]->no.nav.medlemskap.domene.InputPeriode[\"fom\"])",
              "cause" : "com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException: Instantiation of [simple type, class no.nav.medlemskap.domene.InputPeriode] value failed for JSON property fom due to missing (therefore NULL) value for creator parameter fom which is a non-nullable type\n at [Source: (InputStreamReader); line: 6, column: 5] (through reference chain: no.nav.medlemskap.domene.Request[\"periode\"]->no.nav.medlemskap.domene.InputPeriode[\"fom\"])",
              "code" : {
                "value" : 400,
                "description" : "Bad Request"
              },
              "callId" : { }
            }
            """.trimIndent()

        JSONAssert.assertEquals(
            forventetResponse, faktiskResponse,
            CustomComparator(
                JSONCompareMode.STRICT,
                Customization("tidspunkt") { _, _ -> true }
            )
        )
    }

    @Test
    fun `Request uten body får 400 respons`() {
        val faktiskResponse = RestAssured.given()
            .body("")
            .header(Header("Content-Type", "application/json"))
            .post("/")
            .then()
            .statusCode(400)
            .extract().asString()

        val forventetResponse =
            """
            {
              "url" : "/",
              "message" : "No content to map due to end-of-input\n at [Source: (InputStreamReader); line: 1, column: 0]",
              "cause" : "com.fasterxml.jackson.databind.exc.MismatchedInputException: No content to map due to end-of-input\n at [Source: (InputStreamReader); line: 1, column: 0]",
              "code" : {
                "value" : 400,
                "description" : "Bad Request"
              },
              "callId" : { }
            }
            """.trimIndent()

        JSONAssert.assertEquals(
            forventetResponse, faktiskResponse,
            CustomComparator(
                JSONCompareMode.STRICT,
                Customization("tidspunkt") { _, _ -> true }
            )
        )
    }

    @Test
    fun `Request med tom dato før fom dato får 400 respons`() {
        val faktiskResponse = RestAssured.given()
            .body(inputMedFomDatoFørTomDato)
            .header(Header("Content-Type", "application/json"))
            .post("/")
            .then()
            .statusCode(400)
            .extract().asString()

        val forventetResponse =
            """
            {
                "url" : "/",
                "message" : "Periode tom kan ikke være før periode fom",
                "cause" : "io.ktor.features.BadRequestException: Periode tom kan ikke være før periode fom",
                "code" : {
                    "value" : 400,
                    "description" : "Bad Request"
                },
                "callId" : { }
            }
            """.trimIndent()

        JSONAssert.assertEquals(
            forventetResponse, faktiskResponse,
            CustomComparator(
                JSONCompareMode.STRICT,
                Customization("tidspunkt") { _, _ -> true }
            )
        )
    }

    @Test
    fun `Request med ugyldig fnr får 400 respons`() {
        val faktiskResponse = RestAssured.given()
            .body(inputMedUgyldigFnr)
            .header(Header("Content-Type", "application/json"))
            .post("/")
            .then()
            .statusCode(400)
            .extract().asString()

        val forventetResponse =
            """
            {
                "url" : "/",
                "message" : "Ugyldig fødselsnummer",
                "cause" : "io.ktor.features.BadRequestException: Ugyldig fødselsnummer",
                "code" : {
                    "value" : 400,
                    "description" : "Bad Request"
                },
                "callId" : { }
            }
            """.trimIndent()

        JSONAssert.assertEquals(
            forventetResponse, faktiskResponse,
            CustomComparator(
                JSONCompareMode.STRICT,
                Customization("tidspunkt") { _, _ -> true }
            )
        )
    }

    @Test
    fun `Request med tom før 2016 får 400 respons`() {
        val faktiskResponse = RestAssured.given()
            .body(inputTomFør2016)
            .header(Header("Content-Type", "application/json"))
            .post("/")
            .then()
            .statusCode(400)
            .extract().asString()

        val forventetResponse =
            """
            {
                "url" : "/",
                "message" : "Periode fom kan ikke være før 2016-01-01",
                "cause" : "io.ktor.features.BadRequestException: Periode fom kan ikke være før 2016-01-01",
                "code" : {
                    "value" : 400,
                    "description" : "Bad Request"
                },
                "callId" : { }
            }
            """.trimIndent()

        JSONAssert.assertEquals(
            forventetResponse, faktiskResponse,
            CustomComparator(
                JSONCompareMode.STRICT,
                Customization("tidspunkt") { _, _ -> true }
            )
        )
    }

    @Test
    fun `Request med tom før fom i Json får 400 respons`() {
        val faktiskResponse = RestAssured.given()
            .body(inputMedTomFørFomIJson)
            .header(Header("Content-Type", "application/json"))
            .post("/")
            .then()
            .statusCode(400)
            .extract().asString()

        val forventetResponse =
            """
            {
                "url" : "/",
                "message" : "Instantiation of [simple type, class no.nav.medlemskap.domene.Request] value failed for JSON property fnr due to missing (therefore NULL) value for creator parameter fnr which is a non-nullable type\n at [Source: (InputStreamReader); line: 10, column: 1] (through reference chain: no.nav.medlemskap.domene.Request[\"fnr\"])",
                "cause" : "com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException: Instantiation of [simple type, class no.nav.medlemskap.domene.Request] value failed for JSON property fnr due to missing (therefore NULL) value for creator parameter fnr which is a non-nullable type\n at [Source: (InputStreamReader); line: 10, column: 1] (through reference chain: no.nav.medlemskap.domene.Request[\"fnr\"])",
                "code" : {
                    "value" : 400,
                    "description" : "Bad Request"
                },
                "callId" : { }
            }
            """.trimIndent()

        JSONAssert.assertEquals(
            forventetResponse, faktiskResponse,
            CustomComparator(
                JSONCompareMode.STRICT,
                Customization("tidspunkt") { _, _ -> true }
            )
        )
    }

    @Test
    fun `Request med ugyldig body får 400 respons`() {
        val faktiskResponse = RestAssured.given()
            .body(ugyldigInput)
            .header(Header("Content-Type", "application/json"))
            .post("/")
            .then()
            .statusCode(400)
            .extract().asString()

        val forventetResponse =
            """
            {
                "url" : "/",
                "message" : "Instantiation of [simple type, class no.nav.medlemskap.domene.Request] value failed for JSON property fnr due to missing (therefore NULL) value for creator parameter fnr which is a non-nullable type\n at [Source: (InputStreamReader); line: 1, column: 14] (through reference chain: no.nav.medlemskap.domene.Request[\"fnr\"])",
                "cause" : "com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException: Instantiation of [simple type, class no.nav.medlemskap.domene.Request] value failed for JSON property fnr due to missing (therefore NULL) value for creator parameter fnr which is a non-nullable type\n at [Source: (InputStreamReader); line: 1, column: 14] (through reference chain: no.nav.medlemskap.domene.Request[\"fnr\"])",
                "code" : {
                    "value" : 400,
                    "description" : "Bad Request"
                },
                "callId" : { }
            }
            """.trimIndent()

        JSONAssert.assertEquals(
            forventetResponse, faktiskResponse,
            CustomComparator(
                JSONCompareMode.STRICT,
                Customization("tidspunkt") { _, _ -> true }
            )
        )
    }

    suspend fun mockCreateDatagrunnlag(
        fnr: String,
        callId: String,
        periode: InputPeriode,
        brukerinput: Brukerinput,
        services: Services,
        clientId: String?,
        ytelseFraRequest: Ytelse?
    ): Datagrunnlag = runBlocking {

        val ytelse = Ytelse.SYKEPENGER

        Datagrunnlag(
            periode = periode,
            brukerinput = brukerinput,
            personhistorikk = personhistorikk(),
            pdlpersonhistorikk = personhistorikk(),
            medlemskap = listOf(Medlemskap("dekning", enDato(), enAnnenDato(), true, Lovvalg.ENDL, "NOR", PeriodeStatus.GYLD)),
            arbeidsforhold = listOf(arbeidsforhold()),
            oppgaver = listOf(Oppgave(enDato(), Prioritet.NORM, Status.AAPNET, "Tema")),
            dokument = listOf(Journalpost("Id", "Tittel", "Posttype", "Status", "Tema", listOf(Dokument("Id", "Tittel")))),
            ytelse = ytelse,
            personHistorikkRelatertePersoner = listOf(personhistorikkRelatertPerson()),
            dataOmEktefelle = DataOmEktefelle(personhistorikkEktefelle(), listOf(arbeidsforhold()))
        )
    }

    private fun arbeidsforhold(): Arbeidsforhold {
        return Arbeidsforhold(
            Periode(enDato(), enAnnenDato()),
            listOf(Utenlandsopphold("SWE", Periode(enDato(), enAnnenDato()), YearMonth.of(2010, 1))),
            OpplysningspliktigArbeidsgiverType.Organisasjon,
            Arbeidsgiver("type", "identifikator", listOf(Ansatte(10, Bruksperiode(enDato(), enAnnenDato()), Gyldighetsperiode(enDato(), enAnnenDato()))), listOf("Konkursstatus")),
            Arbeidsforholdstype.NORMALT,
            listOf(Arbeidsavtale(Periode(enDato(), enAnnenDato()), "yrkeskode", Skipsregister.NIS, 100.toDouble()))
        )
    }

    private fun personhistorikk(): Personhistorikk {
        return Personhistorikk(
            statsborgerskap = listOf(Statsborgerskap("NOR", enDato(), enAnnenDato())),
            personstatuser = listOf(FolkeregisterPersonstatus(PersonStatus.BOSA, enDato(), enAnnenDato())),
            bostedsadresser = listOf(Adresse("NOR", enDato(), enAnnenDato())),
            postadresser = listOf(Adresse("NOR", enDato(), enAnnenDato())),
            midlertidigAdresser = listOf(Adresse("NOR", enDato(), enAnnenDato())),
            sivilstand = listOf(Sivilstand(Sivilstandstype.GIFT, enDato(), enAnnenDato(), ektefelleFnr(), folkeregistermetadata())),
            familierelasjoner = listOf(Familierelasjon(barnFnr(), Familierelasjonsrolle.BARN, Familierelasjonsrolle.FAR, folkeregistermetadata())),
            kontaktadresser = emptyList(),
            oppholdsadresser = emptyList()

        )
    }

    private fun personhistorikkRelatertPerson(): PersonhistorikkRelatertPerson {
        return PersonhistorikkRelatertPerson(
            ident = ektefelleFnr(),
            personstatuser = listOf(FolkeregisterPersonstatus(PersonStatus.BOSA, enDato(), enAnnenDato())),
            bostedsadresser = listOf(Adresse("NOR", enDato(), enAnnenDato())),
            postadresser = listOf(Adresse("NOR", enDato(), enAnnenDato())),
            midlertidigAdresser = listOf(Adresse("NOR", enDato(), enAnnenDato()))
        )
    }

    private fun personhistorikkEktefelle(): PersonhistorikkEktefelle {
        return PersonhistorikkEktefelle(
            ident = ektefelleFnr(),
            barn = listOf(PersonhistorikkBarn(ident = barnFnr()))
        )
    }

    private fun folkeregistermetadata() = Folkeregistermetadata(etTidspunkt(), etTidspunkt(), etTidspunkt())

    private fun ektefelleFnr() = "0101197512345"
    private fun barnFnr() = "0101201012345"
    private fun enDato() = LocalDate.of(1975, 10, 10)
    private fun enAnnenDato() = LocalDate.of(2020, 8, 1)
    private fun etTidspunkt() = LocalDateTime.of(2020, 6, 20, 10, 0)

    private val inputMedFomLikeNull =
        """
        {
            "fnr": "15076500565",
            "periode": {
                "fom": null,
                "tom": "2019-12-31"
            },
            "brukerinput": {
                "arbeidUtenforNorge": false
            }
        }
        """.trimIndent()

    private val inputMedFomDatoFørTomDato =
        """
        {
            "fnr": "123456789",
            "periode": {
                "fom": "2019-01-01", 
                "tom": "2015-12-31"
            }, 
        "brukerinput": {
            "arbeidUtenforNorge": false
        }
}
        """.trimIndent()

    private val inputMedUgyldigFnr =
        """
        {
            "fnr": "1",
            "periode": {
                "fom": "2019-01-01", 
                "tom": "2019-12-31"
            }, 
        "brukerinput": {
            "arbeidUtenforNorge": false
        }
}
        """.trimIndent()

    private val inputTomFør2016 =
        """
        {
            "fnr": "123456789",
            "periode": {
                "fom": "2015-12-31", 
                "tom": "2019-12-31"
            }, 
        "brukerinput": {
            "arbeidUtenforNorge": false
        }
}
        """.trimIndent()

    private val inputMedTomFørFomIJson =
        """
        {
            "fødselsnummer": "123456789",
            "periode": {
                "tom": "2019-12-31", 
                "fom": "2019-12-31"
            }, 
        "brukerinput": {
            "arbeidUtenforNorge": false
        }
}
        """.trimIndent()

    private val ugyldigInput =
        """{"foo": "bar"}""".trimIndent()
}
