# language: no
# encoding: UTF-8

Egenskap: Regel 60: Har bruker kun ett arbeidsforhold siste 12 mnd ?

  Regel: Regel 60: Har bruker kun ett arbeidsforhold siste 12 mnd ?

    Scenariomal: Person som har kun ett arbeidsforhold får "Ja"
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | <Fra og med>    | <Til og med>    | Organisasjon     | NORMALT             |


      Når regel "60" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2024      | 30.01.2025      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med | Til og med | Svar |
        | 01.01.2018 |            | Ja   |


    Scenariomal: Person med flere arbeidsforhold innenfor kontrollperioden får "Nei"
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | <Fra og med>    | <Til og med>    | Organisasjon     | NORMALT             |
        | <Fra og med>    | <Til og med>    | Organisasjon     | NORMALT             |

      Når regel "60" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2020      | 30.01.2021      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med | Til og med | Svar  |
        | 01.01.2018 |            | Nei   |
