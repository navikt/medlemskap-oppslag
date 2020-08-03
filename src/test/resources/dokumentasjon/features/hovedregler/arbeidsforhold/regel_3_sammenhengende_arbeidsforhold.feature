# language: no
# encoding: UTF-8

Egenskap: Regel 3: Har bruker sammenhengende arbeidsforhold siste 12 måneder?

  Bakgrunn:

  Scenario: Person med ett sammenhengende arbeidsforhold i hele perioden får "Ja"
    Gitt følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 01.01.2018      |                 | 001       | 80               |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Når regel "3" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Ja"

  Scenariomal: Person med to arbeidsforhold i perioden
    Gitt følgende arbeidsgiver i arbeidsforhold 1
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Og følgende arbeidsavtaler i arbeidsforhold 1
      | Fra og med dato   | Til og med dato | Yrkeskode | Stillingsprosent |
      | <Fra og med dato> | 15.03.2019      | 001       | 80               |

    Og følgende arbeidsgiver i arbeidsforhold 2
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Og følgende arbeidsavtaler i arbeidsforhold 2
      | Fra og med dato     | Til og med dato     | Yrkeskode | Stillingsprosent |
      | <Fra og med dato 2> | <Til og med dato 2> | 001       | 80               |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato     | Til og med dato     | Arbeidsgivertype | Arbeidsforholdstype |
      | <Fra og med dato>   | 15.03.2019          | Organisasjon     | NORMALT             |
      | <Fra og med dato 2> | <Til og med dato 2> | Organisasjon     | NORMALT             |

    Når regel "3" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Fra og med dato | Fra og med dato 2 | Til og med dato 2 | Svar | Kommentar                                                      |
      | 01.01.2019      | 01.02.2019        |                   | Ja   | To Overlappende arbeidsfohold                                  |
      | 01.01.2019      | 16.03.2019        |                   | Ja   | To arbeidsforhold som er sammenhengende                        |
      | 01.01.2019      | 17.03.2019        |                   | Ja   | To arbeidsforhold med en dags opphold                          |
#      | 01.01.2019      | 18.03.2019        |                   | Ja   | To arbeidsforhold med to dagers opphold         |
      | 01.01.2019      | 19.03.2019        |                   | Nei  | To arbeidsforhold med tre dagers opphold                       |
      | 01.01.2019      | 17.04.2019        |                   | Nei  | To arbeidsforhold som ikke er sammenhengende                   |
#      | 01.01.2016      | 16.03.2019        | 29.05.2019        | Nei  | To arbeidsforhold, mangler arbeidsforhold i bakkant |
      | 01.03.2019      | 16.03.2019        | 29.05.2019        | Nei  | To arbeidsforhold, mangler arbeidsforhold i forkant og bakkant |
      | 01.03.2019      | 16.03.2019        |                   | Nei  | To arbeidsforhold, mangler arbeidsforhold i forkant            |

