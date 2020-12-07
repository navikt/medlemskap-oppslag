# language: no
# encoding: UTF-8

Egenskap: Regelflyt for regel 9, for norske borgere
  Bruker som er folkeregistrert (regel 10), er norsk borger (regel 11) og har jobb i Norge (regel 12)

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

  Scenariomal: Regel 9: Forskjellige svar på spørsmålet "Har hatt arbeid utenfor Norge"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | 001       | 20               |               |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge   | Ytelse   |
      | 30.01.2020      | 30.01.2021      | <Har hatt arbeid utenfor Norge> | <Ytelse> |

    Så skal svaret være "<Medlemskap>"
    Og skal regel-årsaker være "<Årsaker>"

    Og skal regel "9" gi svaret "<Regel 9>"

    Eksempler:
      | Ytelse     | Har hatt arbeid utenfor Norge | Regel 9 | Medlemskap | Årsaker |
      | DAGPENGER  | Nei                           | Nei     | Ja         |         |
      | DAGPENGER  | Ja                            | Ja      | UAVKLART   | 12      |
      | SYKEPENGER | Ja                            | Ja      | UAVKLART   | 12      |

  Scenario: Regel 9: Ytelse sykepenger, bruker som svarer nei på spørsmålet "Har hatt arbeid utenfor Norge"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | 001       | 20               |               |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge | Ytelse     |
      | 30.01.2020      | 30.01.2021      | Nei                           | SYKEPENGER |

    Så skal svaret være "Ja"
    Og skal regel-årsaker være ""
    Og skal regel "9" ikke finnes i resultatet
