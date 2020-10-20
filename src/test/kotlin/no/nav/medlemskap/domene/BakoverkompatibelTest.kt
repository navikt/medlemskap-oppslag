package no.nav.medlemskap.domene

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter
import io.ktor.server.engine.*
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.config.ObjectMapperConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.http.Header
import io.restassured.response.ResponseBodyExtractionOptions
import io.restassured.specification.RequestSpecification
import kotlinx.coroutines.runBlocking
import no.nav.medlemskap.ApplicationState
import no.nav.medlemskap.clients.Services
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.config.AzureAdOpenIdConfiguration
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.createHttpServer
import no.nav.medlemskap.domene.barn.DataOmBarn
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

class BakoverkompatibelTest {
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
    }

    @Test
    fun testBakoverkompatibilitet() {

        // Fnr er Roland Gundersen fra https://www.nhn.no/media/2606/testaktoerer-v46.pdf
        val faktiskResponse = given()
            .body(input)
            .header(Header("Content-Type", "application/json"))
            .post("/")
            .then()
            .statusCode(200)
            .extract().asString()

        JSONAssert.assertEquals(
            forventetResponse, faktiskResponse,
            CustomComparator(
                JSONCompareMode.STRICT,
                Customization("tidspunkt") { _, _ -> true }
            )
        )
    }

    @Test
    fun testFeilInput() {

        // Fnr er Roland Gundersen fra https://www.nhn.no/media/2606/testaktoerer-v46.pdf
        val faktiskResponse = given()
            .body(ugyldigInput)
            .header(Header("Content-Type", "application/json"))
            .post("/")
            .then()
            .statusCode(400)
            .extract().asString().trimIndent()

        val forventetErrorResponse =
            """"{
            "url" : "/",
            "message" : "Instantiation of [simple type, class no.nav.medlemskap.domene.Request] value failed for JSON property brukerinput due to missing (therefore NULL) value for creator parameter brukerinput which is a non-nullable type\n at [Source: (InputStreamReader); line: 10, column: 1] (through reference chain: no.nav.medlemskap.domene.Request[\"brukerinput\"])",
            "cause" : "com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException: Instantiation of [simple type, class no.nav.medlemskap.domene.Request] value failed for JSON property brukerinput due to missing (therefore NULL) value for creator parameter brukerinput which is a non-nullable type\n at [Source: (InputStreamReader); line: 10, column: 1] (through reference chain: no.nav.medlemskap.domene.Request[\"brukerinput\"])",
            "code" : {
            "value" : 400,
            "description" : "Bad Request"
        },
            "callId" : { }
        }
            """.trimIndent()

/*
        JSONAssert.assertEquals(
            forventetErrorResponse, faktiskResponse,
            CustomComparator(
                JSONCompareMode.STRICT
            )
        )

 */
    }

    @Test
    fun testKontrakt() {
        val validationFilter = OpenApiValidationFilter("src/main/resources/lovme.yaml")

        val faktiskResponse = given()
            .filter(validationFilter)
            .body(input)
            .header(Header("Content-Type", "application/json"))
            .post("/")
            .then()
            .statusCode(200)
    }
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

private fun arbeidsforhold(): Arbeidsforhold {

    val juridiskEnhetstypeMap = HashMap<String, String?>()
    juridiskEnhetstypeMap["juridiskOrgnummer"] = "juridiskEnhetstype"
    return Arbeidsforhold(
        Periode(enDato(), enAnnenDato()),
        listOf(Utenlandsopphold("SWE", Periode(enDato(), enAnnenDato()), YearMonth.of(2010, 1))),
        OpplysningspliktigArbeidsgiverType.Organisasjon,
        Arbeidsgiver("type", "organisasjonsnummer", listOf(Ansatte(10, Bruksperiode(enDato(), enAnnenDato()), Gyldighetsperiode(enDato(), enAnnenDato()))), listOf("Konkursstatus"), juridiskEnhetstypeMap),
        Arbeidsforholdstype.NORMALT,
        listOf(Arbeidsavtale(Periode(enDato(), enAnnenDato()), Periode(enDato(), enAnnenDato()), "yrkeskode", Skipsregister.NIS, 100.toDouble()))
    )
}

private fun personhistorikk(): Personhistorikk {
    return Personhistorikk(
        statsborgerskap = listOf(Statsborgerskap("NOR", enDato(), enAnnenDato())),
        bostedsadresser = listOf(Adresse("NOR", enDato(), enAnnenDato())),
        sivilstand = listOf(Sivilstand(Sivilstandstype.GIFT, enDato(), enAnnenDato(), ektefelleFnr(), folkeregistermetadata())),
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
private fun enAdresse() = "Adresse"

private val input =
    """
        {
            "fnr": "15076500565",
            "periode": {
                "fom": "2019-01-01",
                "tom": "2019-12-31"
            },
            "brukerinput": {
                "arbeidUtenforNorge": false
            }
        }
    """.trimIndent()

private val ugyldigInput =
    """
        {
            "fnr": "15076500565",
            "periode": {
                "fom": "2019-01-01",
                "tom": "2019-12-31"
            },
            "ukjent": {
                "arbeidUtenforNorge": false
            }
        }
    """.trimIndent()

private val forventetResponse =
    """
    {
      "tidspunkt" : "2020-08-14T17:43:18.273719",
      "versjonTjeneste" : "",
      "versjonRegler" : "v1",
      "datagrunnlag" : {
        "periode" : {
          "fom" : "2019-01-01",
          "tom" : "2019-12-31"
        },
        "brukerinput" : {
          "arbeidUtenforNorge" : false
        },
        "pdlpersonhistorikk" : {
          "statsborgerskap" : [ {
            "landkode" : "NOR",
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          } ],
          "bostedsadresser" : [ {
            "landkode" : "NOR",
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          } ],
          "kontaktadresser" : [ {
            "landkode" : "NOR", 
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          }],
          "oppholdsadresser": [{
             "landkode" : "NOR", 
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          }],
          "doedsfall": [], 
          "sivilstand" : [ {
            "type" : "GIFT",
            "gyldigFraOgMed" : "1975-10-10",
            "gyldigTilOgMed" : "2020-08-01",
            "relatertVedSivilstand" : "0101197512345",
            "folkeregistermetadata" : {
              "ajourholdstidspunkt" : "2020-06-20T10:00:00",
              "gyldighetstidspunkt" : "2020-06-20T10:00:00",
              "opphoerstidspunkt" : "2020-06-20T10:00:00"
            }
          } ],
          "familierelasjoner" : [ {
            "relatertPersonsIdent" : "0101201012345",
            "relatertPersonsRolle" : "BARN",
            "minRolleForPerson" : "FAR",
            "folkeregistermetadata" : {
              "ajourholdstidspunkt" : "2020-06-20T10:00:00",
              "gyldighetstidspunkt" : "2020-06-20T10:00:00",
              "opphoerstidspunkt" : "2020-06-20T10:00:00"
            }
          } ]
        },
        "dataOmEktefelle": {
           "personhistorikkEktefelle": {
                    "ident": "0101197512345",
                    "barn": ["0101201012345"], 
                    "bostedsadresser" : [ {
                         "landkode" : "NOR",
                         "fom" : "1975-10-10",
                         "tom" : "2020-08-01"
                    } ],
                    "kontaktadresser" : [ {
                          "landkode" : "NOR", 
                          "fom" : "1975-10-10",
                          "tom" : "2020-08-01"
                    }],
                   "oppholdsadresser": [{
                        "landkode" : "NOR", 
                        "fom" : "1975-10-10",
                        "tom" : "2020-08-01"
                    }]
           }, 
            "arbeidsforholdEktefelle" : [ { 
                     "periode" : {
                       "fom" : "1975-10-10",
                       "tom" : "2020-08-01"
                     },
                     "utenlandsopphold" : [ {
                       "landkode" : "SWE",
                       "periode" : {
                         "fom" : "1975-10-10",
                         "tom" : "2020-08-01"
                       },
                       "rapporteringsperiode" : "2010-01"
                     } ],
                     "arbeidsgivertype" : "Organisasjon",
                     "arbeidsgiver" : {
                       "type" : "type",
                       "organisasjonsnummer" : "organisasjonsnummer",
                       "ansatte" : [ {
                         "antall" : 10,
                         "bruksperiode" : {
                           "fom" : "1975-10-10",
                           "tom" : "2020-08-01"
                         },
                         "gyldighetsperiode" : {
                           "fom" : "1975-10-10",
                           "tom" : "2020-08-01"
                         }
                       } ],
                       "konkursStatus" : [ "Konkursstatus" ],
                       "juridiskEnhetEnhetstypeMap" : {
                         "juridiskOrgnummer" : "juridiskEnhetstype"
                       }
                     },
                     "arbeidsforholdstype" : "NORMALT",
                     "arbeidsavtaler" : [ {
                       "periode" : {
                         "fom" : "1975-10-10",
                         "tom" : "2020-08-01"
                       },
                       "gyldighetsperiode" : {
                         "fom" : "1975-10-10",
                         "tom" : "2020-08-01"
                       },
                       "yrkeskode" : "yrkeskode",
                       "skipsregister" : "NIS",
                       "stillingsprosent" : 100.0
                     } ]
                   } ]
        },
        "dataOmBarn": [ {
           "personhistorikkBarn": {
                    "ident": "0101201012345",
                    "bostedsadresser" : [ {
                         "landkode" : "NOR",
                         "fom" : "1975-10-10",
                         "tom" : "2020-08-01"
                    } ],
                    "kontaktadresser" : [ {
                          "landkode" : "NOR", 
                          "fom" : "1975-10-10",
                          "tom" : "2020-08-01"
                    }],
                   "oppholdsadresser": [{
                        "landkode" : "NOR", 
                        "fom" : "1975-10-10",
                        "tom" : "2020-08-01"
                    }],
                    "familierelasjoner" : [ {
                         "relatertPersonsIdent" : "15076500565",
                         "relatertPersonsRolle" : "FAR",
                         "minRolleForPerson" : "BARN",
                         "folkeregistermetadata" : {
                           "ajourholdstidspunkt" : "2020-06-20T10:00:00",
                           "gyldighetstidspunkt" : "2020-06-20T10:00:00",
                           "opphoerstidspunkt" : "2020-06-20T10:00:00"
                         }
                   }]
            }
         }],
        "medlemskap" : [ {
          "dekning" : "dekning",
          "fraOgMed" : "1975-10-10",
          "tilOgMed" : "2020-08-01",
          "erMedlem" : true,
          "lovvalg" : "ENDL",
          "lovvalgsland" : "NOR",
          "periodeStatus" : "GYLD"
        } ],
        "arbeidsforhold" : [ {
          "periode" : {
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          },
          "utenlandsopphold" : [ {
            "landkode" : "SWE",
            "periode" : {
              "fom" : "1975-10-10",
              "tom" : "2020-08-01"
            },
            "rapporteringsperiode" : "2010-01"
          } ],
          "arbeidsgivertype" : "Organisasjon",
          "arbeidsgiver" : {
            "type" : "type",
            "organisasjonsnummer" : "organisasjonsnummer",
            "ansatte" : [ {
              "antall" : 10,
              "bruksperiode" : {
                "fom" : "1975-10-10",
                "tom" : "2020-08-01"
              },
              "gyldighetsperiode" : {
                "fom" : "1975-10-10",
                "tom" : "2020-08-01"
              }
            } ],
            "konkursStatus" : [ "Konkursstatus" ],
            "juridiskEnhetEnhetstypeMap" : {
              "juridiskOrgnummer" : "juridiskEnhetstype"
            }
          },
          "arbeidsforholdstype" : "NORMALT",
          "arbeidsavtaler" : [ {
            "periode" : {
              "fom" : "1975-10-10",
              "tom" : "2020-08-01"
            },
            "gyldighetsperiode" : {
              "fom" : "1975-10-10",
              "tom" : "2020-08-01"
            },
            "yrkeskode" : "yrkeskode",
            "skipsregister" : "NIS",
            "stillingsprosent" : 100.0
          } ]
        } ],
        "oppgaver" : [ {
          "aktivDato" : "1975-10-10",
          "prioritet" : "NORM",
          "status" : "AAPNET",
          "tema" : "Tema"
        } ],
        "dokument" : [ {
          "journalpostId" : "Id",
          "tittel" : "Tittel",
          "journalposttype" : "Posttype",
          "journalstatus" : "Status",
          "tema" : "Tema",
          "dokumenter" : [ {
            "dokumentId" : "Id",
            "tittel" : "Tittel"
          } ]
        } ],
        "ytelse" : "SYKEPENGER"
      },
       "resultat" : {
        "regelId" : "REGEL_MEDLEM_KONKLUSJON",
        "avklaring" : "Er bruker medlem?",
        "begrunnelse" : "Kan ikke konkludere med medlemskap",
        "svar" : "UAVKLART",
        "harDekning" : null,
        "dekning" : "",
        "delresultat" : [ {
          "regelId" : "REGEL_MEDL",
          "avklaring" : "Har bruker avklarte opplysninger i MEDL?",
          "begrunnelse" : "",
          "svar" : "UAVKLART",
          "harDekning" : null,
          "dekning" : "",
          "delresultat" : [ {
            "regelId" : "REGEL_OPPLYSNINGER",
            "avklaring" : "Finnes det registrerte opplysninger på bruker?",
            "begrunnelse" : "",
            "svar" : "JA",
            "harDekning" : null,
            "dekning" : "",
            "delresultat" : [ {
              "regelId" : "REGEL_A",
              "avklaring" : "Finnes det registrerte opplysninger i MEDL?",
              "begrunnelse" : "",
              "svar" : "JA",
              "harDekning" : null,
              "dekning" : "",
              "delresultat" : [ ]
            }, {
              "regelId" : "REGEL_C",
              "avklaring" : "Finnes det dokumenter i JOARK på medlemskapsområdet?",
              "begrunnelse" : "",
              "svar" : "NEI",
              "harDekning" : null,
              "dekning" : "",
              "delresultat" : [ ]
            }, {
              "regelId" : "REGEL_B",
              "avklaring" : "Finnes det åpne oppgaver i GOSYS på medlemskapsområdet?",
              "begrunnelse" : "",
              "svar" : "NEI",
              "harDekning" : null,
              "dekning" : "",
              "delresultat" : [ ]
            } ]
          }, {
            "regelId" : "REGEL_1_1",
            "avklaring" : "Er alle perioder siste 12 mnd avklart (endelig/gyldig)?",
            "begrunnelse" : "",
            "svar" : "JA",
            "harDekning" : null,
            "dekning" : "",
            "delresultat" : [ ]
          }, {
            "regelId" : "REGEL_1_2",
            "avklaring" : "Er det periode både med og uten medlemskap innenfor 12 mnd?",
            "begrunnelse" : "",
            "svar" : "NEI",
            "harDekning" : null,
            "dekning" : "",
            "delresultat" : [ ]
          }, {
            "regelId" : "REGEL_1_3",
            "avklaring" : "Er det en periode med medlemskap?",
            "begrunnelse" : "",
            "svar" : "JA",
            "harDekning" : null,
            "dekning" : "",
            "delresultat" : [ ]
          }, {
            "regelId" : "REGEL_1_4",
            "avklaring" : "Er hele perioden med medlemskap innenfor 12-måneders perioden?",
            "begrunnelse" : "",
            "svar" : "NEI",
            "harDekning" : null,
            "dekning" : "",
            "delresultat" : [ ]
          } ]
        }, {
          "regelId" : "REGEL_ARBEIDSFORHOLD",
          "avklaring" : "Er arbeidsforhold avklart?",
          "begrunnelse" : "Regelflyt konkluderer med UAVKLART",
          "svar" : "UAVKLART",
          "harDekning" : null,
          "dekning" : "",
          "delresultat" : [ {
            "regelId" : "REGEL_3",
            "avklaring" : "Har bruker hatt et sammenhengende arbeidsforhold i Aa-registeret de siste 12 månedene?",
            "begrunnelse" : "",
            "svar" : "JA",
            "harDekning" : null,
            "dekning" : "",
            "delresultat" : [ ]
          }, {
            "regelId" : "REGEL_4",
            "avklaring" : "Er foretaket registrert i foretaksregisteret?",
            "begrunnelse" : "",
            "svar" : "JA",
            "harDekning" : null,
            "dekning" : "",
            "delresultat" : [ ]
          }, {
            "regelId" : "REGEL_14",
            "avklaring" : "Er bruker ansatt i staten eller i en kommune?",
            "begrunnelse" : "",
            "svar" : "NEI",
            "harDekning" : null,
            "dekning" : "",
            "delresultat" : [ ]
          },{
            "regelId" : "REGEL_5",
            "avklaring" : "Har arbeidsgiver sin hovedaktivitet i Norge?",
            "begrunnelse" : "",
            "svar" : "JA",
            "harDekning" : null,
            "dekning" : "",
            "delresultat" : [ ]
          }, {
            "regelId" : "REGEL_6",
            "avklaring" : "Er foretaket aktivt?",
            "begrunnelse" : "Arbeidstaker har hatt arbeidsforhold til arbeidsgiver som har konkurs-status satt",
            "svar" : "NEI",
            "harDekning" : null,
            "dekning" : "",
            "delresultat" : [ ]
          } ]
        }, {
          "regelId" : "REGEL_BOSATT",
          "avklaring" : "Er det avklart om bruker bor i Norge?",
          "begrunnelse" : "Regelflyt konkluderer med JA",
          "svar" : "JA",
          "harDekning" : null,
          "dekning" : "",
          "delresultat" : [ {
            "regelId" : "REGEL_10",
            "avklaring" : "Er bruker folkeregistrert som bosatt i Norge og har vært det i 12 mnd?",
            "begrunnelse" : "",
            "svar" : "JA",
            "harDekning" : null,
            "dekning" : "",
            "delresultat" : [ ]
          } ]
        }, {
          "regelId" : "REGEL_STATSBORGERSKAP",
          "avklaring" : "Er statsborgerskap avklart?",
          "begrunnelse" : "Regelflyt konkluderer med JA",
          "svar" : "JA",
          "harDekning" : null,
          "dekning" : "",
          "delresultat" : [ {
            "regelId" : "REGEL_2",
            "avklaring" : "Er bruker omfattet av grunnforordningen (EØS)? Dvs er bruker statsborger i et EØS-land inkl. Norge?",
            "begrunnelse" : "",
            "svar" : "JA",
            "harDekning" : null,
            "dekning" : "",
            "delresultat" : [ ]
          }, {
            "regelId" : "REGEL_11",
            "avklaring" : "Er bruker norsk statsborger?",
            "begrunnelse" : "",
            "svar" : "JA",
            "harDekning" : null,
            "dekning" : "",
            "delresultat" : [ ]
          } ]
        }, {
          "regelId" : "REGEL_NORSK",
          "avklaring" : "Er regler for norsk borgere avklart?",
          "begrunnelse" : "Regelflyt konkluderer med JA",
          "svar" : "JA",
          "harDekning" : null,
          "dekning" : "",
          "delresultat" : [ {
            "regelId" : "REGEL_12",
            "avklaring" : "Har bruker vært i minst 25% stilling de siste 12 mnd?",
            "begrunnelse" : "",
            "svar" : "JA",
            "harDekning" : null,
            "dekning" : "",
            "delresultat" : [ ]
          } ]
        } ]
      }
    }
    """.trimIndent()
