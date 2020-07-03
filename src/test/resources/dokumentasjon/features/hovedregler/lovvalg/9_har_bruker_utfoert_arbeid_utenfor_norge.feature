# language: no
# encoding: UTF-8

Egenskap: Regel 9: Har bruker utført arbeid utenfor Norge?

  Scenariomal: Regel 9: Har bruker utført arbeid utenfor Norge?

    Når regel "9" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | <Arbeid utenfor Norge>        |

    Så skal regelen gi svaret "<Svar>"

    Og skal svaret på hovedregelen være "<Svar>"

    Eksempler:
      | Arbeid utenfor Norge | Svar |
      | Ja                   | Ja   |
      | Nei                  | Nei  |
