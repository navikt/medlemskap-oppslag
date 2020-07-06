# language: no
# encoding: UTF-8

Egenskap: Regel 12: Har bruker vært i minst 25 % stilling siste 12 måneder?

  Scenario: Stilling med mindre enn 25 %
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | 001       | 20               |               |

    Når regel "12" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Nei"


  Scenario: En stilling med mer enn 25 %
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | 001       | 80               |               |

    Når regel "12" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Ja"

  Scenariomal: To stillinger hos samme arbeidsgiver
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent   | Skipsregister |
      | 01.01.2018      |                 | 001       | 10                 |               |
      | 01.01.2018      |                 | 001       | <Stillingsprosent> |               |

    Når regel "12" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Stillingsprosent | Svar |
      | 5                | Nei  |
      | 15               | Ja   |
      | 20               | Ja   |


  Scenariomal: To stillinger hos forskjellige arbeidsgivere
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforhold 1
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Og følgende arbeidsavtaler i arbeidsforhold 1
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | 001       | 10               |               |

    Og følgende arbeidsgiver i arbeidsforhold 2
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Og følgende arbeidsavtaler i arbeidsforhold 2
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent   | Skipsregister |
      | 01.01.2018      |                 | 001       | <Stillingsprosent> |               |

    Når regel "12" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Stillingsprosent | Svar |
      | 5                | Nei  |
      | 15               | Ja   |
      | 20               | Ja   |

