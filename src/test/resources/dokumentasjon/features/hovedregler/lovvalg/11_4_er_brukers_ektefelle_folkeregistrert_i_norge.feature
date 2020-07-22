# language: no
# encoding: UTF-8

Egenskap: Regel 11.4: Er brukers ektefelle folkeregistrert som bosatt i Norge?

  Scenariomal: Ja hvis folkeregistrert bosatt i Norge og eventuell postadresse og midlertidig adresse er norsk
    Gitt følgende sivilstand i personhistorikk fra TPS/PDL
      | Sivilstandstype | Gyldig fra og med dato | Relatert ved sivilstand |
      | GIFT            | 29.06.2015             | 10108000398             |

    Og følgende personhistorikk for relaterte personer fra TPS
      | Ident       | Bosted | Postadresse   | Midlertidig adresse   | Fra og med dato | Til og med dato |
      | 10108000398 | NOR    | <Postadresse> | <Midlertidig adresse> | 18.07.2010      | 15.05.2019      |

    Når regel "11.4" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Postadresse | Midlertidig adresse | Svar |
      |             |                     | Ja   |
      | NOR         | NOR                 | Ja   |
      | FRA         | NOR                 | Nei  |
      | NOR         | FRA                 | Nei  |
      | FRA         | FRA                 | Nei  |
