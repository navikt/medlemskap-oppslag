# language: no
# encoding: UTF-8

Egenskap: Overstyring av regelsvar for regel 12

  Bakgrunn:

    Gitt følgende bostedsadresser i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 01.01.2000      |                 |

    Og følgende kontaktadresser i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 01.01.2000      |                 |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 01.01.2000      |                 |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

  Scenariomal: Overstyring av regel 12 - Stilling med mindre enn 25 %
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | 001       | 22               |               |

    Og med følgende regeloverstyringer
      | Regel | Svar             |
      | 12    | <Overstyrt svar> |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Medlemskap>"

    Og skal regel "12" gi svaret "<Regel 12 svar>"
    Og skal regel "12" gi begrunnelse "<Regel 12 begrunnelse>"

    Eksempler:
      | Overstyrt svar | Regel 12 svar | Regel 12 begrunnelse                                      | Medlemskap |
      |                | Nei           | Bruker har ikke jobbet 25% eller mer i løpet av perioden. | UAVKLART   |
      | Ja             | Ja            | Overstyrt svar                                            | Ja         |
      | Nei            | Nei           | Overstyrt svar                                            | UAVKLART   |
