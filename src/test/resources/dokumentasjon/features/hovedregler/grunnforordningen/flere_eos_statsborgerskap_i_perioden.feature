# language: no
# encoding: UTF-8

Egenskap: Borgere som har flere EØS statsborgerskap i perioden får "Ja"

  Scenariomal: Borgere som har flere EØS statsborgerskap i perioden får "Ja"

    Gitt følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | BEL      | 30.06.2020      | 02.12.2020      |
      | <Land>   | 20.02.2000      | 29.06.2020      |

    Når hovedregel med avklaring "Er bruker omfattet av grunnforordningen?" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret på hovedregelen være <Svar>

    Eksempler:
      | Land | Svar  |
      | POL  | "Ja"  |
      | FRA  | "Ja"  |
      | USA  | "Nei" |
