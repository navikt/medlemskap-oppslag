# language: no
# encoding: UTF-8

Egenskap: Regelflyt for request-validering

  Bakgrunn: Norsk statsborger bosatt i Norge, med 100 % stilling i Norge
    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | Oslo    | NOR      | 01.01.2000      |                 |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 01.01.2000      |                 |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             | 1               |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 01.01.2018      |                 | 001       | 100              |


  Scenariomal: Regelflyt validering av request, gyldig request skal gi Ja på medlemskap
    Når medlemskap beregnes med følgende parametre
      | Fra og med dato   | Til og med dato | Har hatt arbeid utenfor Norge |
      | <Fra og med dato> | 30.01.2021      | Nei                           |

    Så skal svaret være "<Medlemskap>"
    Og skal regel "0.1" ikke finnes i resultatet

    Eksempler:
      | Fra og med dato | Medlemskap |
      | 30.01.2020      | Ja         |

  Scenariomal: Regelflyt validering av request, uggyldig request skal gi UAVKLART på medlemskap
    Når medlemskap beregnes med følgende parametre
      | Fra og med dato   | Til og med dato | Har hatt arbeid utenfor Norge |
      | <Fra og med dato> | 30.01.2021      | Nei                           |

    Så skal svaret være "<Medlemskap>"
    Og skal regel "0.1" gi svaret "<Regel 0.1>"

    Eksempler:
      | Fra og med dato | Regel 0.1 | Medlemskap |
      | 01.01.2014      | Nei       | UAVKLART   |