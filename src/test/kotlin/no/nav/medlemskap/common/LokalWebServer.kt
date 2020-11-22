package no.nav.medlemskap.common

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.config.ObjectMapperConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.http.Header
import io.restassured.response.ValidatableResponse
import kotlinx.coroutines.runBlocking
import no.nav.medlemskap.ApplicationState
import no.nav.medlemskap.clients.Services
import no.nav.medlemskap.config.AzureAdOpenIdConfiguration
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.createHttpServer
import no.nav.medlemskap.cucumber.Medlemskapsparametre
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class LokalWebServer {
    companion object {

        private val configuration = Configuration(
            azureAd = Configuration.AzureAd(
                authorityEndpoint = "http://localhost/"
            )
        )
        private val services = Services(configuration)
        private val openIdConfiguration = AzureAdOpenIdConfiguration("http://localhost", "", "", "")

        private var applicationState = ApplicationState(false, false)

        var testdatagrunnlag: Datagrunnlag? = null

        fun startServer() {

            if (!applicationState.running) {
                val applicationServer = createHttpServer(
                    applicationState = applicationState,
                    useAuthentication = false,
                    configuration = configuration,
                    azureAdOpenIdConfiguration = openIdConfiguration,
                    services = services,
                    port = 7071,
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
                RestAssured.port = 7071
                RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
                    ObjectMapperConfig.objectMapperConfig()
                        .jackson2ObjectMapperFactory { _, _ -> objectMapper }
                )
            }
        }

        fun byggInput(medlemskapsparametre: Medlemskapsparametre): String =
            objectMapper.writeValueAsString(
                Request(
                    fnr = medlemskapsparametre.fnr!!,
                    periode = medlemskapsparametre.inputPeriode,
                    brukerinput = Brukerinput(medlemskapsparametre.harHattArbeidUtenforNorge),
                    førsteDagForYtelse = medlemskapsparametre.førsteDagForYtelse,
                    ytelse = medlemskapsparametre.ytelse
                )
            )

        fun respons(input: String): String =
            given()
                .body(input)
                .header(Header("Content-Type", "application/json"))
                .post("/")
                .then()
                .statusCode(200)
                .extract().asString()

        fun kontrakt(input: String): ValidatableResponse {
            val validationFilter = OpenApiValidationFilter("src/main/resources/lovme.yaml")

            return given()
                .filter(validationFilter)
                .body(input)
                .header(Header("Content-Type", "application/json"))
                .post("/")
                .then()
                .statusCode(200)
        }

        fun testResponsKode(input: String, statusKode: Int) {
            given()
                .body(input)
                .header(Header("Content-Type", "application/json"))
                .post("/")
                .then()
                .statusCode(statusKode)
                .extract().asString()
        }

        suspend fun mockCreateDatagrunnlag(
            request: Request,
            callId: String,
            services: Services,
            clientId: String?
        ): Datagrunnlag = runBlocking {
            if (testdatagrunnlag != null) {
                testdatagrunnlag!!
            }

            val ytelse = Ytelse.SYKEPENGER

            Datagrunnlag(
                periode = request.periode,
                førsteDagForYtelse = request.førsteDagForYtelse,
                brukerinput = request.brukerinput,
                pdlpersonhistorikk = personhistorikk(),
                medlemskap = listOf(Medlemskap("dekning", enDato(), enAnnenDato(), true, Lovvalg.ENDL, "NOR", PeriodeStatus.GYLD)),
                arbeidsforhold = listOf(arbeidsforhold()),
                oppgaver = listOf(Oppgave(enDato(), Prioritet.NORM, Status.AAPNET, "Tema")),
                dokument = listOf(Journalpost("Id", "Tittel", "Posttype", "Status", "Tema", listOf(Dokument("Id", "Tittel")))),
                ytelse = ytelse,
                dataOmBarn = listOf(DataOmBarn(personhistorikkBarn = personhistorikkForBarn())),
                dataOmEktefelle = DataOmEktefelle(personhistorikkEktefelle(), listOf(arbeidsforhold()))
            )
        }
    }
}

private fun arbeidsforhold(): Arbeidsforhold {

    return Arbeidsforhold(
        Periode(enDato(), enAnnenDato()),
        listOf(Utenlandsopphold("SWE", Periode(enDato(), enAnnenDato()), YearMonth.of(2010, 1))),
        OpplysningspliktigArbeidsgiverType.Organisasjon,
        Arbeidsgiver(
            "organisasjonsnummer",
            listOf(Ansatte(10, Bruksperiode(enDato(), enAnnenDato()), Gyldighetsperiode(enDato(), enAnnenDato()))), listOf("Konkursstatus"),
            listOf(JuridiskEnhet("juridiskOrgnummer", "juridiskEnhetstype", 20))
        ),
        Arbeidsforholdstype.NORMALT,
        listOf(Arbeidsavtale(Periode(enDato(), enAnnenDato()), Periode(enDato(), enAnnenDato()), "yrkeskode", Skipsregister.NIS, 100.toDouble(), 37.5)),
        permisjonPermittering = emptyList()
    )
}

private fun personhistorikk(): Personhistorikk {
    return Personhistorikk(
        statsborgerskap = listOf(Statsborgerskap("NOR", enDato(), enAnnenDato())),
        bostedsadresser = listOf(Adresse("NOR", enDato(), enAnnenDato())),
        sivilstand = listOf(Sivilstand(Sivilstandstype.GIFT, enDato(), enAnnenDato(), ektefelleFnr())),
        familierelasjoner = listOf(Familierelasjon(barnFnr(), Familierelasjonsrolle.BARN, Familierelasjonsrolle.FAR, folkeregistermetadata())),
        kontaktadresser = listOf(Adresse("NOR", enDato(), enAnnenDato())),
        oppholdsadresser = listOf(Adresse("NOR", enDato(), enAnnenDato())),
        doedsfall = emptyList()
    )
}

private fun personhistorikkForBarn(): PersonhistorikkBarn {
    return PersonhistorikkBarn(
        ident = barnFnr(),
        bostedsadresser = listOf(Adresse("NOR", enDato(), enAnnenDato())),
        kontaktadresser = listOf(Adresse("NOR", enDato(), enAnnenDato())),
        oppholdsadresser = listOf(Adresse("NOR", enDato(), enAnnenDato())),
        familierelasjoner = listOf(Familierelasjon(brukerFnr(), Familierelasjonsrolle.FAR, Familierelasjonsrolle.BARN, folkeregistermetadata()))

    )
}

private fun personhistorikkEktefelle(): PersonhistorikkEktefelle {
    return PersonhistorikkEktefelle(
        ident = ektefelleFnr(),
        barn = listOf(barnFnr()),
        bostedsadresser = listOf(Adresse("NOR", enDato(), enAnnenDato())),
        kontaktadresser = listOf(Adresse("NOR", enDato(), enAnnenDato())),
        oppholdsadresser = listOf(Adresse("NOR", enDato(), enAnnenDato()))
    )
}

private fun folkeregistermetadata() = Folkeregistermetadata(etTidspunkt(), etTidspunkt(), etTidspunkt())
private fun brukerFnr() = "15076500565"
private fun ektefelleFnr() = "0101197512345"
private fun barnFnr() = "0101201012345"
private fun enDato() = LocalDate.of(1975, 10, 10)
private fun enAnnenDato() = LocalDate.of(2020, 8, 1)
private fun etTidspunkt() = LocalDateTime.of(2020, 6, 20, 10, 0)
