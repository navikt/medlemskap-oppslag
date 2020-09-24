package no.nav.medlemskap.clients.ereg

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.services.ereg.mapOrganisasjonTilArbeidsgiver
import org.junit.Assert
import org.junit.jupiter.api.Test
import java.time.LocalDate

class EregMapperTest {

    @Test
    fun mapOrganisasjonTilArbeidsgiver() {

        val organisasjon = objectMapper.readValue<Organisasjon>(eregHentOrganisasjonResponse)

        val juridiskEnhetOrgnummerEnhetstype = mutableMapOf<String, String?>()
        juridiskEnhetOrgnummerEnhetstype["123456789"] = "AS"

        val mappedArbeidsgiverFraOrganisasjon = mapOrganisasjonTilArbeidsgiver(organisasjon, juridiskEnhetOrgnummerEnhetstype)

        Assert.assertEquals("BEDR", mappedArbeidsgiverFraOrganisasjon.type)
        Assert.assertEquals(1, mappedArbeidsgiverFraOrganisasjon.ansatte?.size)
        Assert.assertEquals(19, mappedArbeidsgiverFraOrganisasjon.ansatte?.first()?.antall)
        Assert.assertEquals(LocalDate.parse("2020-07-16"), mappedArbeidsgiverFraOrganisasjon.ansatte?.first()?.bruksperiode?.fom)
        Assert.assertNull(mappedArbeidsgiverFraOrganisasjon.ansatte?.first()?.bruksperiode?.tom)
        Assert.assertEquals(LocalDate.parse("2020-07-13"), mappedArbeidsgiverFraOrganisasjon.ansatte?.first()?.gyldighetsperiode?.fom)
        Assert.assertNull(mappedArbeidsgiverFraOrganisasjon.ansatte?.first()?.gyldighetsperiode?.tom)
        Assert.assertEquals("975016684", mappedArbeidsgiverFraOrganisasjon.identifikator)
        Assert.assertNull(mappedArbeidsgiverFraOrganisasjon.konkursStatus)
        Assert.assertEquals(1, mappedArbeidsgiverFraOrganisasjon.juridiskEnhetEnhetstypeMap?.keys?.size)
        Assert.assertEquals("123456789", mappedArbeidsgiverFraOrganisasjon.juridiskEnhetEnhetstypeMap?.keys?.first())
        Assert.assertEquals("AS", mappedArbeidsgiverFraOrganisasjon.juridiskEnhetEnhetstypeMap?.values?.first())
    }

    private val eregHentOrganisasjonResponse =
        """
        {
          "organisasjonsnummer": "975016684",
          "type": "Virksomhet",
          "navn": {
            "redigertnavn": "FROGNER BARNEHAGE",
            "navnelinje1": "FROGNER BARNEHAGE",
            "bruksperiode": {
              "fom": "2015-02-23T08:04:53.2"
            },
            "gyldighetsperiode": {
              "fom": "1998-04-21"
            }
          },
          "organisasjonDetaljer": {
            "registreringsdato": "1998-04-21T00:00:00",
            "enhetstyper": [
              {
                "enhetstype": "BEDR",
                "bruksperiode": {
                  "fom": "2014-05-21T15:21:17.549"
                },
                "gyldighetsperiode": {
                  "fom": "1998-04-21"
                }
              }
            ],
            "naeringer": [
              {
                "naeringskode": "88.911",
                "hjelpeenhet": false,
                "bruksperiode": {
                  "fom": "2014-05-22T00:58:27.229"
                },
                "gyldighetsperiode": {
                  "fom": "2003-01-13"
                }
              }
            ],
            "navn": [
              {
                "redigertnavn": "FROGNER BARNEHAGE",
                "navnelinje1": "FROGNER BARNEHAGE",
                "bruksperiode": {
                  "fom": "2015-02-23T08:04:53.2"
                },
                "gyldighetsperiode": {
                  "fom": "1998-04-21"
                }
              }
            ],
            "forretningsadresser": [
              {
                "type": "Forretningsadresse",
                "adresselinje1": "Professor Dahls gate 44",
                "postnummer": "0260",
                "landkode": "NO",
                "kommunenummer": "0301",
                "bruksperiode": {
                  "fom": "2015-02-23T10:38:34.403"
                },
                "gyldighetsperiode": {
                  "fom": "2010-08-12"
                }
              }
            ],
            "epostadresser": [
              {
                "adresse": "frogner@bfr.oslo.kommune.no",
                "bruksperiode": {
                  "fom": "2016-09-13T04:04:24.12"
                },
                "gyldighetsperiode": {
                  "fom": "2016-09-12"
                }
              }
            ],
            "sistEndret": "2020-07-13",
            "ansatte": [
              {
                "antall": 19,
                "bruksperiode": {
                  "fom": "2020-07-16T00:51:50.236"
                },
                "gyldighetsperiode": {
                  "fom": "2020-07-13"
                }
              }
            ]
          },
          "virksomhetDetaljer": {
            "enhetstype": "BEDR",
            "oppstartsdato": "1982-01-01",
            "eierskiftedato": "2002-06-30"
          },
          "bestaarAvOrganisasjonsledd": [
            {
              "organisasjonsledd": {
                "organisasjonsnummer": "874778702",
                "type": "Organisasjonsledd",
                "navn": {
                  "redigertnavn": "OSLO KOMMUNE BYDEL 5 FROGNER",
                  "navnelinje1": "OSLO KOMMUNE BYDEL 5 FROGNER",
                  "bruksperiode": {
                    "fom": "2015-02-23T08:04:53.2"
                  },
                  "gyldighetsperiode": {
                    "fom": "2004-02-06"
                  }
                },
                "organisasjonsleddOver": [
                  {
                    "organisasjonsledd": {
                      "organisasjonsnummer": "876819872",
                      "type": "Organisasjonsledd",
                      "navn": {
                        "redigertnavn": "OSLO KOMMUNE, BYRÅDSAVDELING FOR",
                        "navnelinje1": "OSLO KOMMUNE, BYRÅDSAVDELING FOR",
                        "navnelinje2": "HELSE, ELDRE OG INNBYGGERTJENESTER",
                        "bruksperiode": {
                          "fom": "2020-07-03T04:00:45.796"
                        },
                        "gyldighetsperiode": {
                          "fom": "2020-07-02"
                        }
                      },
                      "inngaarIJuridiskEnheter": [
                        {
                          "organisasjonsnummer": "958935420",
                          "navn": {
                            "redigertnavn": "OSLO KOMMUNE",
                            "navnelinje1": "OSLO KOMMUNE",
                            "bruksperiode": {
                              "fom": "2015-02-23T08:04:53.2"
                            },
                            "gyldighetsperiode": {
                              "fom": "1997-08-30"
                            }
                          },
                          "bruksperiode": {
                            "fom": "2014-05-23T15:24:51.336"
                          },
                          "gyldighetsperiode": {
                            "fom": "1997-10-29"
                          }
                        }
                      ]
                    },
                    "bruksperiode": {
                      "fom": "2014-05-23T15:22:19.065"
                    },
                    "gyldighetsperiode": {
                      "fom": "1998-08-19"
                    }
                  }
                ]
              },
              "bruksperiode": {
                "fom": "2014-05-23T17:30:16.985"
              },
              "gyldighetsperiode": {
                "fom": "2002-08-21"
              }
            }
          ]
        }
        """.trimIndent()
}
