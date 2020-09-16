## language: no
## encoding: UTF-8
#
#Egenskap: Regel 11.3: Har bruker barn i TPS/PDL?
#
#  Scenariomal: Regel 11.3 - Hvis barnets alder er under 25 år skal svaret være "Ja"
#    Gitt følgende familerelasjoner i personhistorikk fra PDL
#      | Relatert persons ident   | Relatert persons rolle | Min rolle for person |
#      | <Relatert persons ident> | BARN                   | FAR                  |
#
#    Og følgende personhistorikk for barn fra PDL
#      | Ident                    |
#      | <Relatert persons ident> |
#
#    Når regel "11.3" kjøres med følgende parametre
#      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
#      | 30.01.2020      | 30.01.2021      | Nei                           |
#
#    Så skal svaret være "<Svar>"
#
#    Eksempler:
#      | Relatert persons ident | Svar |
#      | 25079528660            | Ja   |
#      | 09069534888            | Ja   |
#      | 26079447659            | Nei  |
#      | 01029431171            | Nei  |
#      | 10019448164            | Nei  |
#
#  Scenario: Regel 11.3 - Hvis bruker ikke har barn skal svaret være "Nei"
#    Når regel "11.3" kjøres med følgende parametre
#      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
#      | 30.01.2020      | 30.01.2021      | Nei                           |
#
#    Så skal svaret være "Nei"
