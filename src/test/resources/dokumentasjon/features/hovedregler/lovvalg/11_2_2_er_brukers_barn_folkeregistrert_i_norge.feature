# language: no
# encoding: UTF-8

Egenskap: Regel 11.2.2: Er brukers barn folkeregistrert som bosatt i Norge?

  Scenariomal: Ja hvis folkeregistrert bosatt i Norge og eventuell postadresse og midlertidig adresse er norsk
    Gitt følgende familerelasjoner i personhistorikk fra TPS/PDL
      | Relatert persons ident | Relatert persons rolle | Min rolle for person |
      | 09069534888            | BARN                   | FAR                  |

    Og følgende personhistorikk for relaterte personer fra TPS
      | Ident       | Bosted | Postadresse   | Midlertidig adresse   | Fra og med dato | Til og med dato |
      | 09069534888 | NOR    | <Postadresse> | <Midlertidig adresse> | 18.07.2010      | 15.05.2019      |

    Når regel "11.2.2" kjøres med følgende parametre
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
