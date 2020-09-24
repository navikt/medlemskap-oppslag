package no.nav.medlemskap.clients.pdl.mapper

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.services.pdl.mapper.PdlMapperBarn
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PdlBarnMapperTest {

    private val javaTimeModule = JavaTimeModule()

    @Test
    fun `PDLdata mappes om til riktige data om barn `() {

        val mappetHistorikkTilBarn = pdlData()
        val mappetBostedsadresse = mappetHistorikkTilBarn.bostedsadresser[0]
        val mappetBostedsadresse2 = mappetHistorikkTilBarn.bostedsadresser[1]
        val mappetOppholdssadresse = mappetHistorikkTilBarn.oppholdsadresser[0]
        val mappetOppholdsadresse2 = mappetHistorikkTilBarn.oppholdsadresser[1]
        val mappetKontaktadresse = mappetHistorikkTilBarn.kontaktadresser[0]
        val mappetFamilierelasjonerFar = mappetHistorikkTilBarn.familierelasjoner[0]
        val mappetFamilierelasjonerMor = mappetHistorikkTilBarn.familierelasjoner[1]

        Assertions.assertEquals(mappetHistorikkTilBarn.bostedsadresser.size, mappetHistorikkTilBarn.bostedsadresser.size)
        Assertions.assertEquals(mappetHistorikkTilBarn.oppholdsadresser.size, mappetHistorikkTilBarn.oppholdsadresser.size)
        Assertions.assertEquals(mappetHistorikkTilBarn.kontaktadresser.size, mappetHistorikkTilBarn.kontaktadresser.size)

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

        Assertions.assertEquals("FAR", mappetFamilierelasjonerFar?.relatertPersonsRolle?.name)
        Assertions.assertEquals("BARN", mappetFamilierelasjonerFar?.minRolleForPerson?.name)
        Assertions.assertEquals("15076500565", mappetFamilierelasjonerFar?.relatertPersonsIdent)

        Assertions.assertEquals("MOR", mappetFamilierelasjonerMor?.relatertPersonsRolle?.name)
        Assertions.assertEquals("BARN", mappetFamilierelasjonerMor?.minRolleForPerson?.name)
        Assertions.assertEquals("10108000398", mappetFamilierelasjonerMor?.relatertPersonsIdent)
    }

    fun pdlData(): PersonhistorikkBarn {
        val pdlDataBarn = objectMapper.readValue<HentPerson.Person>(pdlDataJson)
        return PdlMapperBarn.mapPersonhistorikkTilBarn("09069534888", pdlDataBarn)
    }

    val pdlDataJson =
        """
 {
            "familierelasjoner": [
                {
                   "relatertPersonsIdent" : "15076500565", 
                   "relatertPersonsRolle" : "FAR",
                   "minRolleForPerson": "BARN", 
                   "folkeregistermetadata": {
                        "ajourholdstidspunkt": "2020-06-20T10:01:01", 
                        "gyldighetstidspunkt": "2019-10-10T10:01:01"
                   }
                }, 
                {
                   "relatertPersonsIdent": "10108000398", 
                   "relatertPersonsRolle" : "MOR",
                   "minRolleForPerson": "BARN", 
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
            "sivilstand": [],
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
