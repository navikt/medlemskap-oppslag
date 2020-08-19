package no.nav.medlemskap.domene

import io.ktor.server.engine.ApplicationEngine
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
    fun testBakoverkompatibilitet() {

        //Fnr er Roland Gundersen fra https://www.nhn.no/media/2606/testaktoerer-v46.pdf
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

private val input = """
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

private val forventetResponse = """
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
        "personhistorikk" : {
          "statsborgerskap" : [ {
            "landkode" : "NOR",
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          } ],
          "personstatuser" : [ {
            "personstatus" : "BOSA",
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          } ],
          "bostedsadresser" : [ {
            "landkode" : "NOR",
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          } ],
          "postadresser" : [ {
            "landkode" : "NOR",
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          } ],
          "midlertidigAdresser" : [ {
            "landkode" : "NOR",
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          } ],
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
        "pdlpersonhistorikk" : {
          "statsborgerskap" : [ {
            "landkode" : "NOR",
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          } ],
          "personstatuser" : [ {
            "personstatus" : "BOSA",
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          } ],
          "bostedsadresser" : [ {
            "landkode" : "NOR",
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          } ],
          "postadresser" : [ {
            "landkode" : "NOR",
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          } ],
          "midlertidigAdresser" : [ {
            "landkode" : "NOR",
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          } ],
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
                    "barn": [
                        {
                            "ident": "0101201012345"
                        }
                    ]
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
                       "identifikator" : "identifikator",
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
                       "konkursStatus" : [ "Konkursstatus" ]
                     },
                     "arbeidsfolholdstype" : "NORMALT",
                     "arbeidsavtaler" : [ {
                       "periode" : {
                         "fom" : "1975-10-10",
                         "tom" : "2020-08-01"
                       },
                       "yrkeskode" : "yrkeskode",
                       "skipsregister" : "NIS",
                       "stillingsprosent" : 100.0
                     } ]
                   } ]
        },
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
            "identifikator" : "identifikator",
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
            "konkursStatus" : [ "Konkursstatus" ]
          },
          "arbeidsfolholdstype" : "NORMALT",
          "arbeidsavtaler" : [ {
            "periode" : {
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
        "ytelse" : "SYKEPENGER",
        "personHistorikkRelatertePersoner" : [ {
          "ident" : "0101197512345",
          "personstatuser" : [ {
            "personstatus" : "BOSA",
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          } ],
          "bostedsadresser" : [ {
            "landkode" : "NOR",
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          } ],
          "postadresser" : [ {
            "landkode" : "NOR",
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          } ],
          "midlertidigAdresser" : [ {
            "landkode" : "NOR",
            "fom" : "1975-10-10",
            "tom" : "2020-08-01"
          } ]
        } ]
      },
      "resultat" : {
        "regelId" : "REGEL_MEDLEM_KONKLUSJON",
        "avklaring" : "Er bruker medlem?",
        "begrunnelse" : "Kan ikke konkludere med medlemskap",
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
      }
    }
""".trimIndent()
