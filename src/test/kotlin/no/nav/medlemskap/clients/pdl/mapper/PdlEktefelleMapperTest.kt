package no.nav.medlemskap.clients.pdl.mapper

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import no.nav.medlemskap.services.pdl.mapper.PdlMapperEktefelle
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PdlEktefelleMapperTest {

    private val javaTimeModule = JavaTimeModule()

    @Test
    fun `PDLdata mappes om til riktige data om ektefelle`() {

        val mappetHistorikkTilEktefelle = pdlData()
        val mappetBostedsadresse = mappetHistorikkTilEktefelle.bostedsadresser[0]
        val mappetBostedsadresse2 = mappetHistorikkTilEktefelle.bostedsadresser[1]
        val mappetOppholdssadresse = mappetHistorikkTilEktefelle.oppholdsadresser[0]
        val mappetOppholdsadresse2 = mappetHistorikkTilEktefelle.oppholdsadresser[1]
        val mappetKontaktadresse = mappetHistorikkTilEktefelle.kontaktadresser[0]
        val barnMappet = mappetHistorikkTilEktefelle.barn

        Assertions.assertEquals(mappetHistorikkTilEktefelle.bostedsadresser.size, mappetHistorikkTilEktefelle.bostedsadresser.size)
        Assertions.assertEquals(mappetHistorikkTilEktefelle.oppholdsadresser.size, mappetHistorikkTilEktefelle.oppholdsadresser.size)
        Assertions.assertEquals(mappetHistorikkTilEktefelle.kontaktadresser.size, mappetHistorikkTilEktefelle.kontaktadresser.size)

        Assertions.assertEquals("NOR", mappetBostedsadresse.landkode)
        Assertions.assertEquals(LocalDate.of(1990, 1, 1), mappetBostedsadresse.fom)
        Assertions.assertEquals(LocalDate.of(1992, 1, 1), mappetBostedsadresse.tom)
        Assertions.assertEquals("NOR", mappetBostedsadresse.landkode)
        Assertions.assertEquals(LocalDate.of(1992, 1, 2), mappetBostedsadresse2.fom)
        Assertions.assertEquals(null, mappetBostedsadresse2.tom)
        Assertions.assertEquals("SWE", mappetOppholdssadresse.landkode)

        Assertions.assertEquals(LocalDate.of(1989, 12, 31), mappetOppholdssadresse.fom)
        Assertions.assertEquals(LocalDate.of(1992, 1, 1), mappetOppholdssadresse.tom)
        Assertions.assertEquals("NOR", mappetOppholdsadresse2.landkode)
        Assertions.assertEquals(LocalDate.of(1992, 12, 31), mappetOppholdsadresse2.fom)
        Assertions.assertEquals(null, mappetOppholdsadresse2.tom)

        Assertions.assertEquals("BEL", mappetKontaktadresse.landkode)
        Assertions.assertEquals(LocalDate.of(1989, 12, 31), mappetKontaktadresse.fom)
        Assertions.assertEquals(null, mappetKontaktadresse.tom)

        Assertions.assertEquals(2, mappetHistorikkTilEktefelle.barn?.size)
        Assertions.assertEquals("09069534888", barnMappet?.get(0))
        Assertions.assertEquals("10079541651", barnMappet?.get(1))
    }

    fun pdlData(): PersonhistorikkEktefelle {
        val pdlDataEktefelle = objectMapper.readValue<HentPerson.Person>(pdlDataJson)
        return PdlMapperEktefelle.mapPersonhistorikkTilEktefelle("101197512345", pdlDataEktefelle)
    }

    val pdlDataJson =
        """
 {
            "familierelasjoner": [
                {
                   "relatertPersonsIdent" : "09069534888", 
                   "relatertPersonsRolle" : "BARN",
                   "minRolleForPerson": "MOR", 
                   "folkeregistermetadata": {
                        "ajourholdstidspunkt": "2020-06-20T10:01:01", 
                        "gyldighetstidspunkt": "2019-10-10T10:01:01"
                   }
                }, 
                {
                   "relatertPersonsIdent": "10079541651", 
                   "relatertPersonsRolle" : "BARN",
                   "minRolleForPerson": "MOR", 
                   "folkeregistermetadata": {
                        "ajourholdstidspunkt": "2020-06-20T10:01:01", 
                        "gyldighetstidspunkt": "2019-10-10T10:01:01"
                   }
                }, 
                 {
                   "relatertPersonsIdent": "10019448164", 
                   "relatertPersonsRolle" : "BARN",
                   "minRolleForPerson": "MOR", 
                   "folkeregistermetadata": {
                        "ajourholdstidspunkt": "2020-06-20T10:01:01", 
                        "gyldighetstidspunkt": "2019-10-10T10:01:01"
                   }
                 }, 
                 {
                   "relatertPersonsIdent": "10019400000", 
                   "relatertPersonsRolle" : "BARN",
                   "minRolleForPerson": "MOR", 
                   "folkeregistermetadata": {
                        "ajourholdstidspunkt": "2020-06-20T10:01:01", 
                        "gyldighetstidspunkt": "2019-10-10T10:01:01"
                   }
                 }
            ], 
            "statsborgerskap": [
                {
                    "land": "SWE", 
                    "gyldigFraOgMed": "1976-07-15",
                    "gyldigTilOgMed": "1989-12-31"
                }, 
                {
                    "land": "NOR", 
                    "gyldigFraOgMed": "1990-01-01"
                }
            ], 
            "sivilstand": [
               {
                "type": "GIFT", 
                "gyldigFraOgMed": "1990-01-01",
                "relatertVedSivilstand": "15076500565",
                "folkeregistermetadata": {
                        "gyldighetstidspunkt": "1990-10-10T10:01:01"
                 }
               }
            ],
            "bostedsadresse": [
               {
                "angittFlyttedato": "1992-01-01",
                "coAdressenavn": "Donald Duck",
                "vegadresse": 
                    {
                    "postnummer": "8072"
                    },
                "matrikkeladresse": 
                    {
                    "postnummer": "8072"
                    },
                "ukjentBosted": 
                    {
                    "bostedskommune": "Bodo kommune"
                    },
                "folkeregistermetadata":
                    {
                        "gyldighetstidspunkt": "1990-01-01T10:01:01",
                        "opphoerstidspunkt": "1992-01-01T10:01:01"
                    },
                 "metadata": {
                  "opplysningsId": "1234",
                  "master": "FREG",
                  "endringer": [
                    {
                        "type": "OPPRETT",
                        "registrert": "1989-12-31T10:01:01", 
                        "registrertAv": "Z990200", 
                        "systemkilde": "FREG", 
                        "kilde": "NAV"
                    }
                  ], 
                  "historisk": "true"
                  }
              },
              {
                "angittFlyttedato": "1992-01-02",
                "coAdressenavn": "Donald Duck",
                "vegadresse": 
                {
                  "postnummer": "8072"   
                },
                  "matrikkeladresse": 
                     {
                      "postnummer": "8072"
                     },
                "ukjentBosted": {
                      "bostedskommune": "Bodo kommune"
                },
                "folkeregistermetadata": {
                        "gyldighetstidspunkt": "1992-01-02T10:01:01"
                 },
                 "metadata": {
                  "opplysningsId": "1234",
                  "master": "FREG",
                  "endringer": [
                    {
                        "type": "OPPRETT",
                        "registrert": "1989-12-31T10:01:01", 
                        "registrertAv": "Z990200", 
                        "systemkilde": "FREG", 
                        "kilde": "NAV"
                    }
                  ], 
                  "historisk": "true"
              }
              }
            ], 
            "kontaktadresse": [
                {
                    "gyldigFraOgMed" : "1989-12-31T10:01:01",
                    "coAdressenavn": "Donald Duck", 
                    "type": "Utland", 
                    "utenlandskAdresse": {
                        "landkode": "BEL"
                    },
                    "folkeregistermetadata": {
                        "ajourholdstidspunkt": "2010-09-20T10:01:01"
                    }, 
                    "metadata": {
                         "opplysningsId": "1234",
                         "master": "FREG",
                         "endringer": [
                             {
                                "type": "OPPRETT",
                                "registrert": "1989-12-31T10:01:01", 
                                "registrertAv": "Z990200", 
                                "systemkilde": "FREG", 
                                "kilde": "NAV"
                             }
                         ], 
                         "historisk": "true"
                    }
                }
            ], 
            "oppholdsadresse": [
                {
                    "gyldigFraOgMed" : "1989-12-31T10:01:01", 
                    "coAdressenavn": "Donald duck",
                    "utenlandskAdresse": {
                        "landkode": "SWE"
                    },
                    "metadata": {
                         "opplysningsId": "1234",
                         "master": "FREG",
                         "endringer": [
                             {
                                "type": "OPPRETT",
                                "registrert": "1989-12-31T10:01:01", 
                                "registrertAv": "Z990200", 
                                "systemkilde": "FREG", 
                                "kilde": "NAV"
                             }
                         ], 
                         "historisk": "true"
                    }, 
                    "folkeregistermetadata": {
                        "ajourholdstidspunkt": "1989-12-31T10:01:01",
                        "opphoerstidspunkt": "1992-01-01T10:01:01"
                    }
                },
                 {
                    "gyldigFraOgMed" : "1992-12-31T10:01:01", 
                    "coAdressenavn": "Donald duck",
                    "metadata": {
                         "opplysningsId": "1234",
                         "master": "FREG",
                         "endringer": [
                             {
                                "type": "OPPRETT",
                                "registrert": "1989-12-31T10:01:01", 
                                "registrertAv": "Z990200", 
                                "systemkilde": "FREG", 
                                "kilde": "NAV"
                             }
                         ], 
                         "historisk": "true"
                    }, 
                    "folkeregistermetadata": {
                        "ajourholdstidspunkt": "1991-08-01T10:01:01"
                    }
                }
            ]
        }
        """.trimIndent()
}
