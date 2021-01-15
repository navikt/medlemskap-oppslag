# language: no
# encoding: UTF-8

Egenskap: Regel 19.1: Har bruker arbeids- og oppholdstillatelse i kontrollperiode?

  Scenariomal: Regel 19.1: Har bruker arbeids- og oppholdstillatelse i kontrollperiode?

    Gitt følgende oppholdstillatelse
      | Gyldig fra og med   | Gyldig til og med   | Har tillatelse   |
      | <Gyldig fra og med> | <Gyldig til og med> | <Har tillatelse> |

    Når regel "19.1" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 23.01.2020      | 30.01.2020      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Gyldig fra og med | Gyldig til og med | Har tillatelse | Svar |
      | 01.01.2018        |                   | Ja             | Ja   |
      | 23.01.2019        |                   | Ja             | Nei  |
      | 22.01.2019        |                   | Ja             | Ja   |
      | 22.01.2019        | 22.03.2020        | Ja             | Ja   |
      |                   | 22.03.2020        | Ja             | Ja   |
      |                   | 21.03.2020        | Ja             | Nei  |
