# language: no
# encoding: UTF-8

Egenskap: Regel 11.2.1: Har bruker barn i TPS/PDL?

  Scenariomal: Hvis barnets alder er under 25 år skal svaret være "Ja"
    Gitt følgende familerelasjoner i personhistorikk fra TPS/PDL
      | Relatert persons ident   | Relatert persons rolle | Min rolle for person |
      | <Relatert persons ident> | BARN                   | FAR                  |

    Og følgende personhistorikk for relaterte personer fra TPS
      | Ident                    |
      | <Relatert persons ident> |

    Når regel "11.2.1" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Relatert persons ident | Svar |
      | 18071076276            | Ja   |
      | 23027524079            | Nei  |
