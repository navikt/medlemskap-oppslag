# language: no
# encoding: UTF-8

Egenskap: Borgere som har dobbelt statsborgerskap i hele perioden, der det ene er norsk får "Ja"

  Scenariomal: Borgere som har dobbelt statsborgerskap i hele perioden, der det ene er norsk får "Ja"

    Gitt følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 30.01.2000      |                 |
      | <Land>   | 30.01.2000      |                 |

    Når hovedregel med avklaring "Er bruker omfattet av grunnforordningen?" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge | Ytelse     |
      | 30.01.2020      | 30.01.2021      | Nei                           | SYKEPENGER |

    Så skal svaret på hovedregelen være <Svar>

    Eksempler:
      | Land | Svar |
      | POL  | "Ja" |
      | FRA  | "Ja" |
      | USA  | "Ja" |
