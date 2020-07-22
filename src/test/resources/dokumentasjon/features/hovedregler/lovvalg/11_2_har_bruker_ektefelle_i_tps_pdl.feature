# language: no
# encoding: UTF-8

Egenskap: Regel 11.2: Har bruker ektefelle i TPS/PDL?

  Scenariomal: Hvis sivilstand er gift eller registrert partner, skal svaret være "Ja"
    Gitt følgende sivilstand i personhistorikk fra TPS/PDL
      | Sivilstandstype   | Gyldig fra og med dato | Relatert ved sivilstand |
      | <Sivilstandstype> | 29.06.2015             | 23027524079             |

    Og følgende personhistorikk for relaterte personer fra TPS
      | Ident       |
      | 23027524079 |

    Når regel "11.2" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Sivilstandstype     | Svar |
      | GIFT                | Ja   |
      | REGISTRERT_PARTNER  | Ja   |
      | SKILT               | Nei  |
      | SEPARERT            | Nei  |
      | ENKE_ELLER_ENKEMANN | Nei  |
