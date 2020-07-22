# language: no
# encoding: UTF-8

Egenskap: Regel 11.4.1: Er brukers barn folkeregistrert som bosatt i Norge?

  Scenariomal: Er bruker med ektefelle sitt barn folkeregistrert?
    Gitt følgende familerelasjoner i personhistorikk fra TPS/PDL
      | Relatert persons ident | Relatert persons rolle | Min rolle for person |
      | 09069534888            | BARN                   | FAR                  |

    Og følgende sivilstand i personhistorikk fra TPS/PDL
      | Sivilstandstype | Gyldig fra og med dato | Relatert ved sivilstand |
      | GIFT            | 29.06.2015             | 10108000398             |

    Og følgende personhistorikk for relaterte personer fra TPS
      | Ident       | Bosted | Postadresse   | Midlertidig adresse   | Fra og med dato | Til og med dato |
      | 10108000398 | NOR    |               |                       | 18.07.2010      | 15.05.2019      |
      | 09069534888 | NOR    | <Postadresse> | <Midlertidig adresse> | 18.07.2010      | 15.05.2019      |

    Når regel "11.4.1" kjøres med følgende parametre
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
