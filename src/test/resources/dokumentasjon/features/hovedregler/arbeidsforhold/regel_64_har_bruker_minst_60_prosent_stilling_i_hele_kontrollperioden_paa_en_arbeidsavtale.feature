# language: no
# encoding: UTF-8

Egenskap: Regel 64: Har bruker minst 60% stilling på en arbeidsavtale i hele kontrollperioden ?

  Regel: Regel 64: Har bruker minst 60% stilling på en arbeidsavtale i hele kontrollperioden ?

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
        | 01.01.2023 |            | 60               | Ja   |
        | 01.01.2023 |            | 50               | Nei  |

    Scenariomal: Akseptert stillingsprosent endres basert på statsborgerskapet
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | 01.01.2023      |                 | Organisasjon     | NORMALT             |

      Og følgende arbeidsavtaler i arbeidsforholdet
        | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent   | Skipsregister |
        | 01.01.2023      |                 | 001       | <Stillingsprosent> |               |

      Og følgende statsborgerskap i personhistorikken
        | Landkode   | Fra og med dato | Til og med dato |
        | <Landkode> | 01.01.1980      |                 |

      Når regel "64" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2024      | 30.01.2025      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Landkode | Stillingsprosent | Svar | Kommentar
        | NOR      | 30               | Ja   | Norsk statsborger med mer enn 25 stillingsprosent får Ja
        | NOR      | 20               | Nei  | Norsk statsborger med under 25 stillingsprosent får Nei
        | JPN      | 70               | Ja   | Tredjelandsborger med mer enn 60 stillingsprosent får Ja
        | JPN      | 50               | Nei  | Tredjelandsborger med under 60 stillingsprosent får Nei