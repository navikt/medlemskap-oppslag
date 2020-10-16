# language: no
# encoding: UTF-8

Egenskap: Regel 14: Er bruker ansatt i staten eller i en kommune?

  Scenario: Regel 14 - Stilling med mindre enn 25 %
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte | Juridisk enhetstype |
      | 1             | STAT             | NOR      | 9              | STAT                |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | 001       | 20               |               |

    Når regel "14" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Nei"


  Scenario: Regel 14 - En stilling med mer enn 25 %
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte | Juridisk enhetstype |
      | 1             | STAT             | NOR      | 9              | STAT                |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | 001       | 80               |               |

    Når regel "14" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Ja"

  Scenario: Regel 14 - Stilling uten statlig eller kommunal juridisk enhetstype
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte | Juridisk enhetstype |
      | 1             | STAT             | NOR      | 9              | AS                  |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | 001       | 100              |               |

    Når regel "14" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Nei"


  Scenariomal: Regel 14 - Stilling med statlig eller kommunal juridisk enhetstype
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte | Juridisk enhetstype   |
      | 1             | STAT             | NOR      | 9              | <Juridisk enhetstype> |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | 001       | 100              |               |

    Når regel "14" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Ja"

    Eksempler:
      | Juridisk enhetstype |
      | STAT                |
      | KOMM                |
      | FKF                 |
      | FYLK                |
      | KF                  |
      | SF                  |


  Scenario: Regel 14 - To parallelle stillinger hos forskjellige arbeidsgivere
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforhold 1
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte | Juridisk enhetstype |
      | 1             | BEDR             | NOR      | 9              | STAT                |

    Og følgende arbeidsavtaler i arbeidsforhold 1
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | 001       | 25               |               |

    Og følgende arbeidsgiver i arbeidsforhold 2
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte | Juridisk enhetstype |
      | 1             | BEDR             | NOR      | 9              | AS                  |

    Og følgende arbeidsavtaler i arbeidsforhold 2
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | 001       | 100              |               |

    Når regel "14" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Ja"


  Scenariomal: Regel 14 - To stillinger etter hverandre siste 12 måneder hos forskjellige arbeidsgivere
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      | 30.12.2019      | Organisasjon     | NORMALT             |
      | 31.12.2019      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforhold 1
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte | Juridisk enhetstype     |
      | 1             | BEDR             | NOR      | 9              | <Juridisk enhetstype 1> |

    Og følgende arbeidsavtaler i arbeidsforhold 1
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2019      |                 | 001       | 80               |               |

    Og følgende arbeidsgiver i arbeidsforhold 2
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte | Juridisk enhetstype     |
      | 2             | BEDR             | NOR      | 9              | <Juridisk enhetstype 2> |

    Og følgende arbeidsavtaler i arbeidsforhold 2
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 31.12.2019      |                 | 001       | 20               |               |

    Når regel "14" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.03.2020      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Juridisk enhetstype 1 | Juridisk enhetstype 2 | Svar |
      | STAT                  | AS                    | Ja   |
      | AS                    | STAT                  | Nei  |
