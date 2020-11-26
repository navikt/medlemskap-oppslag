# language: no
# encoding: UTF-8


Egenskap: Regel 5: Har arbeidsgiver sin hovedaktivitet i Norge?

  Scenariomal: Person med arbeidsgiver med mer enn 5 ansatte får "Ja"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Antall ansatte   |
      | 1             | BEDR             | <Antall ansatte> |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 01.01.2018      |                 | 001       | 100              |

    Når regel "5" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"
    Og skal avklaringen være som definert i RegelId
    Og skal begrunnelse utfylt være "<Svar begrunnelse>"

    Eksempler:
      | Antall ansatte | Svar | Svar begrunnelse |
      | 4              | Nei  | Nei              |
      | 5              | Nei  | Nei              |
      | 6              | Ja   | Ja               |
      | 9              | Ja   | Ja               |
      |                | Nei  | Nei              |


  Scenariomal: Person med arbeidsgiver med mer enn 5 ansatte i juridisk enhet får "Ja"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Antall ansatte | Antall ansatte i juridisk enhet   |
      | 1             | BEDR             | 4              | <Antall ansatte i juridisk enhet> |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 01.01.2018      |                 | 001       | 100              |


    Når regel "5" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Antall ansatte i juridisk enhet | Svar |
      | 4                               | Nei  |
      | 5                               | Nei  |
      | 6                               | Ja   |
      | 9                               | Ja   |
      |                                 | Nei  |