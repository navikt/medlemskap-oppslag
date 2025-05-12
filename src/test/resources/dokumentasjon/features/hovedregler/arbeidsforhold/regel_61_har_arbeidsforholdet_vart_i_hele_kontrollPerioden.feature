# language: no
# encoding: UTF-8

Egenskap: Regel 61: Har arbeidsavtale vart i hele kontrollperioden ?

  Regel: Regel 61: Har arbeidsavtale vart i hele kontrollperioden ?

    Scenariomal: Arbeidsforhold som har vart i hele kontrollperioden får "Ja"
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | <Fra og med>    | <Til og med>    | Organisasjon     | NORMALT             |

      Og følgende arbeidsavtaler i arbeidsforholdet
        | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent   | Skipsregister |
        | <Fra og med>    | <Til og med>    | 001       | <Stillingsprosent> |               |

      Når regel "61" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2024      | 30.01.2025      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med | Til og med | Stillingsprosent | Svar |
        | 01.01.2018 |            | 100              | Ja   |
        | 01.01.2018 | 01.08.2023 | 100              | Nei  |


