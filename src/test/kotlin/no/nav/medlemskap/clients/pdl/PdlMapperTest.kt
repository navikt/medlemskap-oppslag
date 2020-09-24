package no.nav.medlemskap.clients.pdl
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.services.pdl.mapper.PdlMapper.mapTilPersonHistorikkTilBruker
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PdlMapperTest {

    private val javaTimeModule = JavaTimeModule()

    @Test
    fun `PDLdata mappes om til riktige statsborgerskap `() {

        val mappetHistorikkTilBruker = pdlData()
        val mappetStatsborgerskapUtland = mappetHistorikkTilBruker.statsborgerskap[0]
        val mappetStatsborgerskapNOR = mappetHistorikkTilBruker.statsborgerskap[1]

        Assertions.assertEquals(mappetHistorikkTilBruker.statsborgerskap.size, mappetHistorikkTilBruker.statsborgerskap.size)
        Assertions.assertEquals("SWE", mappetStatsborgerskapUtland.landkode)
        Assertions.assertEquals(LocalDate.of(1976, 7, 15), mappetStatsborgerskapUtland.fom)
        Assertions.assertEquals(LocalDate.of(1989, 12, 31), mappetStatsborgerskapUtland.tom)

        Assertions.assertEquals("NOR", mappetStatsborgerskapNOR.landkode)
        Assertions.assertEquals(LocalDate.of(1990, 1, 1), mappetStatsborgerskapNOR.fom)
        Assertions.assertEquals(null, mappetStatsborgerskapNOR.tom)
    }

    @Test
    fun `PDLdata mappes om til riktige bostedsadresser`() {
        val mappetHistorikkTilBruker = pdlData()
        val mappetBostedsadresse = mappetHistorikkTilBruker.bostedsadresser[0]
        val mappetBostedsadresse2 = mappetHistorikkTilBruker.bostedsadresser[1]

        Assertions.assertEquals(mappetHistorikkTilBruker.bostedsadresser.size, mappetHistorikkTilBruker.bostedsadresser.size)

        Assertions.assertEquals("NOR", mappetBostedsadresse.landkode)
        Assertions.assertEquals(LocalDate.of(1990, 1, 1), mappetBostedsadresse.fom)
        Assertions.assertEquals(LocalDate.of(1992, 1, 1), mappetBostedsadresse.tom)

        Assertions.assertEquals("NOR", mappetBostedsadresse.landkode)
        Assertions.assertEquals(LocalDate.of(1992, 1, 2), mappetBostedsadresse2.fom)
        Assertions.assertEquals(null, mappetBostedsadresse2.tom)
    }

    @Test
    fun `PDLdata mappes om til riktige oppholdsadresser`() {
        val mappetHistorikkTilBruker = pdlData()
        val mappetOppholdssadresse = mappetHistorikkTilBruker.oppholdsadresser[0]
        val mappetOppholdsadresse2 = mappetHistorikkTilBruker.oppholdsadresser[1]

        Assertions.assertEquals(mappetHistorikkTilBruker.oppholdsadresser.size, mappetHistorikkTilBruker.oppholdsadresser.size)

        Assertions.assertEquals("SWE", mappetOppholdssadresse.landkode)
        Assertions.assertEquals(LocalDate.of(1989, 12, 31), mappetOppholdssadresse.fom)
        Assertions.assertEquals(LocalDate.of(1992, 1, 1), mappetOppholdssadresse.tom)

        Assertions.assertEquals("NOR", mappetOppholdsadresse2.landkode)
        Assertions.assertEquals(LocalDate.of(1992, 12, 31), mappetOppholdsadresse2.fom)
        Assertions.assertEquals(null, mappetOppholdsadresse2.tom)
    }

    @Test
    fun `PDLdata mappes om til riktige kontaktadresser`() {
        val mappetHistorikkTilBruker = pdlData()
        val mappetKontaktadresse = mappetHistorikkTilBruker.kontaktadresser[0]

        Assertions.assertEquals(mappetHistorikkTilBruker.kontaktadresser.size, mappetHistorikkTilBruker.kontaktadresser.size)
        Assertions.assertEquals("BEL", mappetKontaktadresse.landkode)
        Assertions.assertEquals(LocalDate.of(1989, 12, 31), mappetKontaktadresse.fom)
        Assertions.assertEquals(null, mappetKontaktadresse.tom)
    }

    @Test
    fun `PDLdata mappes om til riktig sivilstand`() {
        val mappetHistorikkTilBruker = pdlData()
        val sivilstandMappet = mappetHistorikkTilBruker.sivilstand[0]

        Assertions.assertEquals(mappetHistorikkTilBruker.kontaktadresser.size, mappetHistorikkTilBruker.kontaktadresser.size)
        Assertions.assertEquals("GIFT", sivilstandMappet.type.name)
        Assertions.assertEquals("10108000398", sivilstandMappet.relatertVedSivilstand)
        Assertions.assertEquals(LocalDate.of(1990, 1, 1), sivilstandMappet.gyldigFraOgMed)
        Assertions.assertEquals(null, sivilstandMappet.gyldigTilOgMed)
        Assertions.assertEquals(null, sivilstandMappet.folkeregistermetadata?.ajourholdstidspunkt)
        Assertions.assertEquals(LocalDateTime.of(1990, 10, 10, 10, 1, 1), sivilstandMappet.folkeregistermetadata?.gyldighetstidspunkt)
        Assertions.assertEquals(null, sivilstandMappet.folkeregistermetadata?.opphoerstidspunkt)
    }

    @Test
    fun `PDLdata mappes om til riktig familierelasjoner`() {
        val mappetHistorikkTilBruker = pdlData()
        val familierelasjonerMappet = mappetHistorikkTilBruker.familierelasjoner.get(0)

        Assertions.assertEquals(mappetHistorikkTilBruker.familierelasjoner.size, mappetHistorikkTilBruker.familierelasjoner.size)

        Assertions.assertEquals("BARN", familierelasjonerMappet.relatertPersonsRolle.name)
        Assertions.assertEquals("FAR", familierelasjonerMappet.minRolleForPerson?.name)
        Assertions.assertEquals("09069534888", familierelasjonerMappet.relatertPersonsIdent)

        Assertions.assertEquals(LocalDateTime.of(2020, 6, 20, 10, 1, 1), familierelasjonerMappet.folkeregistermetadata?.ajourholdstidspunkt)
        Assertions.assertEquals(LocalDateTime.of(2019, 10, 10, 10, 1, 1), familierelasjonerMappet.folkeregistermetadata?.gyldighetstidspunkt)
        Assertions.assertEquals(null, familierelasjonerMappet.folkeregistermetadata?.opphoerstidspunkt)
    }

    fun pdlData(): Personhistorikk {
        val localDateTimeDeserializer = LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
        javaTimeModule.addDeserializer(LocalDateTime::class.java, localDateTimeDeserializer)

        val objectMapper: ObjectMapper = ObjectMapper()
            .registerModules(KotlinModule(), javaTimeModule)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

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
