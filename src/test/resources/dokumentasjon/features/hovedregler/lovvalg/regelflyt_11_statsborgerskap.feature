# language: no
# encoding: UTF-8

Egenskap: Regel 11: Statsborgerskap

  Bakgrunn:
    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | Oslo    | NOR      | 01.01.2000      |                 |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | 001       | 100              |               |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

  Scenariomal: Regel 11: EØS-borgere som bor og jobber i Norge er medlem
    Gitt følgende personstatuser i personhistorikken
      | Personstatus | Fra og med dato | Til og med dato |
      | FØDR         | 01.01.2000      |                 |

    Og følgende statsborgerskap i personhistorikken
      | Landkode          | Fra og med dato | Til og med dato |
      | <Statsborgerskap> | 01.01.2000      |                 |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"
    Og skal regel "11" gi svaret "<Regel 11>"

    Eksempler:
      | Statsborgerskap | Svar     | Regel 11 |
      | NOR             | Ja       | Ja       |
      | FRA             | Ja       | Nei      |