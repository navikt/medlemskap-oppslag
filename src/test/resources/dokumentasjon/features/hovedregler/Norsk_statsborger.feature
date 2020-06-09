# language: no
# encoding: UTF-8

Egenskap: Norsk statsborger som er bosatt i Norge og jobber i Norge

  Scenario: Norsk statsborger

    Gitt følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 15.01.2020      |                 |

    Når lovvalg og medlemskap beregnes med følgende parametre
    | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
    | 15.01.2020      | 30.01.2020      | Nei                           |

    Så skal medlemskap i Folketrygden være "UAVKLART"