# language: no
# encoding: UTF-8
Egenskap: Regel 66: Har bruker vært sammenhengende i minst 60 % stilling i èn arbeidsavtale de siste 12 månedene? ?

  Regel: Regel 66: Har bruker vært sammenhengende i minst 60 % stilling i èn arbeidsavtale de siste 12 månedene? ?

    Scenariomal: Sammenhengende arbeidsavtaler med minst 60% stilling får "Ja"
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | 01.01.2020      |                 | Organisasjon     | NORMALT             |

      Og følgende arbeidsavtaler i arbeidsforholdet
        | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent     | Skipsregister |
        | <Fra og med 1>  | <Til og med 1>  | 001       | <Stillingsprosent 1> |               |
        | <Fra og med 2>  | <Til og med 2>  | 001       | <Stillingsprosent 2> |               |

      Når regel "66" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2024      | 30.01.2025      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med 1 | Til og med 1 | Fra og med 2 | Til og med 2 | Stillingsprosent 1 | Stillingsprosent 2 | Svar | Kommentar |
        | 01.02.2023   | 01.08.2023   | 01.08.2023   |              | 100                | 100                | Ja   |           |
        | 01.02.2023   | 01.08.2023   | 01.09.2023   |              | 100                | 100                | Nei  |           |

    Scenariomal: Sammenhengende eller paralelle arbeidsavtaler med minst 60% stilling får "Ja"
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | 01.01.2020      |                 | Organisasjon     | NORMALT             |

      Og følgende arbeidsavtaler i arbeidsforholdet
        | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent     | Skipsregister |
        | <Fra og med 1>  | <Til og med 1>  | 001       | <Stillingsprosent 1> |               |
        | <Fra og med 2>  | <Til og med 2>  | 001       | <Stillingsprosent 2> |               |
        | <Fra og med 3>  |                 | 001       | <Stillingsprosent 3> |               |


      Når regel "66" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2024      | 30.01.2025      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med 1 | Til og med 1 | Fra og med 2 | Til og med 2 | Fra og med 3 | Til og med 3 | Stillingsprosent 1 | Stillingsprosent 2 | Stillingsprosent 3 | Svar | Kommentar |
        #| 01.02.2023   | 01.04.2023   | 01.05.2023   | 02.06.2023   | 01.02.2023   |              | 100                | 100                | 100                | Ja   |           |
        #| 01.02.2023   | 01.04.2023   | 01.05.2023   | 02.06.2023   | 01.02.2023   |              | 100                | 100                | 25                 | Nei  |           |


