# language: no
# encoding: UTF-8

Egenskap: Regel 61: Har arbeidsforholdet vart i hele kontrollperioden ?

  Regel: Regel 61: Har arbeidsforholdet vart i hele kontrollperioden ?

    Scenariomal: Arbeidsforhold som har vart io hele kontrollperioden får "Ja"
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | <Fra og med>    | <Til og med>    | Organisasjon     | NORMALT             |


      Når regel "61" kjøres med følgende parametre
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

      Når regel "61" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2020      | 30.01.2021      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med | Til og med | Svar |
        | 01.01.2018 |            | JA   |
        | 01.12.2019 |            | Nei  |
        | 01.12.2014 | 30.01.2020 | JA   |
        | 01.12.2014 | 29.01.2020 | Nei  |

