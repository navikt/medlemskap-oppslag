# language: no
# encoding: UTF-8

Egenskap: Norsk statsborger omfattes av grunnforordningen EØS

  Scenario: Person med bare norsk statsborgerskap omfattes av grunnforordningen EØS

    Gitt følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 30.01.2000      |                 |

    Når omfattet av grunnforordningen EØS beregnes med følgende parametre
    | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
    | 15.01.2020      | 30.01.2020      | Nei                           |

    Så skal omfattet av grunnforordningen EØS være "Ja"