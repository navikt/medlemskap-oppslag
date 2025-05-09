# language: no
# encoding: UTF-8

Egenskap: Regel 64: Har bruker minst 60% stilling i hele kontrollperioden ?

  Regel: Regel 64: Har bruker minst 60% stilling i hele kontrollperioden ?

    Scenariomal: Arbeidsavtale med over 60% stilling som har vart hele kontrollperioden får "Ja"
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | 01.01.2023      |                 | Organisasjon     | NORMALT             |

      Og følgende arbeidsavtaler i arbeidsforholdet
        | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent   | Skipsregister |
        | <Fra og med>    |                 | 001       | <Stillingsprosent> |               |

      Når regel "64" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2024      | 30.01.2025      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med | Til og med | Stillingsprosent | Svar |
        | 01.01.2023 |            | 100              | Ja   |
        | 01.01.2023 |            | 50               | Nei  |



    Scenariomal: Sammenhengende arbeidsavtaler med over 60% stilling i kontrollperioden får "Ja"
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | 01.01.2023      |                 | Organisasjon     | NORMALT             |

      Og følgende arbeidsavtaler i arbeidsforholdet
        | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent     | Skipsregister |
        | <Fra og med 1>  | <Til og med 1>  | 001       | <Stillingsprosent 1> |               |
        | <Fra og med 2>  |                 | 001       | <Stillingsprosent 2> |               |

      Når regel "64" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2024      | 30.01.2025      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med 1 | Til og med 1 | Fra og med 2 | Stillingsprosent 1 | Stillingsprosent 2 | Svar | Kommentar                  |
        | 01.01.2023   | 01.01.2024   | 01.01.2024   | 80                 | 100                | Ja   |                            |
        | 01.01.2023   | 01.01.2024   | 01.01.2024   | 50                 | 100                | Nei  |                            |
        | 01.01.2023   | 01.01.2024   | 01.01.2024   | 60                 | 100                | Ja   |                            |
        | 01.01.2023   | 30.11.2023   | 01.01.2024   | 100                | 100                | Nei  | Hull mellom arbeidsavtaler |


    Scenariomal: Parallelle arbeidsavtaler med over 60% stilling i kontrollperioden får "Ja"
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | 01.01.2023      |                 | Organisasjon     | NORMALT             |

      Og følgende arbeidsavtaler i arbeidsforholdet
        | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent     | Skipsregister |
        | <Fra og med 1>  |                 | 001       | <Stillingsprosent 1> |               |
        | <Fra og med 2>  |                 | 001       | <Stillingsprosent 2> |               |

      Når regel "64" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2024      | 30.01.2025      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med 1 | Til og med 1 | Fra og med 2 | Stillingsprosent 1 | Stillingsprosent 2 | Svar |
        | 01.01.2023   |              | 01.01.2023   | 50                 | 50                 | Nei  |
        | 01.01.2023   |              | 01.01.2023   | 60                 | 60                 | Ja   |