package no.nav.medlemskap.clients.pdl.mapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.domene.Personhistorikk
import no.nav.medlemskap.services.pdl.mapper.PdlMapper.mapTilPersonHistorikkTilBruker
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class PdlMapperTest {

    private val javaTimeModule = JavaTimeModule()

    @Test
    fun `PDLdata mappes om til riktige statsborgerskap `() {

        val mappetHistorikkTilBruker = pdlData()
        val mappetStatsborgerskapUtland = mappetHistorikkTilBruker.statsborgerskap[0]
        val mappetStatsborgerskapNOR = mappetHistorikkTilBruker.statsborgerskap[1]
        val mappetBostedsadresse = mappetHistorikkTilBruker.bostedsadresser[0]
        val mappetBostedsadresse2 = mappetHistorikkTilBruker.bostedsadresser[1]
        val mappetOppholdssadresse = mappetHistorikkTilBruker.oppholdsadresser[0]
        val mappetOppholdsadresse2 = mappetHistorikkTilBruker.oppholdsadresser[1]
        val mappetKontaktadresse = mappetHistorikkTilBruker.kontaktadresser[0]
        val familierelasjonerMappet = mappetHistorikkTilBruker.familierelasjoner.get(0)
        val sivilstandMappet = mappetHistorikkTilBruker.sivilstand[0]

        Assertions.assertEquals(2, mappetHistorikkTilBruker.statsborgerskap.size)
        Assertions.assertEquals(1, mappetHistorikkTilBruker.familierelasjoner.size)
        Assertions.assertEquals(2, mappetHistorikkTilBruker.bostedsadresser.size)
        Assertions.assertEquals(1, mappetHistorikkTilBruker.kontaktadresser.size)
        Assertions.assertEquals(2, mappetHistorikkTilBruker.oppholdsadresser.size)

        Assertions.assertEquals("SWE", mappetStatsborgerskapUtland.landkode)
        Assertions.assertEquals(LocalDate.of(1976, 7, 15), mappetStatsborgerskapUtland.fom)
        Assertions.assertEquals(LocalDate.of(1989, 12, 31), mappetStatsborgerskapUtland.tom)

        Assertions.assertEquals("NOR", mappetStatsborgerskapNOR.landkode)
        Assertions.assertEquals(LocalDate.of(1990, 1, 1), mappetStatsborgerskapNOR.fom)
        Assertions.assertEquals(null, mappetStatsborgerskapNOR.tom)

        Assertions.assertEquals("NOR", mappetBostedsadresse.landkode)
        Assertions.assertEquals(LocalDate.of(1990, 1, 1), mappetBostedsadresse.fom)
        Assertions.assertEquals(LocalDate.of(1992, 1, 1), mappetBostedsadresse.tom)

        Assertions.assertEquals("NOR", mappetBostedsadresse.landkode)
        Assertions.assertEquals(LocalDate.of(1992, 1, 2), mappetBostedsadresse2.fom)
        Assertions.assertEquals(null, mappetBostedsadresse2.tom)

        Assertions.assertEquals("BARN", familierelasjonerMappet.relatertPersonsRolle.name)
        Assertions.assertEquals("FAR", familierelasjonerMappet.minRolleForPerson?.name)
        Assertions.assertEquals("09069534888", familierelasjonerMappet.relatertPersonsIdent)

        Assertions.assertEquals(LocalDateTime.of(2020, 6, 20, 10, 1, 1), familierelasjonerMappet.folkeregistermetadata?.ajourholdstidspunkt)
        Assertions.assertEquals(LocalDateTime.of(2019, 10, 10, 10, 1, 1), familierelasjonerMappet.folkeregistermetadata?.gyldighetstidspunkt)
        Assertions.assertEquals(null, familierelasjonerMappet.folkeregistermetadata?.opphoerstidspunkt)

        Assertions.assertEquals("SWE", mappetOppholdssadresse.landkode)
        Assertions.assertEquals(LocalDate.of(1989, 12, 31), mappetOppholdssadresse.fom)
        Assertions.assertEquals(LocalDate.of(1992, 1, 1), mappetOppholdssadresse.tom)

        Assertions.assertEquals("NOR", mappetOppholdsadresse2.landkode)
        Assertions.assertEquals(LocalDate.of(1992, 12, 31), mappetOppholdsadresse2.fom)
        Assertions.assertEquals(null, mappetOppholdsadresse2.tom)

        Assertions.assertEquals("BEL", mappetKontaktadresse.landkode)
        Assertions.assertEquals(LocalDate.of(1989, 12, 31), mappetKontaktadresse.fom)
        Assertions.assertEquals(null, mappetKontaktadresse.tom)

        Assertions.assertEquals("GIFT", sivilstandMappet.type.name)
        Assertions.assertEquals("10108000398", sivilstandMappet.relatertVedSivilstand)
        Assertions.assertEquals(LocalDate.of(1990, 1, 1), sivilstandMappet.gyldigFraOgMed)
        Assertions.assertEquals(null, sivilstandMappet.gyldigTilOgMed)
        Assertions.assertEquals(null, sivilstandMappet.folkeregistermetadata?.ajourholdstidspunkt)
        Assertions.assertEquals(LocalDateTime.of(1990, 10, 10, 10, 1, 1), sivilstandMappet.folkeregistermetadata?.gyldighetstidspunkt)
        Assertions.assertEquals(null, sivilstandMappet.folkeregistermetadata?.opphoerstidspunkt)
    }

    fun pdlData(): Personhistorikk {
        val pdlData = objectMapper.readValue<HentPerson.Person>(pdlDataJson)
        return mapTilPersonHistorikkTilBruker(pdlData)
    }

    val pdlDataJson =
        """
{
            "familierelasjoner": [
                {
                   "relatertPersonsIdent" : "09069534888", 
                   "relatertPersonsRolle" : "BARN",
                   "minRolleForPerson": "FAR", 
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
                "relatertVedSivilstand": "10108000398",
                "folkeregistermetadata": {
                        "gyldighetstidspunkt": "1990-10-10T10:01:01"
                 }
               }
            ],
            "bostedsadresse": [ {
                "angittFlyttedato": "1992-01-01",
                "vegadresse": {
                    "postnummer": "8072"
                    },
                "matrikkeladresse": {
                    "postnummer": "8072"
                    },
                "ukjentBosted": {
                    "bostedskommune": "Bodo kommune"
                    },
                "folkeregistermetadata": {
                        "gyldighetstidspunkt": "1990-01-01T10:01:01",
                        "opphoerstidspunkt": "1992-01-01T10:01:01"
                    }
              },
              {
                "angittFlyttedato": "1992-01-02",
                "vegadresse": {
                  "postnummer": "8072"   
                },
                  "matrikkeladresse": {
                      "postnummer": "8072"
                },
                "ukjentBosted":{
                      "bostedskommune": "Bodo kommune"
                },
                "folkeregistermetadata": {
                        "gyldighetstidspunkt": "1992-01-02T10:01:01"
                }
              }
            ], 
            "kontaktadresse": [
                {
                    "gyldigFraOgMed" : "1989-12-31T10:01:01",
                    "utenlandskAdresse": {
                        "landkode": "BEL"
                    },
                    "folkeregistermetadata": {
                        "ajourholdstidspunkt": "2010-09-20T10:01:01"
                    }
                }
            ], 
            "oppholdsadresse": [
                 {
                    "gyldigFraOgMed" : "1989-12-31T10:01:01",
                    "utenlandskAdresse": {
                        "landkode": "SWE"
                    },
                    "folkeregistermetadata": {
                        "ajourholdstidspunkt": "1989-12-31T10:01:01",
                        "opphoerstidspunkt": "1992-01-01T10:01:01"
                    }
                 },
                 {
                    "gyldigFraOgMed" : "1992-12-31T10:01:01",
                    "folkeregistermetadata": {
                        "ajourholdstidspunkt": "1991-08-01T10:01:01"
                    }
                 }
            ],
            "doedsfall":[], 
            "folkeregisterpersonstatus": [
              {
                "status": "bosatt",
                "forenkletStatus": "bosattEtterFolkeregisterloven",
                "folkeregistermetadata": {
                  "ajourholdstidspunkt": "2020-09-30T16:17:23",
                  "gyldighetstidspunkt": "2020-09-30T16:17:23",
                  "opphoerstidspunkt": null,
                  "kilde": "Dolly",
                  "aarsak": null,
                  "sekvens": null
                },
                "metadata": {
                  "opplysningsId": "cc2650f4-5acc-4a53-9a74-223f21c82478",
                  "master": "Freg",
                  "endringer": [
                    {
                      "type": "OPPRETT",
                      "registrert": "2020-09-30T16:17:23",
                      "registrertAv": "Folkeregisteret",
                      "systemkilde": "FREG",
                      "kilde": "Dolly"
                    }
                  ],
                  "historisk": false
                }
              }
            ]
        }
        """.trimIndent()
}
