# language: no
# encoding: UTF-8
Egenskap: Regel 66: Er arbeidsavtalene sammenhengende i kontrollperioden ?

  Regel: Regel 66: Er arbeidsavtalene sammenhengende i kontrollperioden ?

    Scenariomal: Sammenhengende arbeidsavtaler i kontrollperioden får "Ja"
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | 01.01.2020      |                 | Organisasjon     | NORMALT             |

      Og følgende arbeidsavtaler i arbeidsforholdet
        | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
        | <Fra og med 1>  | <Til og med 1>  | 001       | 99               |               |
        | <Fra og med 2>  |                 | 001       | 100              |               |

      Når regel "66" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2024      | 30.01.2025      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med 1 | Til og med 1 | Fra og med 2 | Til og med 2 | Svar | Kommentar      |
        | 01.02.2023   | 01.08.2023   | 01.08.2023   |              | Ja   |                |
        | 01.02.2023   | 01.08.2023   | 01.09.2023   |              | Nei  | 1 måned mellom |
