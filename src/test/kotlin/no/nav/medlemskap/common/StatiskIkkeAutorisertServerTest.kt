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
import org.junit.Test
import org.junit.jupiter.api.BeforeAll
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class StatiskIkkeAutorisertServerTest {

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
                ))
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
                        port = 7071,
                        createDatagrunnlag = ::mockCreateDatagrunnlag
                )

                Runtime.getRuntime().addShutdownHook(Thread {
                    applicationState.initialized = false
                    applicationServer.stop(5000, 5000)
                })

                applicationServer.start()
                applicationState.initialized = true
                applicationState.running = true

                RestAssured.baseURI = "http://localhost"
                RestAssured.basePath = "/"
                RestAssured.port = 7071
                RestAssured.config = RestAssuredConfig.config().objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                        .jackson2ObjectMapperFactory { _, _ -> objectMapper })
            }
        }

    }

    @Test
    fun `Request uten body får 400 respons`() {
        val faktiskResponse = RestAssured.given()
                .body("")
                .header(Header("Content-Type", "application/json"))
                .post("/")
                .then()
                .statusCode(200)
                .extract().asString()

        println("Dette er responsen: $faktiskResponse")
    }


    suspend fun mockCreateDatagrunnlag(
            fnr: String,
            callId: String,
            periode: InputPeriode,
            brukerinput: Brukerinput,
            services: Services,
            clientId: String?,
            ytelseFraRequest: Ytelse?): Datagrunnlag = runBlocking {

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
                personHistorikkRelatertePersoner = listOf(personhistorikkRelatertPerson())
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
                familierelasjoner = listOf(Familierelasjon(barnFnr(), Familierelasjonsrolle.BARN, Familierelasjonsrolle.FAR, folkeregistermetadata()))
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


    private fun folkeregistermetadata() = Folkeregistermetadata(etTidspunkt(), etTidspunkt(), etTidspunkt())

    private fun ektefelleFnr() = "0101197512345"
    private fun barnFnr() = "0101201012345"
    private fun enDato() = LocalDate.of(1975, 10, 10)
    private fun enAnnenDato() = LocalDate.of(2020, 8, 1)
    private fun etTidspunkt() = LocalDateTime.of(2020, 6, 20, 10, 0)


}