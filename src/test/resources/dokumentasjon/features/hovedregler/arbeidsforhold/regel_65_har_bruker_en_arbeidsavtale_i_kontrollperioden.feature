# language: no
# encoding: UTF-8

Egenskap: Regel 65: Har bruker flere arbeidsavtaler siste 12 månedene ?

  Regel: Regel 65: Har bruker flere arbeidsavtaler siste 12 månedene ?

    Scenariomal: Person som har hatt en arbeidsavtale får "Nei"
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | <Fra og med 1>  |                 | Organisasjon     | NORMALT             |

      Og følgende arbeidsavtaler i arbeidsforholdet
        | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent   | Skipsregister |
        | <Fra og med 1>  |                 | 001       | <Stillingsprosent> |               |

      Når regel "65" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2024      | 30.01.2025      | Nei                           |

      Så skal svaret være "<Svar>"
      Eksempler:
        | Fra og med 1 | Til og med 1 | Stillingsprosent | Svar |
        | 01.01.2018   |              | 100              | Nei  |


    Scenariomal: Person som har hatt flere arbeidsavtaler får "Ja"
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | <Fra og med 1>  |                 | Organisasjon     | NORMALT             |

      Og følgende arbeidsavtaler i arbeidsforholdet
        | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent     | Skipsregister |
        | <Fra og med 1>  | <Til og med 1>  | 001       | <Stillingsprosent 1> |               |
        | <Fra og med 2>  |                 | 001       | <Stillingsprosent 2> |               |

      Når regel "65" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2024      | 30.01.2025      | Nei                           |

      Så skal svaret være "<Svar>"
      Eksempler:
        | Fra og med 1 | Til og med 1 | Fra og med 2 | Til og med 2 | Stillingsprosent 1 | Stillingsprosent 2 | Svar |
        | 01.01.2018   | 01.08.2023   | 01.08.2023   |              | 100                | 100                | Ja   |
