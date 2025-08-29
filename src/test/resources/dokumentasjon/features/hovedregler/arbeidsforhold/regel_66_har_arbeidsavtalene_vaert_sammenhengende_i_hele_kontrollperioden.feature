# language: no
# encoding: UTF-8
Egenskap: Regel 66: Har bruker vært sammenhengende i minst 60 % stilling i èn arbeidsavtale de siste 12 månedene? ?

  Regel: Regel 66: Har bruker vært sammenhengende i minst 60 % stilling i èn arbeidsavtale de siste 12 månedene? ?

    Scenariomal: Sammenhengende eller parallelle arbeidsavtaler med minst 60% stilling får "Ja"
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
        | Fra og med 1 | Til og med 1 | Fra og med 2 | Til og med 2 | Stillingsprosent 1 | Stillingsprosent 2 | Svar | Kommentar                                     |
        | 01.01.2023   | 01.08.2023   | 01.08.2023   |              | 100                | 100                | Ja   |                                               |
        | 01.01.2023   | 01.08.2023   | 01.09.2023   |              | 100                | 100                | Nei  | Bryter på kontrollperiode                     |
        | 01.01.2023   |              | 01.02.2023   |              | 20                 | 80                 | Nei  | Bryter på kontrollperiode                     |
        | 01.01.2023   |              | 01.02.2023   |              | 80                 | 20                 | Ja   |                                               |
        | 01.01.2023   |              | 01.02.2023   |              | 50                 | 50                 | Nei  | Bryter på kontrollperiode og stillingsprosent |



    Scenariomal: Sammenhengende og parallelle arbeidsavtaler med minst 60% stilling får "Ja"
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
        | Fra og med 1 | Til og med 1 | Fra og med 2 | Til og med 2 | Fra og med 3 | Til og med 3 | Stillingsprosent 1 | Stillingsprosent 2 | Stillingsprosent 3 | Svar |
        | 01.01.2023   | 01.04.2023   | 01.05.2023   | 02.06.2023   | 01.01.2023   |              | 100                | 100                | 100                | Ja   |
        | 01.01.2023   | 01.04.2023   | 01.05.2023   | 02.06.2023   | 01.01.2023   |              | 100                | 100                | 55                 | Nei  |
        | 01.01.2023   | 01.04.2023   | 01.04.2023   |              | 01.01.2023   |              | 100                | 100                | 55                 | Ja   |
        | 01.01.2023   | 01.04.2023   | 01.04.2023   | 01.06.2023   | 01.06.2023   |              | 100                | 100                | 100                | Ja   |
        | 01.01.2023   | 01.04.2023   | 01.04.2023   |              | 01.08.2023   |              | 100                | 100                | 100                | Ja   |
        | 01.01.2023   | 01.04.2023   | 01.04.2023   |              | 01.01.2023   |              | 55                 | 55                 | 55                 | Nei  |
        | 01.03.2023   | 01.04.2023   | 01.04.2023   |              | 01.01.2023   |              | 100                | 100                | 55                 | Nei  |
        | 01.03.2023   | 01.04.2023   | 01.04.2023   |              | 01.01.2023   |              | 100                | 100                | 100                | Ja   |

    Scenariomal:  får "Ja"
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | 01.01.1994      |                 | Organisasjon     | NORMALT             |

      Og følgende arbeidsavtaler i arbeidsforholdet
        | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent   | Skipsregister |
        | 01.01.1994      |                 | 001       | <Stillingsprosent> |               |

      Og følgende statsborgerskap i personhistorikken
        | Landkode   | Fra og med dato | Til og med dato |
        | <Landkode> | 01.01.1980      |                 |

      Når regel "66" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2024      | 30.01.2025      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Landkode | Stillingsprosent | Svar | kommentar
        | NOR      | 20               | Nei  | Norsk statsborger med under 25 prosent stilling får Nei
        | NOR      | 30               | Ja   | Norsk statsborger med mer enn 25 prosent stilling får Ja
        | JPN      | 70               | Ja   | Tredjelandsborger med mer enn 60 prosent stilling får Ja
        | JPN      | 50               | Nei  | Tredjelandsborger med under 60 prosent stilling får Nei
