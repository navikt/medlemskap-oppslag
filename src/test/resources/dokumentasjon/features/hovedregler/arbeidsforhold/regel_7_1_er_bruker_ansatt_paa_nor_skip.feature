# language: no
# encoding: UTF-8


Egenskap: Regel 7.1: Er bruker ansatt på et norsk skip?

  Scenariomal: Regel 7.1 Bruker som er ansatt på et norsk skip får "Ja"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | MARITIMT            |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Antall ansatte | Konkursstatus |
      | 1             | BEDR             | 9              |               |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Skipsregister   | Stillingsprosent | Fartsområde   |
      | 01.01.2018      |                 | 001       | <Skipsregister> | 100              | <Fartsområde> |


    Når regel "7.1" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"
    Og skal avklaringen være som definert i RegelId
    Og skal begrunnelsen være som definert i RegelId

    Eksempler:
      | Skipsregister | Fartsområde | Svar |
      | NIS           |             | Nei  |
      | NOR           |             | Ja   |
      | UTL           |             | Nei  |
      |               |             | Nei  |
      | NIS           | INNENRIKS   | Ja   |
      | NIS           | UTENRIKS    | Nei  |

  Scenario: Regel 7.1 Bruker som er ansatt på et norsk skip med flere arbeidsavtaler får "Ja"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | MARITIMT            |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Antall ansatte | Konkursstatus |
      | 1             | BEDR             | 9              |               |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Skipsregister | Stillingsprosent |
      | 01.01.2018      |                 | 001       | NOR           | 100              |
      | 02.09.2018      |                 | 002       | NOR           | 100              |


    Når regel "7.1" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Ja"
    Og skal avklaringen være som definert i RegelId
    Og skal begrunnelsen være som definert i RegelId

  Scenariomal: 7.1 Bruker som er ansatt på et NIS skip med flere arbeidsavtaler
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | MARITIMT            |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Antall ansatte | Konkursstatus |
      | 1             | BEDR             | 9              |               |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Skipsregister    | Stillingsprosent | Fartsområde    |
      | 01.01.2018      |                 | 001       | <Skipsregister1> | 100              | <Fartsområde1> |
      | 02.09.2018      |                 | 002       | <Skipsregister2> | 100              | <Fartsområde2> |

    Når regel "7.1" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Fartsområde1 | Fartsområde2 | Skipsregister1 | Skipsregister2 | Svar |
      | INNENRIKS    | INNENRIKS    | NIS            | NIS            | Ja   |
      | INNENRIKS    |              | NIS            | NIS            | Nei  |
      | INNENRIKS    |              | NIS            | NOR            | Ja   |
      | INNENRIKS    | INNENRIKS    | NIS            | UTL            | Nei  |
      | UTENRIKS     | INNENRIKS    | NIS            | NIS            | Nei  |
