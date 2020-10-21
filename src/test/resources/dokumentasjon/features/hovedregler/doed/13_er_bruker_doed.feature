# language: no
# encoding: UTF-8

Egenskap: Regelflyt for regel 13

  Scenariomal: Regelflyt for regel 13

    Gitt følgende opplysninger om doedsfall fra PDL:
      | Doedsdato       |
      | <Dato>          |
      | <Dato>          |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |


    Så skal svaret være "<Medlemskap>"

    Og skal regel "13" gi svaret "<Regel 13>"


    Eksempler:
      | Dato          | Regel 13 | Medlemskap |
      | 2021-01-06    | Ja       | Ja         |
      |               | Nei      | Nei        |

