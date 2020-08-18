## language: no
## encoding: UTF-8
#
#Egenskap: Regel 11.2: Har bruker ektefelle i TPS/PDL?
#
#  Scenariomal: Regel 11.2 - Hvis sivilstand er gift eller registrert partner, skal svaret være "Ja"
#    Gitt følgende sivilstand i personhistorikk fra TPS/PDL
#      | Sivilstandstype   | Gyldig fra og med dato | Relatert ved sivilstand |
#      | <Sivilstandstype> | 29.06.2015             | 10108000398             |
#
#    Og følgende personhistorikk for relaterte personer fra TPS
#      | Ident       |
#      | 10108000398 |
#
#    Når regel "11.2" kjøres med følgende parametre
#      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
#      | 30.01.2020      | 30.01.2021      | Nei                           |
#
#    Så skal svaret være "<Svar>"
#
#    Eksempler:
#      | Sivilstandstype     | Svar |
#      | GIFT                | Ja   |
#      | REGISTRERT_PARTNER  | Ja   |
#      | SKILT               | Nei  |
#      | SEPARERT            | Nei  |
#      | ENKE_ELLER_ENKEMANN | Nei  |
#
#
#  Scenario: Regel 11.2 - Gift og så skilt
#    Gitt følgende sivilstand i personhistorikk fra TPS/PDL
#      | Sivilstandstype | Gyldig fra og med dato | Gyldig til og med dato | Relatert ved sivilstand |
#      | GIFT            | 29.06.2015             | 29.06.2016             | 10108000398             |
#      | SEPARERT        | 30.06.2016             | 30.06.2016             | 10108000398             |
#      | SKILT           | 01.07.2016             |                        | 10108000398             |
#
#    Og følgende personhistorikk for relaterte personer fra TPS
#      | Ident       |
#      | 10108000398 |
#
#    Når regel "11.2" kjøres med følgende parametre
#      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
#      | 30.01.2020      | 30.01.2021      | Nei                           |
#
#    Så skal svaret være "Nei"
#
#
#  Scenario: Regel 11.2 - Sivilstatus bestemmes av sivilstatus dagen før inputperiodens fra og med dato
#    Gitt følgende sivilstand i personhistorikk fra TPS/PDL
#      | Sivilstandstype | Gyldig fra og med dato | Gyldig til og med dato | Relatert ved sivilstand |
#      | GIFT            | 29.06.2015             | 15.01.2020             | 10108000398             |
#      | SEPARERT        | 16.01.2020             |                        | 10108000398             |
#
#    Og følgende personhistorikk for relaterte personer fra TPS
#      | Ident       |
#      | 10108000398 |
#
#    Når regel "11.2" kjøres med følgende parametre
#      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
#      | 30.01.2020      | 30.01.2021      | Nei                           |
#
#    Så skal svaret være "Nei"
#
#
#  Scenario: Regel 11.2 - Fødselsnummer med fem nuller
#    Gitt følgende sivilstand i personhistorikk fra TPS/PDL
#      | Sivilstandstype | Gyldig fra og med dato | Gyldig til og med dato | Relatert ved sivilstand |
#      | GIFT            | 29.06.2015             |                        | 10108000000             |
#
#    Og følgende personhistorikk for relaterte personer fra TPS
#      | Ident       |
#      | 10108000000 |
#
#    Når regel "11.2" kjøres med følgende parametre
#      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
#      | 30.01.2020      | 30.01.2021      | Nei                           |
#
#    Så skal svaret være "Nei"
#
#  Scenario: Regel 11.2 - Bruker er gift, men "Relatert ved sivilstand" mangler
#    Gitt følgende sivilstand i personhistorikk fra TPS/PDL
#      | Sivilstandstype | Gyldig fra og med dato | Gyldig til og med dato | Relatert ved sivilstand |
#      | GIFT            | 29.06.2015             |                        |                         |
#
#    Når regel "11.2" kjøres med følgende parametre
#      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
#      | 30.01.2020      | 30.01.2021      | Nei                           |
#
#    Så skal svaret være "Nei"
